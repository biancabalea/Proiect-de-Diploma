package com.example.salvamontgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    private static SmsListener listener;

    public MyReceiver() {
    }

    public static void setSmsListener(SmsListener smsListener) {
        listener = smsListener;
    }

    public interface SmsListener {
        void onTextReceived(String msg);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent Received: " + intent.getAction());
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle dataBundle = intent.getExtras();
            if (dataBundle != null) {
                Object[] mypdu = (Object[]) dataBundle.get("pdus");
                if (mypdu != null) {
                    final SmsMessage[] message = new SmsMessage[mypdu.length];
                    StringBuilder msgBuilder = new StringBuilder();
                    for (int i = 0; i < mypdu.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = dataBundle.getString("format");
                            message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
                        } else {
                            message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                        }
                        msgBuilder.append(message[i].getMessageBody());
                    }
                    String msg = msgBuilder.toString().trim();
                    Log.i(TAG, "Message received: " + msg);
                    if (listener != null) {
                        if (msg.equals("000#0735187278###")) {
                            listener.onTextReceived("Successful connection");
                        } else if (msg.equals("Voice control recording 10")) {
                            listener.onTextReceived("Calling");
                        } else {
                            listener.onTextReceived(msg);
                        }
                    }
                    if (msg.startsWith("SOS")) {
                        listener.onTextReceived("SOS Message Received: " + msg);
                    }
                }
            }
        }
    }
}
