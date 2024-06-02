package com.example.iot_232_assignment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi, txtWifiInfo;
    LabeledSwitch btnLED, btnPUMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemprature);
        txtHumi = findViewById(R.id.txtHumidity);

        btnLED = findViewById(R.id.btnLED);
        btnPUMP = findViewById(R.id.btnPUMP);

        boolean isWifiConnected = getIntent().getBooleanExtra("WIFI_CONNECTED", false);

        if (isWifiConnected) {
            // Do something if WiFi is connected
        } else {
            // Do something if WiFi is not connected
        }

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn) {
                    sendDataMQTT("kientranvictory/feeds/button1", "1");
                } else {
                    sendDataMQTT("kientranvictory/feeds/button1", "0");
                }
            }
        });

        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn) {
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
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Test", topic + "===" + message.toString());
                if(topic.contains("sensor1")) {
                    txtTemp.setText(message.toString() + "°C");
                } else if(topic.contains("sensor2")) {
                    txtHumi.setText(message.toString() + "%");
                } else if(topic.contains("button1")) {
                    if(message.toString().equals("1")) {
                        btnLED.setOn(true);
                    } else if (message.toString().equals("0")) {
                        btnLED.setOn(false);
                    }
                } else if(topic.contains("button2")) {
                    if(message.toString().equals("1")) {
                        btnPUMP.setOn(true);
                    } else if (message.toString().equals("0")) {
                        btnPUMP.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }
}
