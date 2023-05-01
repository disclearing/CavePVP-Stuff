package cc.fyre.universe.packet.maintenance;

import cc.fyre.universe.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author xanderume@gmail (JavaProject)
 */
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceModePacket implements Packet {

    @Getter private JsonObject jsonObject;

    @Override
    public int id() {
        return 458;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.jsonObject = object;
    }
}
