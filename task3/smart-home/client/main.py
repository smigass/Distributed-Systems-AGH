import sys

import Ice
import IceGrid
import inquirer
from Ice import ObjectPrx

from SmartHome import *

def display_device_details(details, proxy=None):
    print("\n" + "="*50)
    print(f"Name: {details.name}")
    print(f"Location: {details.location}")
    print(f"Status: {details.status.name.capitalize()}")
    print(f"Uptime: {details.uptime}")
    print("="*50)


def control_light(light):
    device_type = light.ice_id().split("::")[-1]
    while True:
        try:
            details = light.getDetails()
            light_details = light.getLightDetails()

            display_device_details(details, light)

            if device_type == "NormalLight":
                print(f"Brightness: {light_details.brightness}%")
                options = [
                    inquirer.List(
                        "action",
                        message="What would you like to do?",
                        choices=["Turn On", "Turn Off", "Set Brightness", "Back"]
                    )
                ]
            elif device_type == "DeskLamp":
                print(f"Brightness: {light_details.brightness}%")
                print(f"Color Temperature: {light_details.colorTemperature.name.capitalize()}")
                options = [

                    inquirer.List(
                        "action",
                        message="What would you like to do?",
                        choices=["Turn On", "Turn Off", "Set Brightness", "Set Temperature", "Back"]
                    )
                ]
            else:
                print(f"Color: {light_details.color}")
                print(f"Rotation Speed: {light_details.rotationSpeed}%")
                options = [
                    inquirer.List(
                        "action",
                        message="What would you like to do?",
                        choices=["Turn On", "Turn Off", "Set Color", "Set Rotation Speed", "Start Party", "Stop Party", "Back"]
                    )
                ]

            answer = inquirer.prompt(options)
            action = answer["action"]

            if action == "Back":
                break
            elif action == "Turn On":
                light.turnOn()
                print("Light turned ON")
            elif action == "Turn Off":
                light.turnOff()
                print("Light turned OFF")
            elif action == "Set Brightness":
                options = [inquirer.Text("brightness", message="Enter brightness (0-100)")]
                answer = inquirer.prompt(options)
                brightness = int(answer["brightness"])
                light.setBrightness(brightness)
                print(f"Brightness set to {brightness}%")
            elif action == "Set Temperature":
                options = [
                    inquirer.List(
                        "temp",
                        message="Choose temperature",
                        choices=["WARM", "NEUTRAL", "COOL"]
                    )
                ]
                answer = inquirer.prompt(options)
                temp_map = {"WARM": ColorTemperature.WARM, "NEUTRAL": ColorTemperature.NEUTRAL, "COOL": ColorTemperature.COOL}
                light.setTemperature(temp_map[answer["temp"]])
                print(f"Temperature set to {answer['temp']}")
            elif action == "Set Color":
                options = [inquirer.Text("color", message="Enter color in hex format (FFFFFF")]
                answer = inquirer.prompt(options)
                light.setColor(answer["color"])
                print(f"Color set to {answer['color']}")
            elif action == "Set Rotation Speed":
                options = [inquirer.Text("speed", message="Enter rotation speed (0-100)")]
                answer = inquirer.prompt(options)
                speed = int(answer["speed"])
                light.setRotationSpeed(speed)
                print(f"Rotation speed set to {speed}%")
            elif action == "Start Party":
                options = [inquirer.Text("color", message="Enter starting color in hex format (FFFFFF)")]
                answer = inquirer.prompt(options)
                light.startDiscoParty(answer["color"])
                print("Disco party started!")
            elif action == "Stop Party":
                light.stopDiscoParty()
                print("Disco party stopped")
        except Exception as e:
            print(f"Error: {str(e)}")


