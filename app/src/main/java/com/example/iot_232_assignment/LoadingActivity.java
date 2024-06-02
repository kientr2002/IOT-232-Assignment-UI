package com.example.iot_232_assignment;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable wifiCheckRunnable;
    private static final int CHECK_INTERVAL = 1000; // Check every 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();
        wifiCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (isWifiConnected()) {
                    navigateToMainActivity();
                } else {
                    handler.postDelayed(this, CHECK_INTERVAL);
                }
            }
        };

        // Start checking WiFi status after a delay
        handler.postDelayed(wifiCheckRunnable, CHECK_INTERVAL);
    }

    private boolean isWifiConnected() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                return true;
            }
        }
        return false;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.putExtra("WIFI_CONNECTED", true);
        startActivity(intent);
        finish();
    }
}
