package org.cavepvp.profiles.packet.type;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public NotificationSendPacket(UUID target) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("target",target.toString());
    }

    @Override
    public int id() {
        return 94;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String targetUUID() {
        return this.jsonObject.get("target").getAsString();
    }

    public void notifyPlayer() {

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.getUniqueId().toString().equalsIgnoreCase(this.targetUUID())) {
                continue;
            }

            loopPlayer.playSound(loopPlayer.getLocation(), Sound.NOTE_PLING, 1, 1);

            final FancyMessage spacer = new FancyMessage("").tooltip(ChatColor.GREEN + "Click to view all notifications").command("/notifications");

            spacer.send(loopPlayer);
            new FancyMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Notifications").tooltip(ChatColor.GREEN + "Click to view all unread notifications").command("/notifications").send(loopPlayer);
            new FancyMessage(ChatColor.GRAY + "You have a new notification! " + ChatColor.GREEN + "[Click to view]").tooltip(ChatColor.GREEN + "Click to view all unread notifications").command("/notifications").send(loopPlayer);
            spacer.send(loopPlayer);
            break;
        }

    }


}
