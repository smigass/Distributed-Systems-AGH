package ds.smarthome.objects.tv;

import SmartHome.*;
import com.zeroc.Ice.Current;
import ds.smarthome.objects.DeviceImpl;

import java.util.ArrayDeque;
import java.util.Deque;

public class TvImpl extends DeviceImpl implements TV {
    private Mode mode;
    private int volume = 10;
    private int channel = 1;

    public static final byte MAX_CHANNELS = 10;

    private final Deque<Integer> recentChannels = new ArrayDeque<>(MAX_CHANNELS);

    public TvImpl(String name, String location, Mode mode) {
        super(name, location);
        this.mode = mode;
        this.recentChannels.add(1);
    }

    public TvImpl(String name, String location) {
        super(name, location);
        this.mode = Mode.NORMAL;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Mode getMode(Current current) {
        return mode;
    }

    @Override
    public String[] getRecentChannels(Current current) {
        System.out.println(recentChannels);
        return recentChannels.stream().map(channel -> TvChannelProvider.channels.get(channel)).toArray(String[]::new);
    }

    @Override
    public TVChannelInfo[] getTVChannelList(Current current) {
        return TvChannelProvider.channels.entrySet().stream()
                .map(entry -> new TVChannelInfo(entry.getKey(), entry.getValue()))
                .toArray(TVChannelInfo[]::new);
    }

    @Override
    public void setVolume(int volume, Current current) throws InvalidValue{
        if (volume < 0 || volume > 100) {
            throw new InvalidValue("Volume must be between 0 and 100");
        }
        this.volume = volume;
    }

    @Override
    public int getVolume(Current current) {
        return volume;
    }

    @Override
    public void setChannel(int channel, Current current) throws NoSuchChannel{
        if (!TvChannelProvider.channels.containsKey(channel)) {
            throw new NoSuchChannel("Channel " + channel + " does not exist");
        }
        this.channel = channel;
        this.status = Status.ON;
        if (!this.recentChannels.isEmpty()){
            if (!this.recentChannels.peek().equals(channel)) {
                if (this.recentChannels.size() == MAX_CHANNELS) {
                    this.recentChannels.removeFirst();
                }
                this.recentChannels.add(channel);
            }
        } else {
            this.recentChannels.add(channel);
        }
    }

    @Override
    public int getChannel(Current current) {
        return channel;
    }

    @Override
    public void setMode(Mode mode, Current current) {
        this.mode = mode;
    }

    @Override
    public DeviceDetails getDetails(Current current) {
        TVDetails tvDetails = new TVDetails();
        tvDetails.status = this.status;
        tvDetails.name = this.name;
        tvDetails.location = this.location;
        if (this.status.equals(Status.ON)) {
            tvDetails.channel = this.channel;
            tvDetails.mode = this.mode;
            tvDetails.volume = this.volume;
            tvDetails.uptime = this.getUptime(current);
            tvDetails.recentChannels = this.getRecentChannels(current);
        }
        return tvDetails;
    }
}
