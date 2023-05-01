package cc.fyre.proton.uuid;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import cc.fyre.proton.pidgin.packet.Packet;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class UUIDCacheUpdatePacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    UUIDCacheUpdatePacket(UUID uuid, String name) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid", uuid.toString());
        this.jsonObject.addProperty("name", name);
    }

    @Override
    public int id() {
        return 999;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.jsonObject = object;
    }

    public UUID uuid() {
        return UUID.fromString(this.jsonObject.get("uuid").getAsString());
    }

    public String name() {
        return this.jsonObject.get("name").getAsString();
    }
}
