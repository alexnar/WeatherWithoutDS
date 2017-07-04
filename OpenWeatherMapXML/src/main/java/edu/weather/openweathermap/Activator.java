package edu.weather.openweathermap;

import edu.weather.api.dto.Weather;
import edu.weather.api.service.WeatherApiService;
import edu.weather.openweathermap.impl.OpenWeatherMapXml;
import edu.weather.openweathermap.url.ApiUrl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;
import java.util.List;

public class Activator implements BundleActivator {
    public void start(BundleContext context) throws Exception {
        System.out.println("TEST");
        Hashtable<String, Object> properties = generateProperties();
        context.registerService(WeatherApiService.class.getName(), new OpenWeatherMapXml(), properties);
        List<Weather> weatherList = new OpenWeatherMapXml().getWeatherForecast();
        weatherList.stream().forEach((weather) -> System.out.println(weather));
    }

    public void stop(BundleContext context) throws Exception {

    }

    /**
     * Generate properties of service.
     *
     * @return HashTable, contains properties (key, value)
     */
    private Hashtable<String, Object> generateProperties() {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put("latitude", ApiUrl.getLatitude());
        properties.put("longitude", ApiUrl.getLongitude());
        properties.put("forecast days count", ApiUrl.getForecastDatesCount());
        properties.put("api key", ApiUrl.getApiKey());
        return properties;
    }
}
