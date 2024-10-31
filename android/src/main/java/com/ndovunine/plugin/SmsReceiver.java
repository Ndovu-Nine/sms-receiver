package com.ndovunine.plugin;

import android.util.Log;

public class SmsReceiver {

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }
}
