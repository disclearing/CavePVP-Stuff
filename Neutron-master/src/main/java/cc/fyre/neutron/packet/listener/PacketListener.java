package cc.fyre.neutron.packet.listener;

import cc.fyre.neutron.packet.BroadcastPacket;
import cc.fyre.neutron.packet.IPChangePacket;
import cc.fyre.neutron.packet.ManagementBroadcastPacket;
import cc.fyre.neutron.security.packet.SecurityAlertUpdatePacket;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;

public class PacketListener implements cc.fyre.proton.pidgin.packet.listener.PacketListener {
    @IncomingPacketHandler
    public void onBroadcast(BroadcastPacket packet) {
        packet.broadcast();
    }

    @IncomingPacketHandler
    public void onManagementBroadcast(ManagementBroadcastPacket packet) {
        packet.broadcast();
    }
    @IncomingPacketHandler
    public void onChange(IPChangePacket packet) {
        packet.broadcast();
    }

    @IncomingPacketHandler
    public void onChange(SecurityAlertUpdatePacket packet) {
        packet.update();
    }
}