def control_tv(tv):
    while True:
        try:
            details = tv.getDetails()
            display_device_details(details, tv)

            volume = tv.getVolume()
            channel = tv.getChannel()
            mode = tv.getMode()

            print(f"Volume: {volume}")
            print(f"Current Channel: {channel}")
            print(f"Mode: {mode.name.capitalize()}")

            options = [
                inquirer.List(
                    "action",
                    message="What would you like to do?",
                    choices=["Turn On", "Turn Off", "Set Volume", "Change Channel", "Change Mode", "Show Channels", "Recent Channels", "Back"]
                )
            ]

            answer = inquirer.prompt(options)
            action = answer["action"]

            if action == "Back":
                break
            elif action == "Turn On":
                tv.turnOn()
                print("TV turned ON")
            elif action == "Turn Off":
                tv.turnOff()
                print("TV turned OFF")
            elif action == "Set Volume":
                options = [inquirer.Text("volume", message="Enter volume (0-100)")]
                answer = inquirer.prompt(options)
                volume = int(answer["volume"])
                tv.setVolume(volume)
                print(f"Volume set to {volume}")
            elif action == "Change Channel":
                options = [inquirer.Text("channel", message="Enter channel number")]
                answer = inquirer.prompt(options)
                channel = int(answer["channel"])
                tv.setChannel(channel)
                print(f"Channel changed to {channel}")
            elif action == "Show Channels":
                channels = tv.getTVChannelList()
                print("\nAvailable Channels:")
                for ch in channels:
                    print(f"  {ch.channelNumber}: {ch.channelName}")
            elif action == "Recent Channels":
                recent = tv.getRecentChannels()
                print("\nRecent Channels:")
                for ch in recent:
                    print(f"  - {ch}")
            elif action == "Change Mode":
                options = [
                    inquirer.List(
                        "mode",
                        message="Choose mode",
                        choices=["NORMAL", "ECO"]
                    )
                ]
                answer = inquirer.prompt(options)
                mode_map = {"NORMAL": Mode.NORMAL, "ECO": Mode.ECO}
                tv.setMode(mode_map[answer["mode"]])
                print(f"Mode changed to {answer['mode']}")
        except InvalidValue as e:
            print(f"Error: {str(e.reason)}")
        except DeviceStateError as e:
            print(f"Error: {str(e.reason)}")
        except Exception as e:
            print(f"Error: {str(e)}")


def display_weather(ws):
    try:
        details = ws.getDetails()
        display_device_details(details, ws)

        weather = ws.getWeatherData()
        print("Weather Information:")
        print(f"Temperature: {weather.temperature} C")
        print(f"Humidity: {weather.humidity:.2f}%")
        print(f"Pressure: {weather.pressure} hPa")
        print(f"Wind Speed: {weather.windSpeed:.2f} m/s")
        print(f"Condition: {weather.overallCondition.name.capitalize()}")
        print("="*50)

        input("\nPress Enter to go back...")
    except InvalidValue as e:
        print(f"Error: {str(e.reason)}")
    except DeviceStateError as e:
        print(f"Error: {str(e.reason)}")
    except Exception as e:
        print(f"Error: {str(e)}")


def display_floor_devices(devices):
    if not devices:
        print("No devices on this floor.\n")
        return

    print("\n" + "="*50)
    print("Devices on this floor:")
    print("="*50)

    lights = []
    tvs = []
    weather_stations = []

    for device in devices:
        try:
            device_type = device.ice_ids()[-1].split("::")[-1]
            name = device.ice_getIdentity().name

            if "Light" in device_type:
                lights.append((name, device_type, device))
            elif device_type == "TV":
                tvs.append((name, device_type, device))
            elif device_type == "WeatherStation":
                weather_stations.append((name, device_type, device))
        except Exception as e:
            print(f"Error processing device: {e}")

    if lights:
        print("\nLights:")
        for name, dtype, _ in lights:
            print(f"  - {name} ({dtype})")

    if tvs:
        print("\nTVs:")
        for name, dtype, _ in tvs:
            print(f"  - {name}")

    if weather_stations:
        print("\nWeather Stations:")
        for name, dtype, _ in weather_stations:
            print(f"  - {name}")

    print("\n" + "="*50)

    all_devices = lights + tvs + weather_stations
    if all_devices:
        options = [
            inquirer.List(
                "device",
                message="Select device to control",
                choices=[name + " | " + dtype for name, dtype, _ in all_devices] + ["Back"]
            )
        ]
        answer = inquirer.prompt(options)

        if answer["device"] != "Back":
            selected_name = answer["device"]
            for name, dtype, device in all_devices:
                if selected_name == name + " | " + dtype:
                    if "Light" in dtype:
                        control_light(device)
                    elif dtype == "TV":
                        control_tv(device)
                    elif dtype == "WeatherStation":
                        display_weather(device)
                    break


