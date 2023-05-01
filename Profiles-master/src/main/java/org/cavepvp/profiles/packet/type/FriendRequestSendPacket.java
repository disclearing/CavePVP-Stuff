package org.cavepvp.profiles.packet.type;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestSendPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public FriendRequestSendPacket(String sender, UUID target) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("sender",sender);
        this.jsonObject.addProperty("target", target.toString());
    }

    @Override
    public int id() {
        return 93;
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

            final FancyMessage spacer = new FancyMessage("").tooltip(ChatColor.GREEN + "Click to accept this friend request").command("/friend add " + this.senderName());
            final FancyMessage topMessage = new FancyMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "New Friend Request").tooltip(ChatColor.GREEN + "Click to view accept this friend request").command("/friend add " + this.senderName());
            final FancyMessage bottomMessage = new FancyMessage(this.senderName() + ChatColor.GRAY + " has added you as a friend!").tooltip(ChatColor.GREEN + "Click to view accept this friend request").command("/friend add " + this.senderName());

            spacer.send(loopPlayer);
            topMessage.send(loopPlayer);
            bottomMessage.send(loopPlayer);
            spacer.send(loopPlayer);
        }

    }


}
