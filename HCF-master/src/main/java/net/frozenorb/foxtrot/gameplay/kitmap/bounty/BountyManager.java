package net.frozenorb.foxtrot.gameplay.kitmap.bounty;

import cc.fyre.proton.uuid.UUIDCache;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.listener.BountyListener;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BountyManager {
    @Getter private UUID autoBounty;

    public BountyManager() {
        final Foxtrot instance = Foxtrot.getInstance();

        instance.getServer().getPluginManager().registerEvents(new BountyListener(instance, this), instance);

        new BukkitRunnable() {
            @Override
            public void run() {

//                if (instance.getServer().getOnlinePlayers().size() <= 6) {
//                    return;
//                }

                if (!DisableCommand.bounty) {
                    return;
                }

                if (autoBounty != null && instance.getServer().getPlayer(autoBounty) != null) {
                    return;
                }

                final List<Player> players = instance.getServer().getOnlinePlayers().stream()
                        .filter(it -> getBounty(it) == null && instance.getServerHandler().isWarzone(it.getLocation())).collect(Collectors.toList());

                if (players.isEmpty()) {
                    return;
                }

                final Player randomPlayer = players.get(ThreadLocalRandom.current().nextInt(0,players.size()-1));

                for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage("");
                    onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Bounty");
                    onlinePlayer.sendMessage(ChatColor.translate("&7A random bounty worth &a50 Gems &7has been placed on &f" + randomPlayer.getDisplayName()));
                    onlinePlayer.sendMessage(ChatColor.translate("&cLocation: &f" + randomPlayer.getLocation().getBlockX() + ", " + randomPlayer.getLocation().getBlockZ()));
                    onlinePlayer.sendMessage("");
                }

                autoBounty = randomPlayer.getUniqueId();

                placeBounty(UUIDCache.CONSOLE_UUID, randomPlayer, 50);
            }
        }.runTaskTimer(instance, 20*60, 20*60);
    }

    @Getter
    private final Map<UUID, Bounty> bountyMap = new HashMap<>();

    public void save() {
        bountyMap.forEach((key, value) -> Foxtrot.getInstance().getGemMap().addGemsSync(value.getPlacedBy(), value.getGems()));
        bountyMap.clear();
    }

    public void placeBounty(Player player, Player target, int gems) {
        bountyMap.put(target.getUniqueId(), new Bounty(player.getUniqueId(), gems));
    }

    public void placeBounty(UUID sender, Player target, int gems) {
        bountyMap.put(target.getUniqueId(), new Bounty(sender, gems));
    }

    public Bounty getBounty(Player player) {
        return bountyMap.get(player.getUniqueId());
    }

    public Bounty removeBounty(Player player) {
        return bountyMap.remove(player.getUniqueId());
    }
}
