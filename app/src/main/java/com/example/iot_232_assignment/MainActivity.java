package com.example.iot_232_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements MQTTHelper.ConnectionListener {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi, txtWifiInfo, sensor1, sensor2, sensor3;
    LabeledSwitch btnPUMP, btnLED;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout dashboardLayout = findViewById(R.id.dashboardLayout);
        LinearLayout NavigateBarLayout1 = findViewById(R.id.NavigateBarLayout1);
        LinearLayout NavigateBarLayout2 = findViewById(R.id.NavigateBarLayout2);
        LinearLayout NavigateBarLayout3 = findViewById(R.id.NavigateBarLayout3);
        LinearLayout NavigateBarLayout4 = findViewById(R.id.NavigateBarLayout4);
        LinearLayout DashboardInfo1 = findViewById(R.id.DashboardInfo1);
        LinearLayout DashboardInfo2 = findViewById(R.id.DashboardInfo2);
        LinearLayout buttonPUMP = findViewById(R.id.buttonPUMP);
        LinearLayout buttonLED = findViewById(R.id.buttonLED);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor("#f2f6db"));
        drawable.setCornerRadius(35);

        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setShape(GradientDrawable.RECTANGLE);
        drawable2.setColor(Color.WHITE);;
        drawable2.setCornerRadius(35);

        dashboardLayout.setBackground(drawable);
        NavigateBarLayout1.setBackground(drawable);
        NavigateBarLayout2.setBackground(drawable);
        NavigateBarLayout3.setBackground(drawable);
        NavigateBarLayout4.setBackground(drawable);

        DashboardInfo1.setBackground(drawable2);
        DashboardInfo2.setBackground(drawable2);
        buttonPUMP.setBackground(drawable2);
        buttonLED.setBackground(drawable2);

        btnPUMP = findViewById(R.id.btnPUMP);
        btnLED = findViewById(R.id.btnLED);
        sensor1 = findViewById(R.id.sensor1);
        sensor2 = findViewById(R.id.sensor2);
        sensor3 = findViewById(R.id.sensor3);


        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    sendDataMQTT("kientranvictory/feeds/button1", "1");
                } else {
                    sendDataMQTT("kientranvictory/feeds/button1", "0");
                }
            }
        });

        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    sendDataMQTT("kientranvictory/feeds/button2", "1");
                } else {
                    sendDataMQTT("kientranvictory/feeds/button2", "0");
                }
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
                if (topic.contains("button1")) {
                    if (message.toString().equals("1")) {
                        btnLED.setOn(true);
                    } else if (message.toString().equals("0")) {
                        btnLED.setOn(false);
                    }
                } else if (topic.contains("button2")) {
                    if (message.toString().equals("1")) {
                        btnPUMP.setOn(true);
                    } else if (message.toString().equals("0")) {
                        btnPUMP.setOn(false);
                    }
                } else if (topic.contains("sensor1")) {
                    sensor1.setText(message.toString() + "%");
                } else if (topic.contains("sensor2")) {
                    sensor2.setText(message.toString() + "%");
                } else if (topic.contains("sensor3")) {
                    sensor3.setText(message.toString() + "%");
                }
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
                    new AlertDialog.Builder(MainActivity.this)
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
}
