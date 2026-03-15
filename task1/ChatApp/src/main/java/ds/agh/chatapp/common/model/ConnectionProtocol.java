package ds.agh.chatapp.common.model;

import java.util.Arrays;
import java.util.List;

public enum ConnectionProtocol {
    TCP, UDP, UDP_MULTICAST;

    public static List<String> valuesAsList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .toList();
    }
}
