package edu.weather.api.dto;

/**
 * Weather Data Transfer Object
 */
public class Weather {
    private double latitude;
    private double longitude;
    private double temperature;
    private String weatherDescription;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    /**
     * Copy constructor
     *
     * @param weather Weather object to copy
     */
    private Weather(WeatherBuilder weather) {
        this.latitude = weather.latitude;
        this.longitude = weather.longitude;
        this.temperature = weather.temperature;
        this.weatherDescription = weather.weatherDescription;
    }


    public static class WeatherBuilder {
        private double latitude;
        private double longitude;
        private double temperature;
        private String weatherDescription;

        public WeatherBuilder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public WeatherBuilder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public WeatherBuilder setTemperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public WeatherBuilder setWeatherDescription(String weatherDescription) {
            this.weatherDescription = weatherDescription;
            return this;
        }

        public Weather build() {
            return new Weather(this);
        }
    }
}
