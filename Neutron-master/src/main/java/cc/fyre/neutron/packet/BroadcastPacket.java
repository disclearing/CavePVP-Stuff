package cc.fyre.neutron.packet;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.entity.Player;

@NoArgsConstructor
@AllArgsConstructor
public class BroadcastPacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    public BroadcastPacket(FancyMessage message) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("message",message.toJSONString());
    }

    @Override
    public int id() {
        return 72;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public FancyMessage message() {
        return FancyMessage.deserialize(this.jsonObject.get("message").getAsString());
    }

    public void broadcast() {

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {
            this.message().send(loopPlayer);
        }

    }


}