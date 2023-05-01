package cc.fyre.proton.hologram.packet.v1_7;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.Arrays;

import cc.fyre.proton.hologram.construct.HologramLine;
import cc.fyre.proton.hologram.packet.HologramPacket;
import cc.fyre.proton.hologram.packet.HologramPacketProvider;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Minecraft17HologramPacketProvider implements HologramPacketProvider {

	public HologramPacket getPacketsFor(Location location,HologramLine line) {

		final PacketContainer skullPacket = this.createWitherSkull(location, line.getSkullId());
		final PacketContainer horsePacket = this.createHorse(location, line.getHorseId(), line.getText());
		final PacketContainer attachPacket = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);

		attachPacket.getIntegers().write(1,line.getHorseId());
		attachPacket.getIntegers().write(2,line.getSkullId());

		return new HologramPacket(Arrays.asList(skullPacket,horsePacket,attachPacket), Arrays.asList(line.getSkullId(), line.getHorseId()));
	}

	protected PacketContainer createWitherSkull(Location location, int id) {

		final PacketContainer skull = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

		final StructureModifier<Integer> ints = skull.getIntegers();

		ints.write(0,id);
		ints.write(1,(int)(location.getX()*32.0D));
		ints.write(2,MathHelper.floor((location.getY()-0.13D+55.0D)*32.0D));
		ints.write(3,(int)(location.getZ()*32.0D));
		ints.write(9,66);

		return skull;
	}

	protected PacketContainer createHorse(Location location, int id, String text) {

		final PacketContainer horse = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

		horse.getIntegers().write(0,id);
		horse.getIntegers().write(1,100);
		horse.getIntegers().write(2,(int)(location.getX()*32.0D));
		horse.getIntegers().write(3,MathHelper.floor((location.getY()+55.0D)*32.0D));
		horse.getIntegers().write(4,(int)(location.getZ()*32.0D));

		final WrappedDataWatcher watcher = new WrappedDataWatcher();

		watcher.setObject(0,(byte)0);
		watcher.setObject(1,(short)300);
		watcher.setObject(10,ChatColor.translateAlternateColorCodes('&', text));
		//watcher.setObject(11,(byte)1);
		watcher.setObject(11,(byte)((text.equals("spacer")) ? 0:1));

		watcher.setObject(12,-1700000);

		horse.getDataWatcherModifier().write(0, watcher);

		return horse;
	}

}
