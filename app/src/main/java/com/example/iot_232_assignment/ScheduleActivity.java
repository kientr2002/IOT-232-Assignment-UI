// ScheduleActivity.java
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

public class ScheduleActivity extends AppCompatActivity implements MQTTHelper.ConnectionListener {
    private static final String API_KEY = "36f8a3d576b623a3ca7d36e8c458bc19";
    private static final String API_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final int UPDATE_INTERVAL = 60000; // 60 seconds
    private Handler handler = new Handler();
    private Runnable weatherUpdateRunnable;
    private TextView weatherTextView;
    private TextView forecastTextView;
    private TextView locationTextView;
    private ImageView weatherImageView;

    private ImageButton homeButton;
    private ImageButton scheduleButton;
    private ImageButton mixButton;
    private ImageButton backButton;

    private Button Area1;
    private Button Area2;
    private Button Area3;
    private Button newScheduleButton;

    MQTTHelper mqttHelper;
    private static final String TAG = "ScheduleActivity";

    // Ho Chi Minh City coordinates
    private static final double HCM_LATITUDE = 10.8231;
    private static final double HCM_LONGITUDE = 106.6297;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule); // Ensure this matches your layout file name

        LinearLayout NavigateBarLayout1 = findViewById(R.id.NavigateBarLayout1);
        LinearLayout NavigateBarLayout2 = findViewById(R.id.NavigateBarLayout2);
        LinearLayout NavigateBarLayout3 = findViewById(R.id.NavigateBarLayout3);
        LinearLayout NavigateBarLayout4 = findViewById(R.id.NavigateBarLayout4);

        locationTextView = findViewById(R.id.address);
        weatherTextView = findViewById(R.id.weatherTextView);
        forecastTextView = findViewById(R.id.forecastTextView);
        weatherImageView = findViewById(R.id.weatherImageView);

        LinearLayout dashboardLayout = findViewById(R.id.dashboardLayout);
        LinearLayout DashboardInfo1 = findViewById(R.id.DashboardInfo1);
        LinearLayout DashboardInfo2 = findViewById(R.id.DashboardInfo2);
        LinearLayout informationDashboard = findViewById(R.id.informationDashboard);
        LinearLayout informationDashboard2 = findViewById(R.id.informationDashboard2);



        Area1 = findViewById(R.id.Area1);
        Area2 = findViewById(R.id.Area2);
        Area3 = findViewById(R.id.Area3);
        newScheduleButton = findViewById(R.id.newSchedule);

        Area1.setBackgroundColor(Color.WHITE);
        Area2.setBackgroundColor(Color.WHITE);
        Area3.setBackgroundColor(Color.WHITE);
        newScheduleButton.setBackgroundColor(Color.WHITE);

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

        DashboardInfo1.setBackground(drawable4);
        DashboardInfo2.setBackground(drawable4);

        informationDashboard.setBackground(drawable3);
        informationDashboard2.setBackground(drawable3);

        NavigateBarLayout1.setBackground(drawable);
        NavigateBarLayout2.setBackground(drawable);
        NavigateBarLayout3.setBackground(drawable);
        NavigateBarLayout4.setBackground(drawable);

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
                // Handle button click
                Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, MixActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        Geocoder geocoder = new Geocoder(ScheduleActivity.this, Locale.getDefault());
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

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }
    private void turnOffAllButtons() {
        Area1.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area2.setBackgroundColor(Color.WHITE); // Set other buttons to red
        Area3.setBackgroundColor(Color.WHITE); // Set other buttons to red
    }
    public void onConnectionResult(boolean success) {
        if (!success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(ScheduleActivity.this)
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
