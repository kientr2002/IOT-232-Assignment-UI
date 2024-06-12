package com.example.iot_232_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.Manifest;
import android.content.BroadcastReceiver;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddMixActivity extends AppCompatActivity implements MQTTHelper.ConnectionListener {

    private ImageButton homeButton;
    private ImageButton scheduleButton;
    private ImageButton mixButton;
    private ImageButton backButton;
    MQTTHelper mqttHelper;

    private LinearLayout container;
    private List<DataModel> dataModels;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mqttMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.myapp.MQTT_MESSAGE")) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "Received MQTT message: " + message);
                Toast.makeText(context, "MQTT message: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private MQTTHelper.AdafruitIOService adafruitIOService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmix); // Ensure this matches your layout file name

        // Initialize UI components
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

        dataModels = new ArrayList<>();

        // Example JSON data
        String jsonData1 = "{  \"id\": 1,  \"action\": \"ADD\",  \"cycle\": 5,  \"flow1\": 20,  \"flow2\": 10,  \"flow3\": 20,  \"isActive\": true,  \"area\": 1,  \"schedulerName\": \"LỊCH TƯỚI 1\",  \"startTime\": \"18:22\",  \"stopTime\": \"18:50\" }";
        String jsonData2 = "{  \"id\": 2,  \"action\": \"ADD\",  \"cycle\": 10,  \"flow1\": 25,  \"flow2\": 15,  \"flow3\": 30,  \"isActive\": false,  \"area\": 2,  \"schedulerName\": \"LỊCH TƯỚI 2\",  \"startTime\": \"19:00\",  \"stopTime\": \"19:30\" }";

        // Parse JSON data to DataModel objects
        Gson gson = new Gson();
        dataModels.add(gson.fromJson(jsonData1, DataModel.class));
        dataModels.add(gson.fromJson(jsonData2, DataModel.class));

        // Find the container layout
        container = findViewById(R.id.container);

        // Add each data model to the container
        for (DataModel dataModel : dataModels) {
            container.addView(createDataLayout(dataModel));
        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent intent = new Intent(AddMixActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMixActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMixActivity.this, MixActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startMQTT();

        // Initialize new input fields and button


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
            public void messageArrived(String topic, MqttMessage message) throws Exception {}

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    public void onConnectionResult(boolean success) {
        if (!success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(AddMixActivity.this)
                            .setTitle("Connection Error")
                            .setMessage("Failed to connect to MQTT broker. The application will now close.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
    }

    private LinearLayout createDataLayout(DataModel dataModel) {
        // Create a new LinearLayout for each data set
        LinearLayout dataLayout = new LinearLayout(this);
        dataLayout.setOrientation(LinearLayout.VERTICAL);
        dataLayout.setPadding(0, 0, 0, 32); // Add padding to separate each data set

        // Add Area TextView
        TextView areaTextView = new TextView(this);
        areaTextView.setText("Area: " + dataModel.getArea());
        dataLayout.addView(areaTextView);

        // Add Scheduler Name TextView
        TextView nameTextView = new TextView(this);
        nameTextView.setText("Name: " + dataModel.getSchedulerName());
        dataLayout.addView(nameTextView);

        // Add Status TextView
        TextView statusTextView = new TextView(this);
        statusTextView.setText("Status: " + (dataModel.isActive() ? "ON" : "OFF"));
        dataLayout.addView(statusTextView);

        // Add Time TextView
        TextView timeTextView = new TextView(this);
        timeTextView.setText("Time: " + dataModel.getStartTime() + " - " + dataModel.getStopTime());
        dataLayout.addView(timeTextView);

        // Add Cycle TextView
        TextView cycleTextView = new TextView(this);
        cycleTextView.setText("Cycle: " + dataModel.getCycle());
        dataLayout.addView(cycleTextView);

        // Add Flow1 (Nitro) TextView
        TextView nitroTextView = new TextView(this);
        nitroTextView.setText("Nitro: " + dataModel.getFlow1());
        dataLayout.addView(nitroTextView);

        // Add Flow2 (Kali) TextView
        TextView kaliTextView = new TextView(this);
        kaliTextView.setText("Kali: " + dataModel.getFlow2());
        dataLayout.addView(kaliTextView);

        // Add Flow3 (Phosphorus) TextView
        TextView phosphorusTextView = new TextView(this);
        phosphorusTextView.setText("Phosphorus: " + dataModel.getFlow3());
        dataLayout.addView(phosphorusTextView);

        // Add Update Button
        Button updateButton = new Button(this);
        updateButton.setText("Update");
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(dataModel);
            }
        });
        dataLayout.addView(updateButton);

        // Add Delete Button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(dataModel, dataLayout);
            }
        });
        dataLayout.addView(deleteButton);

        return dataLayout;
    }

    private void updateData(DataModel dataModel) {
        // Handle the update logic here
        // For example, you might want to show a dialog to edit the data
        Toast.makeText(this, "Update clicked for " + dataModel.getSchedulerName(), Toast.LENGTH_SHORT).show();
    }

    private void deleteData(DataModel dataModel, LinearLayout dataLayout) {
        // Remove the data model from the list
        dataModels.remove(dataModel);
        // Remove the layout from the container
        container.removeView(dataLayout);
        Toast.makeText(this, "Deleted " + dataModel.getSchedulerName(), Toast.LENGTH_SHORT).show();
    }

    // Data model class to hold the data
    private static class DataModel {
        private int id;
        private String action;
        private int cycle;
        private int flow1;
        private int flow2;
        private int flow3;
        private boolean isActive;
        private int area;
        private String schedulerName;
        private String startTime;
        private String stopTime;

        // Getters for all fields
        public int getId() { return id; }
        public String getAction() { return action; }
        public int getCycle() { return cycle; }
        public int getFlow1() { return flow1; }
        public int getFlow2() { return flow2; }
        public int getFlow3() { return flow3; }
        public boolean isActive() { return isActive; }
        public int getArea() { return area; }
        public String getSchedulerName() { return schedulerName; }
        public String getStartTime() { return startTime; }
        public String getStopTime() { return stopTime; }
    }


}
