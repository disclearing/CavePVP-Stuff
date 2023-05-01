package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Location;

public class SignGUIPacketAdaper extends PacketAdapter {

    public SignGUIPacketAdaper() {
        super(Foxtrot.getInstance(), PacketType.Play.Server.OPEN_SIGN_ENTITY);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Location location = new Location(event.getPlayer().getWorld(), event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1), event.getPacket().getIntegers().read(2));

        if (location.getBlock().getState().hasMetadata("noSignPacket")) {
            event.setCancelled(true);
        }
    }

}