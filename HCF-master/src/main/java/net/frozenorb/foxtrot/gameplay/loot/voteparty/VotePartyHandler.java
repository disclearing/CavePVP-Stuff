package net.frozenorb.foxtrot.gameplay.loot.voteparty;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.listener.VoteListener;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class VotePartyHandler {
    private Foxtrot instance;

    @Getter @Setter private int currentVotes;
    @Getter @Setter private int votePartyRequirement = 300;

    public VotePartyHandler(Foxtrot instance) {
        this.instance = instance;

        this.instance.getServer().getPluginManager().registerEvents(new VoteListener(), this.instance);

        this.currentVotes = this.instance.getConfig().getInt("voteParty.currentVotes", 0);
    }

    public void saveData() {
        this.instance.getConfig().set("voteParty.currentVotes", this.currentVotes);
    }

    public void addVote() {
        this.currentVotes++;

        if (this.currentVotes >= this.votePartyRequirement) {
            this.start();
        } else if (this.currentVotes >= this.votePartyRequirement-15) {
            this.instance.getServer().broadcastMessage(ChatColor.translate("&4&lVote Party &8┃ &7We are &f" + (this.votePartyRequirement-this.currentVotes) + " &7votes away from a Vote Party!"));
        }
    }

    public void start() {
        this.currentVotes = 0;

        final Server server = this.instance.getServer();

        server.broadcastMessage("");
        server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Vote Party");
        server.broadcastMessage(ChatColor.WHITE + "We have hit the amount of votes needed to start a party!");
        server.broadcastMessage(ChatColor.GRAY + "Empty one inventory slot and you will be given a key in 10 seconds...");
        server.broadcastMessage("");

        final AtomicInteger seconds = new AtomicInteger(10);

        new BukkitRunnable() {
            @Override
            public void run() {
                server.broadcastMessage(ChatColor.translate("&4&lVote Party &8┃ &7The key will be given out in &f" + seconds.getAndDecrement() + "&e..."));

                if (seconds.get() <= 0) {
                    server.dispatchCommand(server.getConsoleSender(), "cr giveallkey Items 1");
                    server.dispatchCommand(server.getConsoleSender(), "cr giveallkey Gold 3");
                    this.cancel();
                }
            }
        }.runTaskTimer(this.instance, 20, 20);
    }
}
