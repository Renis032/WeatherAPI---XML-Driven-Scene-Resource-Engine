import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.*;

import org.json.simple.JSONObject;

public class Weather extends JFrame
{
    private JSONObject weatherData;

    public Weather()
    {
        super("Weather Gui");

        setSize(450, 650);
        setLocationRelativeTo(null); // center
        setLayout(null); // we configure the layout
        setResizable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ResourceLoader resourceLoader;
        GuiLayout layout;
        try
        {
            resourceLoader = ResourceLoader.load(resolveDataFile("resources.xml"));
            layout = new GuiLayout(resourceLoader);
            File layoutXml = resolveDataFile("weather_layout.xml");
            layout.loadFromXml(this, layoutXml);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to load GUI or resources", e);
        }

        JTextField searchTextField = layout.get("searchTextField", JTextField.class);
        JLabel weatherConditionImage = layout.get("weatherConditionImage", JLabel.class);
        JLabel temperatureLabel = layout.get("temperatureLabel", JLabel.class);
        JLabel weatherConditionLabel = layout.get("weatherConditionLabel", JLabel.class);
        JLabel humidityText = layout.get("humidityText", JLabel.class);
        JLabel windSpeedText = layout.get("windSpeedText", JLabel.class);

        JButton searchButton = layout.get("searchButton", JButton.class);
        searchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String userInput = searchTextField.getText();
                if(userInput.replaceAll("\\s", "").length() <= 0)
                {
                    return;
                }

                weatherData = DataFetcher.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                String weatherResourceId = "weather_" + weatherCondition.toLowerCase(Locale.ROOT);
                ImageIcon graphic = resourceLoader.tryResolve(weatherResourceId);
                if(graphic != null)
                {
                    weatherConditionImage.setIcon(graphic);
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureLabel.setText(temperature + " C");
                weatherConditionLabel.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windSpeed + "km/h</html>");
            }
        });
    }

    private static File resolveDataFile(String filename)
    {
        File fromSrc = new File("src/" + filename);
        if(fromSrc.isFile())
        {
            return fromSrc;
        }
        throw new IllegalStateException("Could not find " + filename);
    }
}
