package org.cavepvp.profiles.packet;

import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import org.cavepvp.profiles.packet.type.*;

public class ProfilesPacketListener implements PacketListener {

    @IncomingPacketHandler
    public void onNotification(NotificationSendPacket packet) {
        packet.notifyPlayer();
    }

    @IncomingPacketHandler
    public void onFriendRequest(FriendRequestSendPacket packet) {
        packet.notifyPlayer();
    }

    @IncomingPacketHandler
    public void onAccept(FriendRequestAcceptPacket packet) {
        packet.notifyPlayer();
    }

    @IncomingPacketHandler
    public void onNotify(FriendSessionPacket packet) {
        packet.notifyPlayers();
    }

    @IncomingPacketHandler
    public void onUpdate(ProfileUpdatePacket packet) {
        packet.updatePlayer();
    }

    @IncomingPacketHandler
    public void onStaffBroadcast(StaffBroadcastPacket packet) {
        packet.broadcast();
    }

}
