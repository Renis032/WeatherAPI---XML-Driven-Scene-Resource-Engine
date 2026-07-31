import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.google.gson.Gson;

public class WeatherApiClient
{
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    private final HttpClient httpClient;
    private final Gson gson;

    public WeatherApiClient()
    {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson(); 
    }

    public GeocodingResponse fetchLocation(String location) throws Exception
    {
        String encodedLocation = location.trim().replaceAll("\\s+", "+");
        String url = GEOCODING_URL + "?name=" + encodedLocation + "&count=10&language=en&format=json";

        String responseBody = getUrlResult(url);
        return gson.fromJson(responseBody, GeocodingResponse.class);
    }

    public ForecastResponse fetchForecast(double latitude, double longitude) throws Exception
    {
        String url = FORECAST_URL
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m";

        String response = getUrlResult(url);
        return gson.fromJson(response, ForecastResponse.class);
    }

    private String getUrlResult(String url) throws Exception
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        if(response.statusCode() != 200)
        {
            throw new RuntimeException("Request to " + url + " failed: " + response.statusCode());
        }

        return response.body();
    }
}
