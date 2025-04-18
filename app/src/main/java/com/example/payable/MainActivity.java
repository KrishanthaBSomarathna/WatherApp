package com.example.payable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payable.model.WeatherResponse;
import com.example.payable.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "64b377cf24c86b1f49d6f563b2ef7f53";

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
                showErrorDialog("Please enter a city name.");
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
                    tvCityName.setText( weather.getName());
                    tvTemperature.setText(String.valueOf((int) weather.getMain().getTemp()));


                    tvWeatherCondition.setText(weather.getWeather().get(0).getDescription());

                    tvCityName.setVisibility(View.VISIBLE);
                    tvTemperature.setVisibility(View.VISIBLE);
                    tvWeatherCondition.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                } else {
                    showErrorDialog("Failed to fetch weather data. Please check the entered city.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showErrorDialog("An error occurred: " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
