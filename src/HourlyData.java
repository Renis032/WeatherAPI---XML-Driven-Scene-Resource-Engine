import java.util.List;

import com.google.gson.annotations.SerializedName;

public class HourlyData
{
    private List<String> time;

    @SerializedName("temperature_2m")
    private List<Double> temperature2m;

    @SerializedName("weather_code")
    private List<Integer> weatherCode;

    @SerializedName("wind_speed_10m")
    private List<Double> windSpeed10m;

    @SerializedName("relative_humidity_2m")
    private List<Integer> relativeHumidity2m;

    public List<String> getTime()
    {
        return time;
    }

    public void setTime(List<String> time)
    {
        this.time = time;
    }

    public List<Double> getTemperature2m()
    {
        return temperature2m;
    }

    public void setTemperature2m(List<Double> temperature2m)
    {
        this.temperature2m = temperature2m;
    }

    public List<Integer> getWeatherCode()
    {
        return weatherCode;
    }

    public void setWeatherCode(List<Integer> weatherCode)
    {
        this.weatherCode = weatherCode;
    }

    public List<Double> getWindSpeed10m()
    {
        return windSpeed10m;
    }

    public void setWindSpeed10m(List<Double> windSpeed10m)
    {
        this.windSpeed10m = windSpeed10m;
    }

    public List<Integer> getRelativeHumidity2m()
    {
        return relativeHumidity2m;
    }

    public void setRelativeHumidity2m(List<Integer> relativeHumidity2m)
    {
        this.relativeHumidity2m = relativeHumidity2m;
    }
}
