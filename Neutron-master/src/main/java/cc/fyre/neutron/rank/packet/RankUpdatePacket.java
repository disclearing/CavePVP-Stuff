package cc.fyre.neutron.rank.packet;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cc.fyre.proton.pidgin.packet.Packet;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class RankUpdatePacket implements Packet {

    @Getter private JsonObject jsonObject;

    public RankUpdatePacket(UUID uuid) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid",uuid.toString());
    }

    @Override
    public int id() {
        return 14;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public UUID uuid() {
        return UUID.fromString(this.jsonObject.get("uuid").getAsString());
    }
}
