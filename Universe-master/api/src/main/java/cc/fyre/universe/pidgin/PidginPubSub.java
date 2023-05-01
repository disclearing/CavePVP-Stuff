package cc.fyre.universe.pidgin;

import cc.fyre.universe.pidgin.packet.Packet;
import cc.fyre.universe.pidgin.packet.listener.PacketListenerData;
import lombok.AllArgsConstructor;

import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@AllArgsConstructor
public class PidginPubSub extends JedisPubSub {

    @Getter private PidginHandler handler;

    @Override
    public void onMessage(String channel,String message) {

        if (!channel.equalsIgnoreCase(this.handler.getChannel())) {
            return;
        }

        try {

            final String[] args = message.split(";");
            final Integer id = Integer.valueOf(args[0]);
            final Packet packet = this.handler.buildPacket(id);

            if (packet == null) {
                return;
            }

            packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

            for (PacketListenerData listener : this.handler.getListeners()) {

                if (!listener.matches(packet)) {
                    continue;
                }

                listener.getMethod().invoke(listener.getInstance(),packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