def main():
    home = [[], [], []]

    lights: list[NormalLightPrx | DeskLampPrx | RgbDiscoLightPrx] = []
    tvs: list[TVPrx] = []
    weather_stations: list[WeatherStationPrx] = []

    with Ice.initialize(sys.argv) as communicator:
        query_proxy = communicator.stringToProxy("IceGrid/Query")
        query = IceGrid.QueryPrx.checkedCast(query_proxy)

        prx_types = [
            "::SmartHome::NormalLight",
            "::SmartHome::RgbDiscoLight",
            "::SmartHome::TV",
            "::SmartHome::WeatherStation",
            "::SmartHome::Light",
            "::SmartHome::DeskLamp",
        ]


        all_devices: list[ObjectPrx] = []

        for prx_type in prx_types:
            objects = query.findAllObjectsByType(prx_type)
            for p in objects:
                all_devices.append(p)


        for raw_proxy in all_devices:
            proxy = communicator.stringToProxy(raw_proxy.ice_toString())
            final_prx = None

            tv_prx = TVPrx.checkedCast(proxy)
            if tv_prx:
                final_prx = tv_prx
                tvs.append(tv_prx)

            light_prx = NormalLightPrx.checkedCast(proxy)
            if light_prx:
                final_prx = light_prx
                lights.append(light_prx)

            light_prx = RgbDiscoLightPrx.checkedCast(proxy)
            if light_prx:
                final_prx = light_prx
                lights.append(light_prx)

            light_prx = DeskLampPrx.checkedCast(proxy)
            if light_prx:
                final_prx = light_prx
                lights.append(light_prx)

            weather_station = WeatherStationPrx.checkedCast(proxy)
            if weather_station:
                final_prx = weather_station
                weather_stations.append(weather_station)

            if final_prx:
                floor = int("".join(filter(str.isdigit, final_prx.ice_getAdapterId())))
                home[floor - 1].append(final_prx)



        while True:
            options = [
                inquirer.List(
                "filter",
                message="Choose filtering option",
                choices=["Floor", "Device type"]
            )]
            filter_answer = inquirer.prompt(options)

            if filter_answer["filter"] == "Floor":
                options = [
                    inquirer.List(
                        "floor",
                        message="Choose floor number",
                        choices=["1", "2", "3"]
                    )]
                floor_answer = inquirer.prompt(options)
                floor_num = int(floor_answer["floor"])
                display_floor_devices(home[floor_num - 1])

            elif filter_answer["filter"] == "Device type":
                options = [
                    inquirer.List(
                        "type",
                        message="Choose device type",
                        choices=["Light", "TV", "WeatherStation"]
                    )]
                type_answer = inquirer.prompt(options)
                device_type = type_answer["type"]

                if device_type == "Light":
                    options = [
                        inquirer.List(
                            "light",
                            message="Choose device",
                            choices=[light.ice_getIdentity().name + " | " +light.ice_ids()[-1].split("::")[2] for light in lights]
                        )
                    ]
                    device_answer = inquirer.prompt(options)
                    light = lights[[light.ice_getIdentity().name + " | " +light.ice_ids()[-1].split("::")[2] for light in lights].index(device_answer["light"])]
                    control_light(light)

                elif device_type == "TV":
                    options = [
                        inquirer.List(
                            "tv",
                            message="Choose device",
                            choices=[tv.ice_getIdentity().name for tv in tvs]
                        )
                    ]
                    device_answer = inquirer.prompt(options)
                    tv = tvs[[tv.ice_getIdentity().name for tv in tvs].index(device_answer["tv"])]
                    control_tv(tv)

                elif device_type == "WeatherStation":
                    options = [
                        inquirer.List(
                            "ws",
                            message="Choose device",
                            choices=[ws.ice_getIdentity().name for ws in weather_stations]
                        )
                    ]
                    device_answer = inquirer.prompt(options)
                    ws = weather_stations[[ws.ice_getIdentity().name for ws in weather_stations].index(device_answer["ws"])]
                    display_weather(ws)



if __name__ == "__main__":
    main()