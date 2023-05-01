package cc.fyre.piston.client.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum Version {
    UNKNOWN("Unknown",500),
    ONE_SEVEN_X("1.7.x",5),
    ONE_EIGHT_X("1.8.x",47);

    String version;
    int ProtocolNumber; //https://wiki.vg/Protocol_version_numbers
}
