package edu.weather.app;


import edu.weather.api.service.WeatherApiService;
import edu.weather.app.impl.WeatherAppServiceImpl;
import edu.weather.app.service.WeatherAppService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        Hashtable<String, Object> properties = generateProperties();
        context.registerService(WeatherAppService.class.getName(),
                new WeatherAppServiceImpl(), properties);
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
        properties.put("osgi.command.scope", "weather");
        properties.put("osgi.command.function", new String[]{"writeWeatherNowToFile", "writeWeatherForecastToFile"});
        return properties;
    }
}
