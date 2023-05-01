package cc.fyre.neutron.packet;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.util.AntiVPNUtil;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class IPChangePacket implements Packet {

    @Getter private JsonObject jsonObject;

    public IPChangePacket(UUID target, String name, String beforeAddress, String newAddress) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("target",target.toString());
        this.jsonObject.addProperty("name",name);
        this.jsonObject.addProperty("beforeAddress",beforeAddress);
        this.jsonObject.addProperty("newAddress",newAddress);
    }

    @Override
    public int id() {
        return 34980;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String name() {
        return this.jsonObject.get("name").getAsString();
    }

    public String beforeAddress() {
        return this.jsonObject.get("beforeAddress").getAsString();
    }

    public String newAddress() {
        return this.jsonObject.get("newAddress").getAsString();
    }

    public UUID getTarget() {
        return UUID.fromString(this.jsonObject.get("target").getAsString());
    }

    public void broadcast() {
        Neutron.getInstance().getServer().getScheduler().runTaskAsynchronously(Neutron.getInstance(), () -> {
            try {
                final AntiVPNUtil.Result afterResult = AntiVPNUtil.getResult(newAddress());
                final AntiVPNUtil.Result beforeResult = AntiVPNUtil.getResult(beforeAddress());

                Neutron.getInstance().getSecurityHandler().addSecurityAlert(getTarget(), null, AlertType.IP_CHANGE, true, "New Address: " + newAddress() + " [" + afterResult.getCountry() + "]", "Old Address: " + beforeAddress() + " [" + beforeResult.getCountry() + "]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
