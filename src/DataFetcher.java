import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataFetcher
{
    private static final WeatherApiClient API_CLIENT = new WeatherApiClient();

    public static WeatherData getWeatherData(String location)
    {
        try
        {
            GeocodingResponse geocodingResponse = API_CLIENT.fetchLocation(location);
            List<GeocodingResult> results = geocodingResponse.getResults();
            if(results == null || results.isEmpty())
            {
                System.out.println("Error: Location not found");
                return null;
            }

            GeocodingResult topResult = results.get(0);
            ForecastResponse forecast = API_CLIENT.fetchForecast(topResult.getLatitude(), topResult.getLongitude());

            HourlyData hourly = forecast.getHourly();
            int index = findCurrentTimeIndex(hourly.getTime());

            double temperature = hourly.getTemperature2m().get(index);
            String weatherCondition = convertWeatherCode(hourly.getWeatherCode().get(index));
            int humidity = hourly.getRelativeHumidity2m().get(index);
            double windSpeed = hourly.getWindSpeed10m().get(index);

            return new WeatherData(temperature, weatherCondition, humidity, windSpeed);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static String convertWeatherCode(int weatherCode)
    {
        String weatherCondition = "";
        if(weatherCode == 0)
        {
            weatherCondition = "Clear";
        }
        else if(weatherCode <= 3 && weatherCode > 0)
        {
            weatherCondition = "Cloudy";
        }
        else if((weatherCode >= 51 && weatherCode <= 67) ||
                (weatherCode >= 80 && weatherCode <= 99))
        {
             weatherCondition = "Rain";
        }
        else if(weatherCode >= 71 && weatherCode <= 77)
        {
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

    private static int findCurrentTimeIndex(List<String> timeList)
    {
        String currentTime = getCurrentTime();
        for(int i = 0; i < timeList.size(); i++)
        {
            if(timeList.get(i).equalsIgnoreCase(currentTime))
            {
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime()
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH'00'");

        return localDateTime.format(formatter);
    }
}
