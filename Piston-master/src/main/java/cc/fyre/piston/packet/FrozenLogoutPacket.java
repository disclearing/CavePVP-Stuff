package cc.fyre.piston.packet;

import cc.fyre.piston.Piston;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@NoArgsConstructor
@AllArgsConstructor
public class FrozenLogoutPacket implements Packet {

    @Getter private JsonObject jsonObject;

    public FrozenLogoutPacket(String playername) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("playername",playername);
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

    public String getPlayerName() {
        return this.jsonObject.get("playername").getAsString();
    }

    public void broadcast() {

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission("neutron.staff")) {
                continue;
            }

            if (Piston.getInstance().getToggleStaff().contains(loopPlayer.getUniqueId())) {
                continue;
            }

            loopPlayer.sendMessage("");
            new FancyMessage(ChatColor.translate("&4&l" + getPlayerName() + " has logged out whilst frozen!")).command("/ban " + getPlayerName() + " Logging out whilst frozen").tooltip(ChatColor.GREEN + "Click to ban").send(loopPlayer);
            loopPlayer.sendMessage("");
        }

    }


}
