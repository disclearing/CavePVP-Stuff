package cc.fyre.proton.tab;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TabCommands {

    @Command(
            names = {"tab-debug add"}, permission = "op"
    )
    public static void execute(Player player, @Parameter(name = "player") Player target) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer) target).getHandle()));
    }

    @Command(
            names = {"tab-debug remove"}, permission = "op"
    )
    public static void execute2(Player player, @Parameter(name = "player") Player target) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) target).getHandle()));
    }

}
