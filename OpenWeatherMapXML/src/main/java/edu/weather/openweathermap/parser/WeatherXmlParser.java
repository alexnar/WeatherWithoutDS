package edu.weather.openweathermap.parser;

import edu.weather.api.dto.Weather;
import edu.weather.logger.WeatherAppLogger;
import edu.weather.openweathermap.exception.LoadXmlFromStringException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherXmlParser {

    private static final Logger LOGGER = WeatherAppLogger.getLogger();
    private static final String LOAD_XML_FROM_STRING_EXCEPTION_MESSAGE = "Can not load xml from string." +
            " Where string is xml response of API";

    private static final String COORDINATES_XML_TAG = "coord";
    private static final String LONGITUDE_XML_TAG = "lon";
    private static final String LATITUDE_XML_TAG = "lat";
    private static final String WEATHER_MAIN_INFO_XML_TAG = "main";
    private static final String TEMPERATURE_XML_TAG = "temperature";
    private static final String CLOUDS_XML_TAG = "clouds";
    private static final String CLOUDS_NAME_XML_ATTRIBUTE = "name";
    private static final String TEMPERATURE_VALUE_XML_ATTRIBUTE = "value";
    private static final String WEATHER_DESCRIPTION_ARRAY_XML_TAG = "weather";
    private static final String WEATHER_DESCRIPTION_XML_TAG = "description";
    private static final String CITY_XML_TAG = "city";
    private static final String LOCATION_XML_TAG = "location";
    private static final String LOCATION_LATITUDE_XML_ATTRIBUTE = "latitude";
    private static final String LOCATION_LONGITUDE_XML_ATTRIBUTE = "longitude";
    private static final String FORECAST_XML_TAG = "forecast";
    private static final String TIME_XML_TAG = "time";
    private static final String TEMPERATURE_XML_ATTRIBUTE = "day";
    private static final String CLOUDS_VALUE_XML_ATTRIBUTE = "value";



    /**
     * Return weather now from XML API.
     *
     * @param weatherXml - xml String, received from API
     * @return - List of Weather DTO
     */
    public static Weather getWeatherNowFromXml(StringBuilder weatherXml) {
        String weatherJsonString = weatherXml.toString();
        Document document = null;
        try {
            document = loadXmlFromString(weatherJsonString);
        } catch (LoadXmlFromStringException e) {
            LOGGER.log(Level.WARNING, LOAD_XML_FROM_STRING_EXCEPTION_MESSAGE, e);
            System.out.println(LOAD_XML_FROM_STRING_EXCEPTION_MESSAGE);
        }
        if (document == null) {
            return new Weather.WeatherBuilder().build();
        }
        Weather weather = parseWeatherNowFromXml(document);
        return weather;
    }

    /**
     * Return weather forecast from XML API.
     *
     * @param weatherXml - xml String, received from API
     * @return - List of Weather DTO
     */
    public static List<Weather> getWeatherForecastFromXml(StringBuilder weatherXml) {
        String weatherJsonString = weatherXml.toString();
        Document document = null;
        try {
            document = loadXmlFromString(weatherJsonString);
        } catch (LoadXmlFromStringException e) {
            LOGGER.log(Level.WARNING, LOAD_XML_FROM_STRING_EXCEPTION_MESSAGE, e);
            System.out.println(LOAD_XML_FROM_STRING_EXCEPTION_MESSAGE);
        }
        if (document == null) {
            return new ArrayList<>();
        }
        parseWeatherForecastFromXml(document);
        return null;
    }

    /**
     * Parse xml document (containing now weather) to Weather DTO.
     *
     * @param document - xml Document
     * @return Weather DTO
     */
    private static Weather parseWeatherNowFromXml(Document document) {
        Element rootElement = document.getDocumentElement();
        NodeList cityElements = rootElement.getElementsByTagName(CITY_XML_TAG);
        Element cityElement = (Element) cityElements.item(0);
        Element coordinateElement = (Element) cityElement.getElementsByTagName(COORDINATES_XML_TAG).item(0);
        String latitudeStr = coordinateElement.getAttribute(LATITUDE_XML_TAG);
        double latitude = Double.parseDouble(latitudeStr);
        String longitudeStr = coordinateElement.getAttribute(LONGITUDE_XML_TAG);
        double longitude = Double.parseDouble(longitudeStr);
        Element temperatureElement = (Element) rootElement.getElementsByTagName(TEMPERATURE_XML_TAG).item(0);
        String temperatureStr = temperatureElement.getAttribute(TEMPERATURE_VALUE_XML_ATTRIBUTE);
        double temperature = Double.parseDouble(temperatureStr);
        Element cloudsElement = (Element) rootElement.getElementsByTagName(CLOUDS_XML_TAG).item(0);
        String description = cloudsElement.getAttribute(CLOUDS_NAME_XML_ATTRIBUTE);
        Weather weather = new Weather.WeatherBuilder().setLatitude(latitude).setLongitude(longitude)
                .setWeatherDescription(description).setTemperature(temperature).build();
        return weather;
    }

    /**
     * Parse xml document (containing foreacst weather) to List of Weather DTO.
     *
     * @param document - xml Document
     * @return List of Weather DTO
     */
    private static List<Weather> parseWeatherForecastFromXml(Document document) {
        Element rootElement = document.getDocumentElement();
        Element locationElement = (Element) rootElement.getElementsByTagName(LOCATION_XML_TAG).item(0);
        locationElement = (Element) locationElement.getElementsByTagName(LOCATION_XML_TAG).item(0);
        String latitudeStr = locationElement.getAttribute(LOCATION_LATITUDE_XML_ATTRIBUTE);
        double latitude = Double.parseDouble(latitudeStr);
        String longitudeStr = locationElement.getAttribute(LOCATION_LONGITUDE_XML_ATTRIBUTE);
        double longitude = Double.parseDouble(longitudeStr);
        Element forecastElement = (Element) rootElement.getElementsByTagName(FORECAST_XML_TAG).item(0);
        NodeList forecastList = forecastElement.getElementsByTagName(TIME_XML_TAG);
        List<Weather> forecastWeatherList = new ArrayList<>();
        for (int elementId = 0; elementId < forecastList.getLength(); elementId++) {
            Element forecastEntry = (Element) forecastList.item(elementId);
            Weather weather = parseForecastEntry(forecastEntry, latitude, longitude);
            forecastWeatherList.add(weather);
        }
        return forecastWeatherList;
    }

    /**
     * Parse single entry of forecast list.
     *
     * @return - Weather DTO
     */
    private static Weather parseForecastEntry(Element entryElement, double latitude, double longitude) {
        Element temperatureElement = (Element) entryElement.getElementsByTagName(TEMPERATURE_XML_TAG).item(0);
        String temperatureStr = temperatureElement.getAttribute(TEMPERATURE_XML_ATTRIBUTE);
        double temperature = Double.parseDouble(temperatureStr);
        Element cloudsElement = (Element) entryElement.getElementsByTagName(CLOUDS_XML_TAG).item(0);
        String description = cloudsElement.getAttribute(CLOUDS_VALUE_XML_ATTRIBUTE);
        Weather weather = new Weather.WeatherBuilder().setLatitude(latitude).setLongitude(longitude)
                .setWeatherDescription(description).setTemperature(temperature).build();
        return weather;
    }


    /*private static Weather getSingleWeatherFromForecast(JsonElement weatherJsonElement, double longitude, double latitude) {

        return null;
    }*/

    /**
     * Convert xml string to Document
     *
     * @param xml - xml string
     * @return - XML Document
     * @throws LoadXmlFromStringException - if can not load xml from string
     */
    private static Document loadXmlFromString(String xml) throws LoadXmlFromStringException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new LoadXmlFromStringException(e.getMessage());
        }
        InputSource inputSource = new InputSource(new StringReader(xml));
        Document document;
        try {
            document = builder.parse(inputSource);
        } catch (SAXException e) {
            throw new LoadXmlFromStringException(e.getMessage());
        } catch (IOException e) {
            throw new LoadXmlFromStringException(e.getMessage());
        }
        return document;
    }

    public static void main(String[] args) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<weatherdata><location><name>Shuzenji</name><type></type><country>JP</country><timezone></timezone><location altitude=\"0\" latitude=\"34.9667\" longitude=\"138.9333\" geobase=\"geonames\" geobaseid=\"1851632\"></location></location><credit></credit><meta><lastupdate></lastupdate><calctime>0.005</calctime><nextupdate></nextupdate></meta><sun rise=\"2017-07-03T19:35:30\" set=\"2017-07-04T10:01:47\"></sun><forecast><time day=\"2017-07-04\"><symbol number=\"502\" name=\"heavy intensity rain\" var=\"10d\"></symbol><precipitation value=\"25.86\" type=\"rain\"></precipitation><windDirection deg=\"219\" code=\"SW\" name=\"Southwest\"></windDirection><windSpeed mps=\"8.06\" name=\"Fresh Breeze\"></windSpeed><temperature day=\"300.15\" min=\"298.12\" max=\"300.15\" night=\"298.23\" eve=\"300.15\" morn=\"300.15\"></temperature><pressure unit=\"hPa\" value=\"1010.05\"></pressure><humidity value=\"91\" unit=\"%\"></humidity><clouds value=\"overcast clouds\" all=\"88\" unit=\"%\"></clouds></time><time day=\"2017-07-05\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1\" type=\"rain\"></precipitation><windDirection deg=\"243\" code=\"WSW\" name=\"West-southwest\"></windDirection><windSpeed mps=\"2.12\" name=\"Light breeze\"></windSpeed><temperature day=\"297.59\" min=\"295.22\" max=\"297.86\" night=\"295.22\" eve=\"296.78\" morn=\"297.06\"></temperature><pressure unit=\"hPa\" value=\"1012.82\"></pressure><humidity value=\"92\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"48\" unit=\"%\"></clouds></time><time day=\"2017-07-06\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1.53\" type=\"rain\"></precipitation><windDirection deg=\"61\" code=\"ENE\" name=\"East-northeast\"></windDirection><windSpeed mps=\"1.96\" name=\"Light breeze\"></windSpeed><temperature day=\"296.58\" min=\"294.95\" max=\"296.81\" night=\"295.62\" eve=\"296.51\" morn=\"294.95\"></temperature><pressure unit=\"hPa\" value=\"1017.06\"></pressure><humidity value=\"97\" unit=\"%\"></humidity><clouds value=\"few clouds\" all=\"24\" unit=\"%\"></clouds></time><time day=\"2017-07-07\"><symbol number=\"501\" name=\"moderate rain\" var=\"10d\"></symbol><precipitation value=\"5.05\" type=\"rain\"></precipitation><windDirection deg=\"75\" code=\"ENE\" name=\"East-northeast\"></windDirection><windSpeed mps=\"2.21\" name=\"Light breeze\"></windSpeed><temperature day=\"295.9\" min=\"294.25\" max=\"296.81\" night=\"294.69\" eve=\"296.81\" morn=\"294.25\"></temperature><pressure unit=\"hPa\" value=\"996.68\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"37\" unit=\"%\"></clouds></time><time day=\"2017-07-08\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"0.4\" type=\"rain\"></precipitation><windDirection deg=\"88\" code=\"E\" name=\"East\"></windDirection><windSpeed mps=\"0.81\" name=\"Calm\"></windSpeed><temperature day=\"296.8\" min=\"293.5\" max=\"298.47\" night=\"295.33\" eve=\"298.47\" morn=\"293.5\"></temperature><pressure unit=\"hPa\" value=\"996.65\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"clear sky\" all=\"9\" unit=\"%\"></clouds></time><time day=\"2017-07-09\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"0.45\" type=\"rain\"></precipitation><windDirection deg=\"158\" code=\"SSE\" name=\"South-southeast\"></windDirection><windSpeed mps=\"0.49\" name=\"Calm\"></windSpeed><temperature day=\"297.94\" min=\"293.97\" max=\"298.68\" night=\"295.93\" eve=\"298.68\" morn=\"293.97\"></temperature><pressure unit=\"hPa\" value=\"995\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"clear sky\" all=\"9\" unit=\"%\"></clouds></time><time day=\"2017-07-10\"><symbol number=\"502\" name=\"heavy intensity rain\" var=\"10d\"></symbol><precipitation value=\"22.63\" type=\"rain\"></precipitation><windDirection deg=\"133\" code=\"SE\" name=\"SouthEast\"></windDirection><windSpeed mps=\"1.11\" name=\"Calm\"></windSpeed><temperature day=\"296.95\" min=\"294.92\" max=\"296.95\" night=\"295.52\" eve=\"296.64\" morn=\"294.92\"></temperature><pressure unit=\"hPa\" value=\"992.51\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"41\" unit=\"%\"></clouds></time><time day=\"2017-07-11\"><symbol number=\"501\" name=\"moderate rain\" var=\"10d\"></symbol><precipitation value=\"6.52\" type=\"rain\"></precipitation><windDirection deg=\"40\" code=\"NE\" name=\"NorthEast\"></windDirection><windSpeed mps=\"2.6\" name=\"Light breeze\"></windSpeed><temperature day=\"296.53\" min=\"295\" max=\"297.01\" night=\"295.48\" eve=\"297.01\" morn=\"295\"></temperature><pressure unit=\"hPa\" value=\"994.2\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"26\" unit=\"%\"></clouds></time><time day=\"2017-07-12\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"2.25\" type=\"rain\"></precipitation><windDirection deg=\"80\" code=\"E\" name=\"East\"></windDirection><windSpeed mps=\"1.42\" name=\"Calm\"></windSpeed><temperature day=\"296.77\" min=\"294.73\" max=\"297.95\" night=\"295.83\" eve=\"297.95\" morn=\"294.73\"></temperature><pressure unit=\"hPa\" value=\"995.9\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"few clouds\" all=\"19\" unit=\"%\"></clouds></time><time day=\"2017-07-13\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1.91\" type=\"rain\"></precipitation><windDirection deg=\"211\" code=\"SSW\" name=\"South-southwest\"></windDirection><windSpeed mps=\"1.01\" name=\"Calm\"></windSpeed><temperature day=\"297.87\" min=\"295.06\" max=\"298.55\" night=\"296.42\" eve=\"298.55\" morn=\"295.06\"></temperature><pressure unit=\"hPa\" value=\"994.43\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"31\" unit=\"%\"></clouds></time><time day=\"2017-07-14\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1.05\" type=\"rain\"></precipitation><windDirection deg=\"200\" code=\"SSW\" name=\"South-southwest\"></windDirection><windSpeed mps=\"1.43\" name=\"Calm\"></windSpeed><temperature day=\"298.72\" min=\"295.36\" max=\"300.66\" night=\"298\" eve=\"300.66\" morn=\"295.36\"></temperature><pressure unit=\"hPa\" value=\"992.17\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"clear sky\" all=\"5\" unit=\"%\"></clouds></time><time day=\"2017-07-15\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"2.55\" type=\"rain\"></precipitation><windDirection deg=\"264\" code=\"W\" name=\"West\"></windDirection><windSpeed mps=\"2.45\" name=\"Light breeze\"></windSpeed><temperature day=\"299.76\" min=\"296.5\" max=\"301.62\" night=\"298.91\" eve=\"301.62\" morn=\"296.5\"></temperature><pressure unit=\"hPa\" value=\"992.86\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"few clouds\" all=\"12\" unit=\"%\"></clouds></time><time day=\"2017-07-16\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1.65\" type=\"rain\"></precipitation><windDirection deg=\"273\" code=\"W\" name=\"West\"></windDirection><windSpeed mps=\"2.83\" name=\"Light breeze\"></windSpeed><temperature day=\"299.75\" min=\"297.38\" max=\"301.86\" night=\"299.19\" eve=\"301.86\" morn=\"297.38\"></temperature><pressure unit=\"hPa\" value=\"992.87\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"clear sky\" all=\"2\" unit=\"%\"></clouds></time><time day=\"2017-07-17\"><symbol number=\"501\" name=\"moderate rain\" var=\"10d\"></symbol><precipitation value=\"6.58\" type=\"rain\"></precipitation><windDirection deg=\"22\" code=\"NNE\" name=\"North-northeast\"></windDirection><windSpeed mps=\"1.36\" name=\"Calm\"></windSpeed><temperature day=\"299.61\" min=\"297.17\" max=\"300.18\" night=\"297.41\" eve=\"300.18\" morn=\"297.17\"></temperature><pressure unit=\"hPa\" value=\"994.29\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"few clouds\" all=\"12\" unit=\"%\"></clouds></time><time day=\"2017-07-18\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"1.72\" type=\"rain\"></precipitation><windDirection deg=\"300\" code=\"WNW\" name=\"West-northwest\"></windDirection><windSpeed mps=\"1.21\" name=\"Calm\"></windSpeed><temperature day=\"298.73\" min=\"296.43\" max=\"300.74\" night=\"298.24\" eve=\"300.74\" morn=\"296.43\"></temperature><pressure unit=\"hPa\" value=\"992.46\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"clear sky\" all=\"1\" unit=\"%\"></clouds></time><time day=\"2017-07-19\"><symbol number=\"500\" name=\"light rain\" var=\"10d\"></symbol><precipitation value=\"0.35\" type=\"rain\"></precipitation><windDirection deg=\"236\" code=\"SW\" name=\"Southwest\"></windDirection><windSpeed mps=\"1.02\" name=\"Calm\"></windSpeed><temperature day=\"299.74\" min=\"297.29\" max=\"299.74\" night=\"299.74\" eve=\"299.74\" morn=\"297.29\"></temperature><pressure unit=\"hPa\" value=\"994.53\"></pressure><humidity value=\"0\" unit=\"%\"></humidity><clouds value=\"scattered clouds\" all=\"29\" unit=\"%\"></clouds></time></forecast></weatherdata>";
        getWeatherForecastFromXml(new StringBuilder(xml));
    }
}
