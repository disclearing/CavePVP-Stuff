package cc.fyre.piston.packet.listener;

import cc.fyre.piston.packet.FrozenLogoutPacket;
import cc.fyre.piston.packet.SyncPlayersPacket;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import cc.fyre.piston.packet.StaffBroadcastPacket;

public class PistonPacketListener implements PacketListener {

    @IncomingPacketHandler
    public void onStaffBroadcast(StaffBroadcastPacket packet) {
        packet.broadcast();
    }

    @IncomingPacketHandler
    public void onSyncPlayer(SyncPlayersPacket packet) {
        packet.addPlayer();
    }

    @IncomingPacketHandler
    public void onPlayerLogout(FrozenLogoutPacket packet) {
        packet.broadcast();
    }
}
