package cc.fyre.neutron.packet;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.pidgin.packet.Packet;
import cc.fyre.proton.util.UUIDUtils;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class ManagementBroadcastPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public ManagementBroadcastPacket(String permission, UUID target, String message) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("permission",permission);
        this.jsonObject.addProperty("target",target.toString());
        this.jsonObject.addProperty("message",message);
    }

    @Override
    public int id() {
        return 34928;
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
    public UUID getTarget() {
        return UUID.fromString(this.jsonObject.get("target").getAsString());
    }

    public String message() {
        return ChatColor.translateAlternateColorCodes('&',this.jsonObject.get("message").getAsString());
    }

    public void broadcast() {

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(this.permission())) {
                continue;
            }

            loopPlayer.sendMessage(this.message());
        }

    }


}
