package net.minecraft.server;

import net.minecraft.util.io.netty.channel.ChannelFutureListener;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

public class QueuedProtocolSwitch implements Runnable { // Poweruser - public

    final EnumProtocol a;
    final EnumProtocol b;
    final Packet c;
    final GenericFutureListener[] d;
    final NetworkManager e;

    QueuedProtocolSwitch(NetworkManager networkmanager, EnumProtocol enumprotocol, EnumProtocol enumprotocol1, Packet packet, GenericFutureListener[] agenericfuturelistener) {
        this.e = networkmanager;
        this.a = enumprotocol;
        this.b = enumprotocol1;
        this.c = packet;
        this.d = agenericfuturelistener;
    }

    public void run() {
    // Poweruser start
        execute(this.e, this.a, this.b, this.c, this.d);
    }

    public static void execute(NetworkManager networkmanager, EnumProtocol enumprotocol, EnumProtocol enumprotocol1, Packet packet, GenericFutureListener[] agenericfuturelistener) {
        if (enumprotocol != enumprotocol1) {
            networkmanager.a(enumprotocol);
        }

        if(agenericfuturelistener == null || agenericfuturelistener.length == 0) {
            NetworkManager.a(networkmanager).writeAndFlush(packet, NetworkManager.a(networkmanager).voidPromise());
        } else {
            NetworkManager.a(networkmanager).writeAndFlush(packet).addListeners(agenericfuturelistener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }
    // Poweruser end
}
