package cc.fyre.neutron.prevention.packets;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@NoArgsConstructor
public class PreventionCreatePacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    public PreventionCreatePacket(UUID uuid, String command, long time, boolean resolved) {
        this.jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", uuid.toString());
        jsonObject.addProperty("command", command);
        jsonObject.addProperty("time", time);
        jsonObject.addProperty("resolved", resolved);

    }

    @Override
    public int id() {
        return 15;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void execute() {
        Neutron.getInstance().getPreventionHandler().getPreventionList().add(new Prevention(UUID.fromString(jsonObject.get("uuid").getAsString()),
                jsonObject.get("command").getAsString(), jsonObject.get("time").getAsLong(), jsonObject.get("resolved").getAsBoolean()));
    }

}
