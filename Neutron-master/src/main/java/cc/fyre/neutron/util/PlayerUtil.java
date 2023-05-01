package cc.fyre.neutron.util;

import cc.fyre.proton.tab.util.TabUtils;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.time.Duration;

public class PlayerUtil {
    public static void sendActionBar(Player player, String text) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(
                new ChatComponentText(text),
                2,
                false
        ));
    }

    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, null);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        if (!TabUtils.is18(player)) {
            return;
        }

        final EntityPlayer handle = ((CraftPlayer)player).getHandle();

        handle.playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.TITLE,new ChatComponentText(ChatColor.translate(title))));

        if (subtitle != null) {
            handle.playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.SUBTITLE,new ChatComponentText(ChatColor.translate(subtitle))));
        }

        handle.playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.TIMES,20,4 * 20,20));
    }
}
