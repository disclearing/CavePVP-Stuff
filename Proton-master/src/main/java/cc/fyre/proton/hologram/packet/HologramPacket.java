package cc.fyre.proton.hologram.packet;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class HologramPacket {

	@Getter private List<PacketContainer> packets;
	@Getter private List<Integer> entityIds;

	public void sendToPlayer(Player player) {

		this.packets.forEach(packet -> {

			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player,packet);
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}

		});

	}
}
