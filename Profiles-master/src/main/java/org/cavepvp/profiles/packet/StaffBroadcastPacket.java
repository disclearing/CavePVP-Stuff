package org.cavepvp.profiles.packet;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;

@NoArgsConstructor
@AllArgsConstructor
public class StaffBroadcastPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public StaffBroadcastPacket(String permission,String message) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("permission",permission);
        this.jsonObject.addProperty("message",message);
    }

    @Override
    public int id() {
        return 34345;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String permission() {
        return this.jsonObject.get("permission").getAsString();
    }

    public String message() {
        return ChatColor.translateAlternateColorCodes('&',this.jsonObject.get("message").getAsString());
    }

    public void broadcast() {

        for (Player loopPlayer : Profiles.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(this.permission())) {
                continue;
            }

            if (Piston.getInstance().getToggleStaff().contains(loopPlayer.getUniqueId())) {
                continue;
            }

            loopPlayer.sendMessage(this.message());
        }

    }


}
