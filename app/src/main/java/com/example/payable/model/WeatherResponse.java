package com.example.payable.model;

import java.util.List;

public class WeatherResponse {
    private String name;
    private Main main;
    private List<Weather> weather;

    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public static class Main {
        private float temp;

        public float getTemp() {
            return temp;
        }
    }

    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }
    }
}