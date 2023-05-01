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
public class MaintenanceListPacket implements Packet {

    @Getter private JsonObject jsonObject;

    @Override
    public int id() {
        return 457;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.jsonObject = object;
    }

    public enum Action {

        ADD,
        REMOVE

    }
}