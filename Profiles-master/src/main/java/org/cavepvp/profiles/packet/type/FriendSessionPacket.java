package org.cavepvp.profiles.packet.type;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class FriendSessionPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public FriendSessionPacket(UUID target, String server, boolean disconnect) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("target",target.toString());
        this.jsonObject.addProperty("server",server);
        this.jsonObject.addProperty("disconnect",disconnect);
    }

    @Override
    public int id() {
        return 95;
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

    public String serverName() {
        return this.jsonObject.get("server").getAsString();
    }

    public boolean disconnect() {
        return this.jsonObject.get("disconnect").getAsBoolean();
    }

    public void notifyPlayers() {

        final Optional<PlayerProfile> playerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(this.targetUUID());

        if (!playerProfile.isPresent()) {
            return;
        }

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if (!playerProfile.get().getFriends().contains(loopPlayer.getUniqueId())) {
                continue;
            }

            if (this.disconnect()) {
                loopPlayer.sendMessage(ChatColor.translate("&4&lFriends &8┃ &f" + playerProfile.get().getName() + " &7has left &f" + serverName() + "&7."));
            } else {
                loopPlayer.sendMessage(ChatColor.translate("&4&lFriends &8┃ &f" + playerProfile.get().getName() + " &7has joined &f" + serverName() + "&7."));
            }
        }

    }


}
