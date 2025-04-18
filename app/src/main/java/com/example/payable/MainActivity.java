package com.example.payable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.model.WeatherResponse;
import com.example.weatherapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "your_openweathermap_api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etCityName = findViewById(R.id.et_city_name);
        Button btnGetWeather = findViewById(R.id.btn_get_weather);
        TextView tvCityName = findViewById(R.id.tv_city_name);
        TextView tvTemperature = findViewById(R.id.tv_temperature);
        TextView tvWeatherCondition = findViewById(R.id.tv_weather_condition);
        TextView tvError = findViewById(R.id.tv_error);

        SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
        String lastCity = sharedPreferences.getString("last_city", null);

        if (lastCity != null) {
            fetchWeather(lastCity, tvCityName, tvTemperature, tvWeatherCondition, tvError);
        }

        btnGetWeather.setOnClickListener(v -> {
            String cityName = etCityName.getText().toString().trim();
            if (cityName.isEmpty()) {
                tvError.setText("Please enter a city name");
                tvError.setVisibility(View.VISIBLE);
            } else {
                fetchWeather(cityName, tvCityName, tvTemperature, tvWeatherCondition, tvError);
                sharedPreferences.edit().putString("last_city", cityName).apply();
            }
        });
    }

    private void fetchWeather(String city, TextView tvCityName, TextView tvTemperature, TextView tvWeatherCondition, TextView tvError) {
        RetrofitClient.getInstance().getWeather(city, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    tvCityName.setText("City: " + weather.getName());
                    tvTemperature.setText("Temperature: " + weather.getMain().getTemp() + " °C");
                    tvWeatherCondition.setText("Condition: " + weather.getWeather().get(0).getDescription());

                    tvCityName.setVisibility(View.VISIBLE);
                    tvTemperature.setVisibility(View.VISIBLE);
                    tvWeatherCondition.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                } else {
                    tvError.setText("Failed to fetch weather data");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                tvError.setText("An error occurred: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}