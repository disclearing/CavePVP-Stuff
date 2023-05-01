package cc.fyre.piston.packet;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class SyncPlayersPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public SyncPlayersPacket(String display, String uuid, boolean disconnect) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("display",display);
        this.jsonObject.addProperty("playerID",uuid.toString());
        this.jsonObject.addProperty("disconnect",disconnect);
    }

    @Override
    public int id() {
        return 239;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getDisplay() {
        return this.jsonObject.get("display").getAsString();
    }

    public UUID getPlayerID() {
        return UUID.fromString(this.jsonObject.get("playerID").getAsString());
    }

    public boolean isDisconnect() {
        return this.jsonObject.get("disconnect").getAsBoolean();
    }

    public void addPlayer() {
        UUID uuid = getPlayerID();
        String displayName = getDisplay();

        if (isDisconnect()) {
            Piston.getInstance().getSyncHandler().getPlayers().remove(uuid);
            return;
        }

        Piston.getInstance().getSyncHandler().getPlayers().put(uuid, displayName);
    }


}
