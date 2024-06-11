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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity implements MQTTHelper.ConnectionListener {

    private static final String API_KEY = "36f8a3d576b623a3ca7d36e8c458bc19";
    private static final String API_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final int UPDATE_INTERVAL = 60000; // 60 seconds
    private Handler handler = new Handler();
    private Runnable weatherUpdateRunnable;
    private TextView weatherTextView;
    private TextView forecastTextView;
    private TextView locationTextView;
    private ImageView weatherImageView;

    private Button Area1;
    private Button Area2;
    private Button Area3;

    private ImageButton homeButton;
    private ImageButton scheduleButton;
    private ImageButton mixButton;
    private ImageButton backButton;


    MQTTHelper mqttHelper;
    TextView sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, updated_at;
    Button btnPUMP1, btnPUMP2, btnPUMP3;
    private static final String TAG = "MainActivity";

    // Ho Chi Minh City coordinates
    private static final double HCM_LATITUDE = 10.8231;
    private static final double HCM_LONGITUDE = 106.6297;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.address);
        weatherTextView = findViewById(R.id.weatherTextView);
        forecastTextView = findViewById(R.id.forecastTextView);
        weatherImageView = findViewById(R.id.weatherImageView);



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

        homeButton  = findViewById(R.id.homeButton);
        scheduleButton = findViewById(R.id.scheduleButton);
        mixButton = findViewById(R.id.mixButton);
        backButton = findViewById(R.id.backButton);

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

        GradientDrawable drawable4 = new GradientDrawable();
        drawable4.setShape(GradientDrawable.RECTANGLE);
        drawable4.setColor(Color.WHITE);
        drawable4.setCornerRadius(35);

        GradientDrawable drawable5 = new GradientDrawable();
        drawable5.setShape(GradientDrawable.RECTANGLE);
        drawable5.setColor(Color.parseColor("#f2f6db"));
        drawable5.setCornerRadius(35);

        dashboardLayout.setBackground(drawable5);
        NavigateBarLayout1.setBackground(drawable);
        NavigateBarLayout2.setBackground(drawable);
        NavigateBarLayout3.setBackground(drawable);
        NavigateBarLayout4.setBackground(drawable);

        DashboardInfo1.setBackground(drawable4);
        DashboardInfo2.setBackground(drawable4);
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
                if (stateOfButton) {
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
                if (stateOfButton) {
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
                if (stateOfButton) {
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


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MixActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewScheduleActivity.class);
                startActivity(intent);
            }
        });

        // Initialize and start periodic weather updates
        initWeatherUpdateRunnable();
        handler.post(weatherUpdateRunnable);

        // Display location information
        displayLocationInfo(HCM_LATITUDE, HCM_LONGITUDE);

        startMQTT();
    }

    private void initWeatherUpdateRunnable() {
        weatherUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                // Fetch and update weather and forecast data
                fetchWeatherAndForecast(HCM_LATITUDE, HCM_LONGITUDE);
                // Schedule the next update
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    private void fetchWeatherAndForecast(double latitude, double longitude) {
        String weatherApiUrl = API_BASE_URL + "weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(weatherApiUrl);

        String forecastApiUrl = API_BASE_URL + "forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        FetchForecastTask forecastTask = new FetchForecastTask();
        forecastTask.execute(forecastApiUrl);
    }

    private void displayLocationInfo(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locality = address.getLocality();
                String adminArea = address.getAdminArea();
                String countryName = address.getCountryName();

                String locationText = String.format(Locale.getDefault(), " %s", adminArea);
                locationTextView.setText(locationText);
            } else {
                locationTextView.setText("Finding Country...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        if (getTag == false) {
                            switchStateOfPUMPButton(btnPUMP1);
                        }
                    } else if (message.toString().equals("0")) {
                        if (getTag == true) {
                            switchStateOfPUMPButton(btnPUMP1);
                        }
                    }
                } else if (topic.contains("button2")) {
                    boolean getTag = (boolean) btnPUMP2.getTag();
                    if (message.toString().equals("1")) {
                        if (getTag == false) {
                            switchStateOfPUMPButton(btnPUMP2);
                        }
                    } else if (message.toString().equals("0")) {
                        if (getTag == true) {
                            switchStateOfPUMPButton(btnPUMP2);
                        }
                    }
                } else if (topic.contains("sensor3")) {
                    boolean getTag = (boolean) btnPUMP3.getTag();
                    if (message.toString().equals("1")) {
                        if (getTag == false) {
                            switchStateOfPUMPButton(btnPUMP3);
                        }
                    } else if (message.toString().equals("0")) {
                        switchStateOfPUMPButton(btnPUMP3);
                    }
                } else if (topic.contains("sensor1")) {
                    sensor1.setText(message.toString() + "%");
                } else if (topic.contains("sensor2")) {
                    sensor2.setText(message.toString() + "%");
                } else if (topic.contains("sensor3")) {
                    sensor3.setText(message.toString() + "%");
                } else if (topic.contains("sensor4")) {
                    sensor4.setText(message.toString() + "°C");
                } else if (topic.contains("sensor5")) {
                    sensor5.setText(message.toString() + "°C");
                } else if (topic.contains("sensor6")) {
                    sensor6.setText(message.toString() + "°C");
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

    @Override
    protected void onResume() {
        super.onResume();
        // Start the periodic updates when the activity is resumed
        handler.post(weatherUpdateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the periodic updates when the activity is paused
        handler.removeCallbacks(weatherUpdateRunnable);
    }

    private void turnOffAllButtons() {
        Area1.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area2.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area3.setBackgroundColor(Color.WHITE); // Set other buttons to red
    }

    private void switchStateOfPUMPButton(Button button) {
        boolean stateOfButton = (boolean) button.getTag();
        if (stateOfButton) {
            button.setTag(false);
            button.setText("OFF");
            button.setBackgroundColor(Color.WHITE);
        } else {
            button.setTag(true);
            button.setText("ON");
            button.setBackgroundColor(Color.GREEN);
        }
    }

    private void sendDataToMQTT(Button button, String value) {
        if (button == btnPUMP1) {
            sendDataMQTT("kientranvictory/feeds/button1", value);
        } else if (button == btnPUMP2) {
            sendDataMQTT("kientranvictory/feeds/button2", value);
        } else if (button == btnPUMP3) {
            sendDataMQTT("kientranvictory/feeds/button3", value);
        }
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String weatherDescription = weatherObject.getString("description");

                    double celsius = temperature - 273.15;
                    String weatherText = String.format(Locale.getDefault(), " %s", weatherDescription);

// Set image drawable based on weather description
                    switch (weatherDescription) {
                        case "clear sky":
                            weatherImageView.setImageResource(R.drawable.sunny);
                            break;
                        case "few clouds":
                        case "scattered clouds":
                        case "broken clouds":
                            weatherImageView.setImageResource(R.drawable.cloudy);
                            break;
                        case "light rain":
                        case "shower rain":
                        case "rain":
                            weatherImageView.setImageResource(R.drawable.rain);
                            break;
                        case "thunderstorm":
                            weatherImageView.setImageResource(R.drawable.thunderstorm);
                            break;
                        case "snow":
                            weatherImageView.setImageResource(R.drawable.snowy);
                            break;
                        // Add more cases for other weather conditions as needed
                        default:
                            // If the weather condition is not handled, set a default image
                            weatherImageView.setImageResource(R.drawable.cloudy_sunny);
                            break;
                    }

                    weatherTextView.setText(weatherText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                weatherTextView.setText("Failed to fetch weather");
            }
        }
    }

    private class FetchForecastTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray("list");

                    StringBuilder forecastText = new StringBuilder();
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                        JSONObject item = list.getJSONObject(0);
                        long timestamp = item.getLong("dt");
                        double temperature = item.getJSONObject("main").getDouble("temp");
                        double humidity = item.getJSONObject("main").getDouble("humidity");
                        JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
                        String weatherDescription = weather.getString("description");

                        calendar.setTimeInMillis(timestamp * 1000);
                        String date = sdf.format(new Date(timestamp * 1000));
                        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":00";

                        double celsius = temperature - 273.15;
                        String forecastInfo = String.format(Locale.getDefault(), "%s",
                                date);
                        forecastText.append(forecastInfo);


                    forecastTextView.setText(forecastText.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // forecastTextView.setText("Failed to fetch forecast");
            }
        }
    }
}
