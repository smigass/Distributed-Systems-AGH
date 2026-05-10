package ds.smarthome;

import com.zeroc.Ice.*;
import ds.smarthome.objects.light.DeskLampImpl;
import ds.smarthome.objects.light.NormalLightImpl;
import ds.smarthome.objects.light.RgbDiscoLightImpl;
import ds.smarthome.objects.tv.TvImpl;
import ds.smarthome.objects.weather.WeatherStationImpl;

public class Server {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {

            String floor = communicator.getProperties().getProperty("App.Floor");

            String adapterName = "Floor" + floor + "Adapter";
            ObjectAdapter adapter = communicator.createObjectAdapter(adapterName);

            registerServants(adapter, floor);

            adapter.activate();

            communicator.waitForShutdown();
        }
    }

    private static void registerServants(ObjectAdapter adapter, String floor) {
        switch (floor) {
            case "1" -> {
                adapter.add(new RgbDiscoLightImpl("Disco Light", "Living room"), Util.stringToIdentity("light/living_room"));
                adapter.add(new NormalLightImpl("Light", "Kitchen"), Util.stringToIdentity("light/kitchen"));
                adapter.add(new NormalLightImpl("Light", "Bedroom"), Util.stringToIdentity("light/bedroom"));
                adapter.add(new TvImpl("Szajsung 50\" UHD", "Living room"), Util.stringToIdentity("tv/living_room"));
            }
            case "2" -> {
                adapter.add(new WeatherStationImpl("West Weather Station", "Guest room window"), Util.stringToIdentity("sensor/station_west"));
                adapter.add(new DeskLampImpl("Desk lamp", "Small room"), Util.stringToIdentity("light/small_room_desk"));
                adapter.add(new NormalLightImpl("Light", "Small room"), Util.stringToIdentity("light/small_room"));
            }
            case "3" -> {
                adapter.add(new WeatherStationImpl("East Weather Station", "Attic"), Util.stringToIdentity("sensor/station_east"));
                adapter.add(new NormalLightImpl("Light", "Office"), Util.stringToIdentity("light/office"));
                adapter.add(new DeskLampImpl("Desk lamp", "Office"), Util.stringToIdentity("light/office_desk"));
            }
        }
    }
}

