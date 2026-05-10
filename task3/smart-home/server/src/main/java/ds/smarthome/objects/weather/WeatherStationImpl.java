package ds.smarthome.objects.weather;

import SmartHome.Status;
import SmartHome.WeatherCondition;
import SmartHome.WeatherData;
import SmartHome.WeatherStation;
import com.zeroc.Ice.Current;
import ds.smarthome.objects.DeviceImpl;

public class WeatherStationImpl extends DeviceImpl implements WeatherStation {
    private final WeatherData weatherData = new WeatherData(
            18f, 60f, 1000f, 5f, WeatherCondition.SUNNY
    );

    public WeatherStationImpl(String name, String location, Status status) {
        super(name, location, status);

        Thread randomDataUpdate = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                weatherData.temperature = Math.clamp(weatherData.temperature + (float)  (Math.random() - 0.5), -40.0f, 45.0f);
                weatherData.humidity = Math.clamp(weatherData.humidity + (float) (Math.random() - 0.5), 0, 100);
                weatherData.pressure =Math.clamp(weatherData.pressure + (float) (Math.random() - 0.5), 900, 1100);
                weatherData.windSpeed = Math.clamp(weatherData.windSpeed + (float) (Math.random() - 0.5), 0, 150);
                weatherData.overallCondition = updateCondition();
            }
        }, "station-updater-" + name);
        randomDataUpdate.setDaemon(true);
        randomDataUpdate.start();
    }

    public WeatherStationImpl(String name, String location) {
        this(name, location, Status.ON);
    }

    public WeatherCondition updateCondition() {
        if (weatherData.windSpeed > 50) {
            return WeatherCondition.WINDY;
        }
        if (weatherData.temperature > 30) {
            return WeatherCondition.SUNNY;
        } else if (weatherData.temperature > 20) {
            return WeatherCondition.CLOUDY;
        } else if (weatherData.temperature > 10) {
            return WeatherCondition.RAINY;
        } else {
            return WeatherCondition.SNOWY;
        }
    }

    @Override
    public WeatherData getWeatherData(Current current) {
        return weatherData;
    }
}
