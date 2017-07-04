package edu.weather.app.impl;

import edu.weather.api.dto.Weather;
import edu.weather.api.service.WeatherApiService;
import edu.weather.app.service.WeatherAppService;
import edu.weather.filewriter.service.FileWriterService;
import edu.weather.logger.WeatherAppLogger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.xml.ws.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherAppServiceImpl implements WeatherAppService {
    private static final Logger LOGGER = WeatherAppLogger.getLogger();
    private static final String FILTER_WRONG_PARAMETER = "Filter wrong parameters";
    private static final String SERVICE_DESCRIPTION = " weather service result";

    private final BundleContext context = FrameworkUtil.getBundle(WeatherAppService.class).getBundleContext();


    @Override
    public void writeWeatherNowToFile() {
        List<ServiceReference> references = getServiceReferences();
        List<String> fileContent = new ArrayList<>();
        for (ServiceReference reference : references) {
            WeatherApiService weatherApiService = (WeatherApiService) context.getService(reference);
            fileContent.add(weatherApiService.toString() + SERVICE_DESCRIPTION);
            Weather weather = weatherApiService.getWeatherNow();
            fileContent.add(weather.toString());
        }
        writeToFile(fileContent);
    }

    @Override
    public void writeWeatherForecastToFile() {
        List<ServiceReference> weatherReferences = getServiceReferences();
        List<String> fileContent = new ArrayList<>();
        for (ServiceReference reference : weatherReferences) {
            WeatherApiService weatherApiService = (WeatherApiService) context.getService(reference);
            fileContent.add(weatherApiService.toString() + SERVICE_DESCRIPTION);
            List<Weather> weatherForecast = weatherApiService.getWeatherForecast();
            for (Weather weather : weatherForecast) {
                fileContent.add(weather.toString());
            }
        }
        writeToFile(fileContent);
    }

    /**
     * Get all WeatherApiService implementations references
     *
     * @return - List of ServiceReference
     */
    private List<ServiceReference> getServiceReferences() {
        ServiceReference[] refs = null;
        try {
            refs = context.getServiceReferences(WeatherApiService.class.getName(), null);
        } catch (InvalidSyntaxException e) {
            LOGGER.log(Level.WARNING, FILTER_WRONG_PARAMETER, e);
        }
        if (refs == null) {
            return new ArrayList<>();
        }
        List<ServiceReference> serviceReferenceList = Arrays.asList(refs);
        return serviceReferenceList;
    }

    /**
     * Write content to file
     *
     * @param fileContent - content
     */
    private void writeToFile(List<String> fileContent) {
        ServiceReference fileWriterReference = context.getServiceReference(FileWriterService.class);
        FileWriterService fileWriterService =  (FileWriterService) context.getService(fileWriterReference);
        fileWriterService.write(fileContent);
    }
}
