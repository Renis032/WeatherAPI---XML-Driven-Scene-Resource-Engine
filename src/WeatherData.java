public class WeatherData
{
    private double temperature;
    private String weatherCondition;
    private int humidity;
    private double windSpeed;

    public WeatherData()
    {
    }

    public WeatherData(double temperature, String weatherCondition, int humidity, double windSpeed)
    {
        this.temperature = temperature;
        this.weatherCondition = weatherCondition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public void setTemperature(double temperature)
    {
        this.temperature = temperature;
    }

    public String getWeatherCondition()
    {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition)
    {
        this.weatherCondition = weatherCondition;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public void setHumidity(int humidity)
    {
        this.humidity = humidity;
    }

    public double getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed)
    {
        this.windSpeed = windSpeed;
    }
}
