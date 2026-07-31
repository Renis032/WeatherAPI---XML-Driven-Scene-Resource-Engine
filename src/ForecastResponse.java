public class ForecastResponse
{
    private double latitude;
    private double longitude;
    private HourlyData hourly;

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public HourlyData getHourly()
    {
        return hourly;
    }

    public void setHourly(HourlyData hourly)
    {
        this.hourly = hourly;
    }
}
