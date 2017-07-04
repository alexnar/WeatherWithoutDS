package edu.weather.openweathermap;

import edu.weather.api.service.WeatherApiService;
import edu.weather.openweathermap.impl.OpenWeatherMapJson;
import edu.weather.openweathermap.url.ApiUrl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * OpenWeatherMapJson Activator.
 */
public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        Hashtable<String, Object> properties = generateProperties();
        context.registerService(WeatherApiService.class.getName(), new OpenWeatherMapJson(), properties);
        System.out.println(new OpenWeatherMapJson().getWeatherNow().toString());
    }

    @Override
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
