import java.util.List;

public class GeocodingResponse
{
    private List<GeocodingResult> results;

    public List<GeocodingResult> getResults()
    {
        return results;
    }

    public void setResults(List<GeocodingResult> results)
    {
        this.results = results;
    }
}
