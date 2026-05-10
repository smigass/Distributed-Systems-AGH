package ds.smarthome.objects.light;

import SmartHome.*;
import com.zeroc.Ice.Current;

public class DeskLampImpl extends LightImpl implements DeskLamp {
    private ColorTemperature colorTemperature = ColorTemperature.NEUTRAL;
    private byte brightness = 50;

    public DeskLampImpl(String name, String location, Status status) {
        super(name, location, status);
    }

    public DeskLampImpl(String name, String location) {
        super(name, location);
    }

    @Override
    public void setTemperature(ColorTemperature colorTemperature, Current current) {
        this.colorTemperature = colorTemperature;
    }

    @Override
    public ColorTemperature getTemperature(Current current) {
        return colorTemperature;
    }

    @Override
    public void setBrightness(byte brightness, Current current) throws DeviceStateError, InvalidFormat {
        if (this.status.equals(Status.STANDBY)) {
            throw new DeviceStateError(this.name + " is in standby mode");
        }
        if (brightness < 0 || brightness > 100) {
            throw new InvalidFormat("Brightness must be a value between 0 and 100");
        }
        this.brightness = brightness;
    }

    @Override
    public byte getBrightness(Current current) {
        return brightness;
    }

    @Override
    public LightDetails getLightDetails(Current current) {
        LightDetails details = new LightDetails();
        details.setBrightness(brightness);
        details.setColorTemperature(colorTemperature);
        return details;
    }
}
