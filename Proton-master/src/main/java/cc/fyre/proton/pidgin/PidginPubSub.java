package cc.fyre.proton.pidgin;

import cc.fyre.proton.Proton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.pidgin.packet.Packet;
import cc.fyre.proton.pidgin.packet.listener.PacketListenerData;
import redis.clients.jedis.JedisPubSub;

@AllArgsConstructor
public class PidginPubSub extends JedisPubSub {

    @Getter private String channel;

    @Override
    public void onMessage(String channel,String message) {

        if (!channel.equalsIgnoreCase(this.channel)) {
            return;
        }

        try {

            final String[] args = message.split(";");
            final Integer id = Integer.valueOf(args[0]);
            final Packet packet = Proton.getInstance().getPidginHandler().buildPacket(id);

            if (packet == null) {
                return;
            }

            packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

            for (PacketListenerData listener : Proton.getInstance().getPidginHandler().getListeners()) {

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
