package cc.fyre.proton.hologram.packet;

import cc.fyre.proton.hologram.construct.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider {

	HologramPacket getPacketsFor(Location location,HologramLine line);

	//TODO: add more versions
}
