package net.frozenorb.foxtrot.server.keyalls.task;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.UniverseAPI;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KeyAllService extends BukkitRunnable {
    private Foxtrot instance;
    private KeyAllHandler keyAllHandler;

    public KeyAllService(Foxtrot instance, KeyAllHandler keyAllHandler) {
        this.instance = instance;
        this.keyAllHandler = keyAllHandler;
    }

    @Override
    public void run() {
        final Server server = this.instance.getServer();
        final Map<String, KeyAll> queue = new HashMap<>(this.keyAllHandler.getCache());

        for (Map.Entry<String, KeyAll> entry : queue.entrySet()) {
            final KeyAll keyAll = entry.getValue();

            long remainingEnd = keyAll.getEnd()-System.currentTimeMillis();
            long remainingStart = keyAll.getGiveAllTime()-System.currentTimeMillis();

            int startSeconds = (int) (remainingStart/1000);

            if (keyAll.getGiveAllTime() != 0 && startSeconds <= 0) {
                keyAll.setEnd(System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
                keyAll.setGiveAllTime(0);
                keyAll.setGiving(true);

                this.keyAllHandler.getCache().replace(entry.getKey(), keyAll);

                for (Player onlinePlayer : server.getOnlinePlayers()) {
                    onlinePlayer.sendMessage("");
                    new FancyMessage(ChatColor.translate(keyAll.getDisplayName())).command("/keyall").tooltip(ChatColor.GREEN + "Click to redeem").send(onlinePlayer);
                    new FancyMessage(ChatColor.translate("&fA key-all is now available to you!")).command("/keyall").tooltip(ChatColor.GREEN + "Click to redeem").send(onlinePlayer);
                    new FancyMessage(ChatColor.translate("&aType /keyall to redeem the rewards! You have 5 minutes!")).command("/keyall").tooltip(ChatColor.GREEN + "Click to redeem").send(onlinePlayer);
                    onlinePlayer.sendMessage("");
                }
                continue;
            }

            if (keyAll.getGiveAllTime() != 0 && reminders.contains(startSeconds)) {
                final FancyMessage fancyMessage = new FancyMessage("§8[§4Alert§8] ");
                fancyMessage.then(
                        keyAll.getDisplayName() + " §7on §f" + UniverseAPI.getServerName() + " §7will take place in §f" + TimeUtils.formatIntoDetailedString(startSeconds) + "§7!");
                fancyMessage.command("/joinqueue " + UniverseAPI.getServerName()).tooltip(ChatColor.GREEN + "Click here to join");

                fancyMessage.send(Foxtrot.getInstance().getServer().getConsoleSender());

                for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    fancyMessage.send(onlinePlayer);
                }

//                Neutron.getInstance().sendMessageToNetwork(fancyMessage);
            }
            
            if (keyAll.getEnd() != 0 && remainingEnd <= 0) {
                keyAll.setGiving(false);
                this.keyAllHandler.getCache().remove(entry.getKey());
            }
        }
    }

    public List<Integer> reminders = Arrays.asList(1,2,3,4,5,10,30,60,120,300, 600, 1800, 3600, 7200);
}
