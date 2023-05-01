package cc.fyre.proton.nametag.construct;

import cc.fyre.proton.packet.ScoreboardTeamPacketMod;
import lombok.Getter;

import java.util.ArrayList;

public final class NameTagInfo {

    @Getter private String name;
    @Getter private String prefix;
    @Getter private String suffix;

    @Getter private ScoreboardTeamPacketMod teamAddPacket;

    public NameTagInfo(String name,String prefix,String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        this.teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<String>(), 0);
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof NameTagInfo) {

            final NameTagInfo otherNametag = (NameTagInfo) other;

            return (this.name.equals(otherNametag.name) && this.prefix.equals(otherNametag.prefix) && this.suffix.equals(otherNametag.suffix));
        }

        return false;
    }

}