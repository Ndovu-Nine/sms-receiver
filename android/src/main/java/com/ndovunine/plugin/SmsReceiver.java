package com.ndovunine.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.getcapacitor.PluginCall;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();

                    Log.d(TAG, "SMS received from: " + sender + "; Message: " + messageBody);

                    // Send the message back to the Ionic app via Capacitor
                    JSObject ret = new JSObject();
                    ret.put("sender", sender);
                    ret.put("message", messageBody);
                    SmsReceiverPlugin.notifyListeners("onSmsReceived", ret, true);
                }
            }
        }
    }
}