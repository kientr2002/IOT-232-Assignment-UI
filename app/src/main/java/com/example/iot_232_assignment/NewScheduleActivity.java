package com.example.iot_232_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.location.Address;
import android.location.Geocoder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewScheduleActivity extends AppCompatActivity implements MQTTHelper.ConnectionListener{
    private ImageButton homeButton;
    private ImageButton scheduleButton;
    private ImageButton mixButton;
    private ImageButton backButton;

    private EditText editTextArea;
    private EditText editTextName;
    private Switch switchStatus;
    private EditText editTextStartTime;
    private EditText editTextStopTime;
    private EditText editTextCycle;
    private EditText editTextNitro;
    private EditText editTextKali;
    private EditText editTextPhosphorus;
    private Button buttonSave;

    MQTTHelper mqttHelper;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newschedule);



        LinearLayout NavigateBarLayout1 = findViewById(R.id.NavigateBarLayout1);
        LinearLayout NavigateBarLayout2 = findViewById(R.id.NavigateBarLayout2);
        LinearLayout NavigateBarLayout3 = findViewById(R.id.NavigateBarLayout3);
        LinearLayout NavigateBarLayout4 = findViewById(R.id.NavigateBarLayout4);

        homeButton  = findViewById(R.id.homeButton);
        scheduleButton = findViewById(R.id.scheduleButton);
        mixButton = findViewById(R.id.mixButton);
        backButton = findViewById(R.id.backButton);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor("#f2f6db"));
        drawable.setCornerRadius(35);

        NavigateBarLayout1.setBackground(drawable);
        NavigateBarLayout2.setBackground(drawable);
        NavigateBarLayout3.setBackground(drawable);
        NavigateBarLayout4.setBackground(drawable);

        // Initialize views
        editTextArea = findViewById(R.id.editTextArea);
        editTextName = findViewById(R.id.editTextName);
        switchStatus = findViewById(R.id.switchStatus);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextStopTime = findViewById(R.id.editTextStopTime);
        editTextCycle = findViewById(R.id.editTextCycle);
        editTextNitro = findViewById(R.id.editTextNitro);
        editTextKali = findViewById(R.id.editTextKali);
        editTextPhosphorus = findViewById(R.id.editTextPhosphorus);
        buttonSave = findViewById(R.id.buttonSave);

        // Set click listener for the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from views
                String area = editTextArea.getText().toString();
                String name = editTextName.getText().toString();
                boolean status = switchStatus.isChecked();
                String startTime = editTextStartTime.getText().toString();
                String stopTime = editTextStopTime.getText().toString();
                String cycle = editTextCycle.getText().toString();
                String nitro = editTextNitro.getText().toString();
                String kali = editTextKali.getText().toString();
                String phosphorus = editTextPhosphorus.getText().toString();

                // Create a JSON object
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", 1); // Assuming id is constant
                    jsonObject.put("action", "ADD");
                    jsonObject.put("cycle", Integer.parseInt(cycle));
                    jsonObject.put("flow1", Integer.parseInt(nitro));
                    jsonObject.put("flow2", Integer.parseInt(kali));
                    jsonObject.put("flow3", Integer.parseInt(phosphorus));
                    jsonObject.put("isActive", status);
                    jsonObject.put("area", area);
                    jsonObject.put("schedulerName", name);
                    jsonObject.put("startTime", startTime);
                    jsonObject.put("stopTime", stopTime);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Display JSON object
                if (jsonObject.length() > 0) {
                    sendDataMQTT("kientranvictory/feeds/data", jsonObject.toString());
                } else {
                    Toast.makeText(NewScheduleActivity.this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewScheduleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewScheduleActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewScheduleActivity.this, MixActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewScheduleActivity.this, NewScheduleActivity.class);
                startActivity(intent);
            }
        });
        startMQTT();
    }

    public void sendDataMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startMQTT() {
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setConnectionListener(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Test", topic + "===" + message.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    @Override
    public void onConnectionResult(boolean success) {
        if (!success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(NewScheduleActivity.this)
                            .setTitle("Connection Error")
                            .setMessage("Failed to connect to MQTT broker. The application will now close.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startMQTT();
                                    System.exit(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
    }



}

