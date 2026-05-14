import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataFetcher
{
    public static JSONObject getWeatherData(String location)
    {
        JSONArray locationData = getLocationData(location);

        JSONObject data = (JSONObject) locationData.get(0);
        double latitude = (double) data.get("latitude");
        double longitude = (double) data.get("longitude");

        
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m";

        try 
        {
            HttpURLConnection connection = fetchApiResponse(url);
            if(connection.getResponseCode() != 200)
            {
                System.out.println("Error: Coult not connect to server");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext())
            {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            connection.disconnect();


            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject hourly = (JSONObject) jsonObject.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findCurrentTimeIndex(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = converWeatherCode((long) weatherCode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);
 
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);

            return weatherData;
            
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }    

    private static String converWeatherCode(long weatherCode) 
    {
        String weatherCondition = "";
        if(weatherCode == 0L)
        {
            weatherCondition = "Clear";
        }
        else if(weatherCode <= 3L && weatherCode > 0L)
        {
            weatherCondition = "Cloudy";
        }
        else if((weatherCode >= 51L && weatherCode <=  67L) ||
                (weatherCode >= 80L && weatherCode <= 99L))
        {
             weatherCondition = "Rain";
        }
        else if(weatherCode >= 71L && weatherCode <= 77L)
        {
            weatherCondition = "Snow";
        }
        
        return weatherCondition;
    }

    private static int findCurrentTimeIndex(JSONArray timeList) 
    {
        String currentTime = getCurrentTime();
        for(int i = 0; i < timeList.size(); i++) 
        {
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime))
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
        
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    private static JSONArray getLocationData(String location)
    {
        location = location.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + location + "&count=10&language=en&format=json";

        try 
        {
            HttpURLConnection connection = fetchApiResponse(urlString);
            if(connection.getResponseCode() != 200)
            {
                System.out.println("Error: Coult not connect");
                return null;
            }
            else
            {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while(scanner.hasNext())
                {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                connection.disconnect();

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) jsonObject.get("results");
                return locationData;
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString)
    {
            try 
            {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.connect();
                return connection;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
    }
}
