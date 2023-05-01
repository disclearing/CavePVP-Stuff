package cc.fyre.neutron.prevention.packets;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prevention.PreventionHandler;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PreventionResolvePacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    public PreventionResolvePacket( long time) {
        this.jsonObject = new JsonObject();
        jsonObject.addProperty("time", time);

    }

    @Override
    public int id() {
        return 18;
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
        for(Prevention prevention : Neutron.getInstance().getPreventionHandler().getPreventionList()) {
            if(prevention.getTime() == jsonObject.get("time").getAsLong()) {
                prevention.setResolved(true);
            }
        }
    }



}