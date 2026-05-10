module SmartHome{
    // Enums
    // Status for every device, whether it's on or off
    enum Status {STANDBY, ON};

    // Device current mode, whether it's in normal mode or energy-saving mode
    enum Mode {NORMAL, ECO};

    // Exceptions
    exception InvalidFormat {
        string reason;
    }

    exception InvalidValue {
        string reason;
    }

    exception DeviceStateError {
        string reason;
    }

    class DeviceDetails {
        string name;
        string location;
        Status status;
        string uptime;
    }


    // Device interface
    interface Device {
        DeviceDetails getDetails(); 
        void turnOn() throws DeviceStateError;
        void turnOff() throws DeviceStateError;
        string getUptime();
    }

    // Lights (3 subtypes)

    exception RotationSpeedError {
        string reason = "Rotation speed must be between 0 and 100";
    }

    enum ColorTemperature {WARM, NEUTRAL, COOL};

    class LightDetails : DeviceDetails {
        optional(1) ColorTemperature colorTemperature;
        optional(2) int brightness;
        optional(3) string color; // Only for disco light, in hex format
        optional(4) byte rotationSpeed; // Only for disco light
    }
    
    // Normal light can't change any settings.
    interface Light : Device {
        LightDetails getLightDetails();
    }

    interface NormalLight : Light {
        void setBrightness(byte brightness) throws DeviceStateError, InvalidFormat; // Brightness should be between 0 and 100
    }

    // RGB disco light can change color and rotation speed
    interface RgbDiscoLight : Light {
        void setColor(string color) throws InvalidFormat, DeviceStateError; // Color should be in hex format
        string getColor();
        byte getRotationSpeed();
        void setRotationSpeed(byte speed) throws RotationSpeedError, DeviceStateError; 
        void startDiscoParty(string color) throws InvalidFormat;
        void stopDiscoParty() throws DeviceStateError;
    }

    // Desk lamp can change color temperature and brightness;
    interface DeskLamp : Light {
        void setTemperature(ColorTemperature colorTemperature);
        ColorTemperature getTemperature();
        void setBrightness(byte brightness) throws DeviceStateError, InvalidFormat; // Brightness should be between 0 and 100
        byte getBrightness();
    }


    // TV

    struct TVChannelInfo {
        int channelNumber;
        string channelName;
    }

    dictionary<int, string> ChannelMap;

    sequence<string> RecentChannels;
    sequence<TVChannelInfo> TVChannelList;
    
    exception NoSuchChannel {
        string reason = "Channel does not exist";
    }

    class TVDetails : DeviceDetails {
        int volume;
        int channel;
        Mode mode;
        RecentChannels recentChannels;
    }


    interface TV : Device {
        void setVolume(int volume) throws DeviceStateError, InvalidValue; // Assume that we can't change volume if the TV is in standby mode
        int getVolume() throws InvalidFormat;
        void setChannel(int channel) throws NoSuchChannel; // But changing channel is allowed even in standby mode, it turns on the TV and changes the channel.
        int getChannel();
        void setMode(Mode mode) throws DeviceStateError;
        Mode getMode();
        idempotent RecentChannels getRecentChannels();
        idempotent TVChannelList getTVChannelList();
    }

    // Weather station

    enum WeatherCondition {SUNNY, CLOUDY, RAINY, SNOWY, WINDY};

    struct WeatherData {
        float temperature;
        float humidity;
        float pressure;
        float windSpeed;
        WeatherCondition overallCondition;
    }

    interface WeatherStation : Device {
        WeatherData getWeatherData();
    }
}
