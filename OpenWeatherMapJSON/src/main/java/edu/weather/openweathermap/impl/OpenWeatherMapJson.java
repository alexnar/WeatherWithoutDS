package edu.weather.openweathermap.impl;

import edu.weather.api.dto.Weather;
import edu.weather.api.service.WeatherApiService;
import edu.weather.datagetter.exception.DataHttpGetException;
import edu.weather.datagetter.service.DataGetterService;
import edu.weather.logger.WeatherAppLogger;
import edu.weather.openweathermap.parser.WeatherJsonParser;
import edu.weather.openweathermap.url.ApiUrl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of WeatherApiService, for
 * working with OpenWeatherMap in JSON format.
 */
public class OpenWeatherMapJson implements WeatherApiService {

    private final DataGetterService dataGetterService;

    private Logger LOGGER = WeatherAppLogger.getLogger();
    private static final String GET_DATA_ERROR = "While getting data, error occurred. " +
            "Check your connection and url you specified";


    public OpenWeatherMapJson() {
        BundleContext bundleContext = FrameworkUtil.getBundle(DataGetterService.class).getBundleContext();
        ServiceReference serviceReference = bundleContext.getServiceReference(DataGetterService.class);
        dataGetterService = (DataGetterService) bundleContext.getService(serviceReference);
    }

    @Override
    public Weather getWeatherNow() {
        String url = ApiUrl.getUrlForWeatherNow();
        StringBuilder weatherData = new StringBuilder();
        try {
            weatherData = dataGetterService.getDataFromUrl(url);
        } catch (DataHttpGetException e) {
            LOGGER.log(Level.WARNING, GET_DATA_ERROR, e);
            System.out.println(GET_DATA_ERROR);
        }
        if (weatherData.length() == 0) {
            return new Weather.WeatherBuilder().build();
        }
        Weather weather = WeatherJsonParser.getWeatherNowFromJson(weatherData);
        return weather;
    }

    @Override
    public List<Weather> getWeatherForecast() {
        String url = ApiUrl.getUrlForWeatherForecast();
        StringBuilder weatherData = new StringBuilder();
        try {
            weatherData = dataGetterService.getDataFromUrl(url);
        } catch (DataHttpGetException e) {
            LOGGER.log(Level.WARNING, GET_DATA_ERROR, e);
            System.out.println(GET_DATA_ERROR);
        }
        if (weatherData.length() == 0) {
            return new ArrayList<>();
        }
        List<Weather> weatherForecastList = WeatherJsonParser.getWeatherForecastFromJson(weatherData);
        return weatherForecastList;
    }

}
