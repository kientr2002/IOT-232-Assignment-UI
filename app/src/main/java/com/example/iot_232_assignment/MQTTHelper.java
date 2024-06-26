package com.example.iot_232_assignment;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;
    public boolean connectResult = true;
    private Set<String> subscribedTopics = new HashSet<>();
    public final String[] arrayTopics = {"kientranvictory/feeds/sensor1", "kientranvictory/feeds/sensor2", "kientranvictory/feeds/sensor3", "kientranvictory/feeds/sensor4", "kientranvictory/feeds/sensor5", "kientranvictory/feeds/sensor6","kientranvictory/feeds/button3", "kientranvictory/feeds/button2", "kientranvictory/feeds/button1"};

    final String clientId = "26092002";
    final String username = "kientranvictory";
    final String password = ""; //them vao ada va luon luon xoa truoc khi push github


    final String serverUri = "tcp://io.adafruit.com:1883";
    public interface ConnectionListener {
        void onConnectionResult(boolean success);
    }

    private ConnectionListener connectionListener;

    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    public MQTTHelper(Context context){
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                    connectResult = false;
                    if (connectResult) {
                        Log.w("Mqtt", "ok");
                    } else {
                        Log.w("Mqtt", "not ok");
                        if (connectionListener != null) {
                            connectionListener.onConnectionResult(false);
                        }
                    }
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic() {

        for (int i = 0; i < arrayTopics.length; i++) {
            final int index = i;
            if (subscribedTopics.contains(arrayTopics[i])) {
//                Log.d(arrayTopics[i], ":Already subscribed");
                continue;
            } else {
                try {
                    mqttAndroidClient.subscribe(arrayTopics[i], 0, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
//                            Log.d(arrayTopics[index], ":Subscribed!");
                            subscribedTopics.add(arrayTopics[index]);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d(arrayTopics[index], ":Subscribed fail!");
                        }
                    });
                } catch (MqttException ex) {
                    System.err.println("Exception subscribing");
                    ex.printStackTrace();
                }
            }

        }
    }

    public interface AdafruitIOService {

        @Headers({
                "X-AIO-Key: ", // Replace with your Adafruit IO Key
                "Content-Type: application/json"
        })
        @GET("kientranvictory/feeds/data")
        Call<List<FeedData>> getFeedData(@Path("feedKey") String feedKey);
    }

    public class FeedData {
        private String value;
        private String created_at;

        public String getValue() {
            return value;
        }

        public String getCreatedAt() {
            return created_at;
        }
    }
}