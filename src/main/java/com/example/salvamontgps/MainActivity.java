package com.example.salvamontgps;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    Button btnBind, btnLocation, getCall, cancelCall;
    TextView textView;
    private static final String DESTINATION_NUMBER = "0727465965";
    private static final String LOCATION = "999";
    private static final String CALL = "333";
    private static final String CANCEL = "555";
    private static final String BIND = "000#0735187278#";
    private static final int MY_PERMISSION_REQUEST_RECEIVE_SMS = 0;
    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBind = findViewById(R.id.btnBind);
        btnLocation = findViewById(R.id.btnLocation);
        getCall = findViewById(R.id.getCall);
        cancelCall = findViewById(R.id.cancelCall);
        textView = findViewById(R.id.textView);

        receiver = new MyReceiver();
        MyReceiver.setSmsListener(msg -> {
            if (msg != null) {
                textView.setText(msg);
            } else {
                textView.setText("Error receiving message");
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS
            }, MY_PERMISSION_REQUEST_RECEIVE_SMS);
        }

        btnBind.setOnClickListener(view -> sendSMS(BIND));
        btnLocation.setOnClickListener(view -> sendSMS(LOCATION));
        getCall.setOnClickListener(view -> sendSMS(CALL));
        cancelCall.setOnClickListener(view -> sendSMS(CANCEL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_RECEIVE_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendSMS(String message) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(DESTINATION_NUMBER, null, message, null, null);
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }
    }
}
