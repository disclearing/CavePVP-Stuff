package cc.fyre.neutron.profile.attributes.grant.packet;


import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import cc.fyre.proton.pidgin.packet.Packet;
import org.bson.Document;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class GrantApplyPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public GrantApplyPacket(UUID uuid,Document document) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid",uuid.toString());
        this.jsonObject.addProperty("document",document.toJson());
    }

    @Override public int id() {
        return 2;
    }

    @Override public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public UUID uuid() {
        return UUID.fromString(this.jsonObject.get("uuid").getAsString());
    }

    public Document document() {
        return Document.parse(this.jsonObject.get("document").getAsString());
    }
}
