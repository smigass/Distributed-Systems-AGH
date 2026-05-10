package ds.smarthome.objects;

import SmartHome.*;
import com.zeroc.Ice.Current;

public abstract class DeviceImpl implements Device {
    protected final String name;
    protected final String location;
    protected Status status;

    protected long startTime;

    public DeviceImpl(String name, String location, Status status) {
        System.out.println("Creating new object with name: " + name + " and location: " + location);
        this.name = name;
        this.location = location;
        this.status = status;
        if (this.status.equals(Status.ON)) {
            this.startTime = System.currentTimeMillis();
        }
    }

    public DeviceImpl(String name, String location) {
        this(name, location, Status.STANDBY);
    }

    @Override
    public DeviceDetails getDetails(Current current) {
        DeviceDetails details = new DeviceDetails();
        details.name = name;
        details.location = location;
        details.status = status;
        details.uptime = getUptime(current);
        return details;
    }

    @Override
    public void turnOn(Current current) throws DeviceStateError {
        if (this.status.equals(Status.ON)) {
            throw new DeviceStateError(this.name + "Already ON");
        }
        this.status = Status.ON;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void turnOff(Current current) throws DeviceStateError{
        if (this.status.equals(Status.STANDBY)) {
            throw new DeviceStateError(this.name + "Already in standby mode");
        }
        this.status = Status.STANDBY;
    }

    @Override
    public String getUptime(Current current) {
        if (this.status.equals(Status.STANDBY)) {
            return "00H 00m 00s";
        }
        return MilsToTimeFormat(System.currentTimeMillis() - startTime);
    }


    private String MilsToTimeFormat(long time){
        long seconds = time / 1000 % 60;
        long minutes = time / (1000 * 60) % 60;
        long hours = time / (1000 * 60 * 60) % 24;
        return String.format("%02dH %02dm %02ds", hours, minutes, seconds);
    }
}
