package com.example.iot_232_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private Button Area1;
    private Button Area2;
    private Button Area3;

    MQTTHelper mqttHelper;
    TextView sensor1, sensor2, sensor3, sensor4, sensor5, sensor6;
    Button btnPUMP1, btnPUMP2, btnPUMP3;
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
        LinearLayout buttonPUMP1 = findViewById(R.id.buttonPUMP1);
        LinearLayout buttonPUMP2 = findViewById(R.id.buttonPUMP2);
        LinearLayout buttonPUMP3 = findViewById(R.id.buttonPUMP3);
        LinearLayout informationDashboard = findViewById(R.id.informationDashboard);
        LinearLayout informationDashboard2 = findViewById(R.id.informationDashboard2);

        Area1 = findViewById(R.id.Area1);
        Area2 = findViewById(R.id.Area2);
        Area3 = findViewById(R.id.Area3);

        Area1.setBackgroundColor(Color.WHITE);
        Area2.setBackgroundColor(Color.WHITE);
        Area3.setBackgroundColor(Color.WHITE);

        btnPUMP1 = findViewById(R.id.btnPUMP1);
        btnPUMP2 = findViewById(R.id.btnPUMP2);
        btnPUMP3 = findViewById(R.id.btnPUMP3);


        btnPUMP1.setBackgroundColor(Color.WHITE);
        btnPUMP2.setBackgroundColor(Color.WHITE);
        btnPUMP3.setBackgroundColor(Color.WHITE);

        btnPUMP1.setTag(false);
        btnPUMP2.setTag(false);
        btnPUMP3.setTag(false);

        sensor1 = findViewById(R.id.sensor1);
        sensor2 = findViewById(R.id.sensor2);
        sensor3 = findViewById(R.id.sensor3);
        sensor4 = findViewById(R.id.sensor4);
        sensor5 = findViewById(R.id.sensor5);
        sensor6 = findViewById(R.id.sensor6);


        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor("#f2f6db"));
        drawable.setCornerRadius(35);

        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setShape(GradientDrawable.RECTANGLE);
        drawable2.setColor(Color.WHITE);
        drawable2.setCornerRadius(35);


        GradientDrawable drawable3 = new GradientDrawable();
        drawable3.setShape(GradientDrawable.RECTANGLE);
        drawable3.setColor(Color.WHITE);
        drawable3.setCornerRadius(35);

        dashboardLayout.setBackground(drawable);
        NavigateBarLayout1.setBackground(drawable);
        NavigateBarLayout2.setBackground(drawable);
        NavigateBarLayout3.setBackground(drawable);
        NavigateBarLayout4.setBackground(drawable);

        DashboardInfo1.setBackground(drawable2);
        DashboardInfo2.setBackground(drawable2);
        buttonPUMP1.setBackground(drawable2);
        buttonPUMP2.setBackground(drawable2);
        buttonPUMP3.setBackground(drawable2);

        informationDashboard.setBackground(drawable3);
        informationDashboard2.setBackground(drawable3);



        btnPUMP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStateOfPUMPButton(btnPUMP1);
                boolean stateOfButton = (boolean) btnPUMP1.getTag();
                if(stateOfButton){
                    sendDataToMQTT(btnPUMP1, "1");
                } else {
                    sendDataToMQTT(btnPUMP1, "0");
                }

            }
        });

        btnPUMP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStateOfPUMPButton(btnPUMP2);
                boolean stateOfButton = (boolean) btnPUMP2.getTag();
                if(stateOfButton){
                    sendDataToMQTT(btnPUMP2, "1");
                } else {
                    sendDataToMQTT(btnPUMP2, "0");
                }
            }
        });

        btnPUMP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStateOfPUMPButton(btnPUMP3);
                boolean stateOfButton = (boolean) btnPUMP3.getTag();
                if(stateOfButton){
                    sendDataToMQTT(btnPUMP3, "1");
                } else {
                    sendDataToMQTT(btnPUMP3, "0");
                }
            }
        });

        Area1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAllButtons();
                Area1.setBackgroundColor(Color.GREEN);
            }
        });

        Area2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAllButtons();
                Area2.setBackgroundColor(Color.GREEN); // Set the clicked button to green
            }
        });

        Area3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAllButtons();
                Area3.setBackgroundColor(Color.GREEN); // Set the clicked button to green
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
                    boolean getTag = (boolean) btnPUMP1.getTag();
                    if (message.toString().equals("1")) {
                        if(getTag == false){
                            switchStateOfPUMPButton(btnPUMP1);
                        }
                    } else if (message.toString().equals("0")) {
                        if(getTag == true){
                            switchStateOfPUMPButton(btnPUMP1);
                        }
                    }
                } else if (topic.contains("button2")) {
                    boolean getTag = (boolean) btnPUMP2.getTag();
                    if (message.toString().equals("1")) {
                        if(getTag == false){
                            switchStateOfPUMPButton(btnPUMP2);
                        }
                    } else if (message.toString().equals("0")) {
                        if(getTag == true){
                            switchStateOfPUMPButton(btnPUMP2);
                        }
                    }
                } else if(topic.contains("sensor3")) {
                    boolean getTag = (boolean) btnPUMP3.getTag();
                    if (message.toString().equals("1")) {
                        if(getTag == false){
                            switchStateOfPUMPButton(btnPUMP3);
                        }
                    } else if (message.toString().equals("0")) {
                            switchStateOfPUMPButton(btnPUMP3);
                        }
                    }
                else if (topic.contains("sensor1")) {
                    sensor1.setText(message.toString() + "%");
                } else if (topic.contains("sensor2")) {
                    sensor2.setText(message.toString() + "%");
                } else if (topic.contains("sensor3")) {
                    sensor3.setText(message.toString() + "%");
                } else if (topic.contains("sensor4")){
                    sensor4.setText(message.toString() + "°C");
                } else if(topic.contains("sensor5")){
                    sensor5.setText(message.toString() + "°C");
                } else if(topic.contains("sensor6")){
                    sensor6.setText(message.toString()+ "°C");
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

    private void turnOffAllButtons() {
        Area1.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area2.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area3.setBackgroundColor(Color.WHITE); // Set other buttons to red
    }

    private void setTextofInformationDashboard(){

    }

    private void switchStateOfPUMPButton(Button button){
        boolean stateOfButton = (boolean) button.getTag();
        if(stateOfButton){
            button.setTag(false);
            button.setText("OFF");
            button.setBackgroundColor(Color.WHITE);
        } else {
            button.setTag(true);
            button.setText("ON");
            button.setBackgroundColor(Color.GREEN);
        }

    }

private void sendDataToMQTT(Button button, String value){
        if(button == btnPUMP1){
            sendDataMQTT("kientranvictory/feeds/button1", value);
        } else if(button == btnPUMP2){
            sendDataMQTT("kientranvictory/feeds/button2", value);
        } else if(button == btnPUMP3){
            sendDataMQTT("kientranvictory/feeds/button3", value);
        }
}
}
