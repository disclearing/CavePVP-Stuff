package cc.fyre.neutron.profile.packet;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor @NoArgsConstructor
public class AltLimitPacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    public AltLimitPacket(int amount) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("int",amount);
    }

    @Override
    public int id() {
        return 35;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int altlimit() {
        return this.jsonObject.get("int").getAsInt();
    }

    public void execute() {
        NeutronConstants.ALT_LIMIT_MAX = altlimit();
    }

}
