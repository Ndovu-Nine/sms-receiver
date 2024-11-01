package com.ndovunine.plugin;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "SmsReceiverPlugin",permissions = {
  @Permission(alias = "readSms", strings = {Manifest.permission.READ_SMS})
})
public class SmsReceiverPlugin extends Plugin {

  private BroadcastReceiver smsReceiver;

  private SmsReceiver implementation = new SmsReceiver();

  @Override
  public void load() {
    super.load();

    smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = sms.getDisplayOriginatingAddress();
                            String message = sms.getMessageBody();
                            long date = sms.getTimestampMillis();

                            // Create the JSObject to send back to JavaScript
                            JSObject smsData = new JSObject();
                            smsData.put("sender", sender);
                            smsData.put("message", message);
                            smsData.put("date", date);

                            // Notify listeners about the new SMS
                            notifyListeners("onSmsReceived", smsData);
                        }
                    }
                }
            }
        };

    // Register the SMS receiver if needed
    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getContext().registerReceiver(smsReceiver, filter);
  }

  @Override
    protected void handleOnDestroy() {
        // Unregister the SMS receiver when the plugin is destroyed
        getContext().unregisterReceiver(smsReceiver);
        super.handleOnDestroy();
    }

  @PluginMethod
  public void echo(PluginCall call) {
    String value = call.getString("value");

    JSObject ret = new JSObject();
    ret.put("value", implementation.echo(value));
    call.resolve(ret);
  }

  @PluginMethod
  public void readSmsMessages(PluginCall call) {
    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS)
      != PackageManager.PERMISSION_GRANTED) {
      requestPermissionForAlias("readSms", call, "permissionCallback");
      return;
    }

    // URI for SMS inbox
    Uri smsUri = Uri.parse("content://sms/inbox");
    ContentResolver contentResolver = getContext().getContentResolver();

    // Define the columns to retrieve
    String[] projection = new String[]{"address", "body", "date"};

    // Query SMS inbox
    Cursor cursor = contentResolver.query(smsUri, projection, null, null, "date DESC");

    if (cursor == null) {
      call.reject("Failed to retrieve SMS messages");
      return;
    }

    List<JSObject> smsList = new ArrayList<>();

    // Iterate through messages and add to list
    while (cursor.moveToNext()) {
      @SuppressLint("Range") String sender = cursor.getString(cursor.getColumnIndex("address"));
      @SuppressLint("Range") String message = cursor.getString(cursor.getColumnIndex("body"));
      @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex("date"));

      JSObject sms = new JSObject();
      sms.put("sender", sender);
      sms.put("message", message);
      sms.put("date", date);
      smsList.add(sms);
    }

    cursor.close();

    // Return the list of messages to JavaScript
    JSArray result = new JSArray(smsList);
    call.resolve(new JSObject().put("messages", result));
  }

  @PermissionCallback
  private void permissionCallback(PluginCall call) {
    if (getPermissionState("readSms") == PermissionState.GRANTED) {
      readSmsMessages(call);
    } else {
      call.reject("SMS read permission denied");
    }
  }
}
