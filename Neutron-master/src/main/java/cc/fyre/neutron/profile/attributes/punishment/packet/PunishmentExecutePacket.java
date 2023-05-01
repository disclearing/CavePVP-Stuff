package cc.fyre.neutron.profile.attributes.punishment.packet;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import cc.fyre.proton.pidgin.packet.Packet;

import org.bson.Document;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class PunishmentExecutePacket implements Packet {

    @Getter private JsonObject jsonObject;

    public PunishmentExecutePacket(UUID uuid,Document document,boolean broadCastOnly,String punishedFancyName, String server) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid",uuid.toString());
        this.jsonObject.addProperty("document",document.toJson());
        this.jsonObject.addProperty("broadCastOnly",broadCastOnly);
        this.jsonObject.addProperty("punishedFancyName",punishedFancyName);
        this.jsonObject.addProperty("server",punishedFancyName);
    }

    @Override
    public int id() {
        return 5;
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

    public Document document() {
        return Document.parse(this.jsonObject.get("document").getAsString());
    }

    public boolean broadCastOnly() {
        return this.jsonObject.get("broadCastOnly").getAsBoolean();
    }

    public String punishedFancyName() {
        return this.jsonObject.get("punishedFancyName").getAsString();
    }
    public String server() {
        return this.jsonObject.get("server").getAsString();
    }
}
