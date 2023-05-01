package cc.fyre.neutron.profile.packet;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import cc.fyre.proton.pidgin.packet.Packet;

import java.util.UUID;

/**
 * @author xanderume@gmail (JavaProject)
 */
@NoArgsConstructor
@AllArgsConstructor
public class PermissionAddPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public PermissionAddPacket(UUID uuid,String permission) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid",uuid.toString());
        this.jsonObject.addProperty("permission",permission);
    }

    @Override
    public int id() {
        return 7;
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

    public String permission() {
        return this.jsonObject.get("permission").getAsString();
    }

}
