package ds.smarthome.objects.light;

import SmartHome.Light;
import SmartHome.Status;
import ds.smarthome.objects.DeviceImpl;

public abstract class LightImpl extends DeviceImpl implements Light {

    public LightImpl(String name, String location, Status status) {
        super(name, location, status);
    }

    public LightImpl(String name, String location) {
        super(name, location);
    }
}
