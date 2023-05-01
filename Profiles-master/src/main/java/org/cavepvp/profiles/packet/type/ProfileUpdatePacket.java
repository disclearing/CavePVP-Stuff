package org.cavepvp.profiles.packet.type;

import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdatePacket implements Packet {

    @Getter
    private JsonObject jsonObject;

    public ProfileUpdatePacket(UUID target) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("target",target.toString());
    }

    @Override
    public int id() {
        return 1068;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public UUID targetUUID() {
        return UUID.fromString(this.jsonObject.get("target").getAsString());
    }

    public void updatePlayer() {
        if (Profiles.getInstance().getPlayerProfileHandler().getCache().containsKey(this.targetUUID())) {
            final PlayerProfile newProfile = Profiles.getInstance().getPlayerProfileHandler().fromDatabase(this.targetUUID()).orElse(null);

            if (newProfile == null) {
                return;
            }

            Profiles.getInstance().getPlayerProfileHandler().getCache().replace(this.targetUUID(), newProfile);
        }
    }


}
