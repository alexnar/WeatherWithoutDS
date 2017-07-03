package edu.weather.api.service;


import edu.weather.api.dto.Weather;

/**
 * WeatherApiService interface, provides methods
 * for working with weather API.
 */
public interface WeatherApiService {
    /**
     * Get weather information now
     */
    Weather getWeatherNow();

    /**
     * Get weather forecast
     */
    Weather getWeatherForecast();
}
