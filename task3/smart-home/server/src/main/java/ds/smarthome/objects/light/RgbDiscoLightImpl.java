package ds.smarthome.objects.light;

import SmartHome.*;
import com.zeroc.Ice.Current;

public class RgbDiscoLightImpl extends LightImpl implements RgbDiscoLight {
    private String color = "FFFFFF";
    private byte rotationSpeed = 0;

    public RgbDiscoLightImpl(String name, String location, Status status) {
        super(name, location, status);
    }

    public RgbDiscoLightImpl(String name, String location) {
        super(name, location);
    }

    @Override
    public void setColor(String color, Current current) throws InvalidFormat, DeviceStateError {
        if (this.status.equals(Status.STANDBY)) {
            throw new DeviceStateError(this.name + " is in standby mode. Cannot set color.");
        }
        if (!color.matches("^[0-9A-Fa-f]{6}$")) {
            throw new InvalidFormat();
        }
        this.color = color;
    }

    @Override
    public String getColor(Current current) {
        return color;
    }

    @Override
    public byte getRotationSpeed(Current current) {
        return rotationSpeed;
    }

    @Override
    public void setRotationSpeed(byte speed, Current current) throws RotationSpeedError, DeviceStateError {
        if (speed < 0 || speed > 100) {
            throw new RotationSpeedError();
        }
        if (this.status.equals(Status.STANDBY)) {
            throw new DeviceStateError(this.name + " is in standby mode. Cannot set rotation speed.");
        }
        this.rotationSpeed = speed;
    }

    @Override
    public void startDiscoParty(String color, Current current) throws InvalidFormat{
        this.status = Status.ON;
        if (!color.matches("^[0-9A-Fa-f]{6}$")) {
            throw new InvalidFormat();
        } else {
            this.color = color;
        }
        this.rotationSpeed = 100;
    }

    @Override
    public void stopDiscoParty(Current current) throws DeviceStateError{
        this.rotationSpeed = 40;
        this.turnOff(current);
        this.color = "#FFFFFF";
    }

    @Override
    public LightDetails getLightDetails(Current current) {
        LightDetails details = new LightDetails();
        details.setColor(color);
        details.setRotationSpeed(rotationSpeed);
        return details;
    }
}
