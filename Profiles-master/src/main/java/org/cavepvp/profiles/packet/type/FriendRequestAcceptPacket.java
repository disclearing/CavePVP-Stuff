package org.cavepvp.profiles.packet.type;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestAcceptPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public FriendRequestAcceptPacket(String sender, UUID target) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("sender",sender);
        this.jsonObject.addProperty("target",target.toString());
    }

    @Override
    public int id() {
        return 92;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String senderName() {
        return this.jsonObject.get("sender").getAsString();
    }

    public String targetUUID() {
        return this.jsonObject.get("target").getAsString();
    }

    public void notifyPlayer() {

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.getUniqueId().toString().equalsIgnoreCase(this.targetUUID())) {
                continue;
            }

            loopPlayer.sendMessage("");
            loopPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "New Friend");
            loopPlayer.sendMessage(this.senderName() + ChatColor.GRAY + " has accepted your friend request!");
            loopPlayer.sendMessage("");
        }

    }


}
