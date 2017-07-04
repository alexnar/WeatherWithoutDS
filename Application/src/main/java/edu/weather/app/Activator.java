package edu.weather.app;

import edu.weather.api.service.WeatherApiService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        ServiceReference serviceReference = context.getServiceReference(WeatherApiService.class);
        WeatherApiService weatherApiService = (WeatherApiService) context.getService(serviceReference);
        System.out.println(weatherApiService);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
