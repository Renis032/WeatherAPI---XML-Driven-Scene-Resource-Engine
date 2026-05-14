import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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

        addGuiComponents();
    }

    private void addGuiComponents()
    {
        //TODO: extract each element to its own function
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 350, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage); 

        JLabel temperatureLabel = new JLabel("X C");
        temperatureLabel.setBounds(0, 350, 450, 54);
        temperatureLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureLabel);

        JLabel weatherConditionLabel = new JLabel("Cloudy");
        weatherConditionLabel.setBounds(0, 405, 450, 36);
        weatherConditionLabel.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionLabel);


        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        humidityImage.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityImage); 

        int test = 100;
        JLabel humidityText = new JLabel("<html> <b> Humidity</b> 100% </html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);


        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage); 

        JLabel windSpeedText = new JLabel("<html> <b> Windspeed</b> 15km/h </html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);


        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String userInput = searchTextField.getText();
                if(userInput.replaceAll("\\s", "").length() <= 0)
                {                System.out.println("0000000000000000000");

                    return;
                }

                weatherData = DataFetcher.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition) 
                {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    default:
                        break;
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
        add(searchButton);
    }

    private ImageIcon loadImage(String path)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Could not find image at path: " + path);
        return null;
    }
}