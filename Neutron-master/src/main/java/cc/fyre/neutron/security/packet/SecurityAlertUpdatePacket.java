package cc.fyre.neutron.security.packet;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.SecurityAlert;
import cc.fyre.neutron.security.SecurityHandler;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SecurityAlertUpdatePacket implements Packet {

    @Getter private JsonObject jsonObject;

    public SecurityAlertUpdatePacket(String id) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("id",id);
    }

    @Override
    public int id() {
        return 349234;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getId() {
        return this.jsonObject.get("id").getAsString();
    }

    public void update() {
        final SecurityHandler securityHandler = Neutron.getInstance().getSecurityHandler();
        final SecurityAlert securityAlert = securityHandler.loadAlert(this.getId());
        securityHandler.getAlerts().add(securityAlert);
    }
}
