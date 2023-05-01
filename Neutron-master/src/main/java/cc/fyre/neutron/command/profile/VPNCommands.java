package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.util.AntiVPNUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.StringJoiner;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class VPNCommands {

    @Command(
            names = {"vpn?"},
            permission = "neutron.command.vpncheck",
            hidden = true,
            async = true
    )
    public static void vpn(Player player, @Parameter(name = "player") String target) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(target, false, false);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Who tf is that?");
            return;
        }

        try {
            long time = System.nanoTime();
            AntiVPNUtil.Result result = AntiVPNUtil.getResult(profile.getIpAddress());
            double elapsed = (System.nanoTime() - time) / 1000000D;

            player.sendMessage(String.format("§e%s %s§e on a VPN", profile.getName(), result.isBad() ? "§ais" : "§cis not"));
            player.sendMessage("§eCountry: §b" + result.getCountry());
            player.sendMessage("§eASN: §b" + result.getAsn());
            player.sendMessage("§eOrg: §b" + result.getOrg());
            player.sendMessage("§eElapsed: §b" + elapsed + " ms");
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("§cAn error occurred: " + e.getMessage());
        }
    }

    @Command(
            names = {"antivpn whitelist", "antivpn wl", "antivpn add"},
            permission = "neutron.command.antivpn",
            hidden = true,
            async = true
    )
    public static void add(Player player, @Parameter(name = "player") String target) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(target, true, false);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Who tf is that?");
            return;
        }

        Proton.getInstance().runBackboneRedisCommand(jedis -> {
            jedis.hset("vpnWhitelist", profile.getUuid().toString(), player.getUniqueId().toString());
            return null;
        });

        player.sendMessage(YELLOW + target + " has been " + GREEN + "added" + YELLOW + " to the VPN whitelist.");
    }

    @Command(
            names = {"antivpn unwhitelist", "antivpn unwl", "antivpn remove", "antivpn delete"},
            permission = "neutron.command.antivpn",
            hidden = true,
            async = true
    )
    public static void remove(Player player, @Parameter(name = "player") String target) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(target, true, false);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Who tf is that?");
            return;
        }

        Proton.getInstance().runBackboneRedisCommand(jedis -> {
            jedis.hdel("vpnWhitelist", profile.getUuid().toString());
            return null;
        });

        player.sendMessage(YELLOW + target + " has been " + RED + "removed" + YELLOW + " from the VPN whitelist.");
    }

    @Command(
            names = {"antivpn list", "antivpn show"},
            permission = "neutron.command.antivpn",
            hidden = true,
            async = true
    )
    public static void list(Player player) {
        StringJoiner sj = new StringJoiner("\n", YELLOW + "VPN Whitelist: ", "");

        Proton.getInstance().runBackboneRedisCommand(jedis -> {
            jedis.hgetAll("vpnWhitelist").forEach((uuid, issuerUuid) -> {
                String name = Proton.getInstance().getUuidCache().name(UUID.fromString(uuid));
                String issuer = Proton.getInstance().getUuidCache().name(UUID.fromString(issuerUuid));

                sj.add(AQUA + name + GRAY + " (" + issuer + ")" + YELLOW);
            });
            return null;
        });

        player.sendMessage(sj.toString());
    }
}
