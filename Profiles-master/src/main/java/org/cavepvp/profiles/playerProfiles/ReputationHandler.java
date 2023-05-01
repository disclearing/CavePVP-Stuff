package org.cavepvp.profiles.playerProfiles;

import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.impl.Brackets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReputationHandler {
    private Profiles instance;

    public ReputationHandler(Profiles instance) {
        this.instance = instance;
    }

    public Brackets findBracket(UUID uuid, String name) {
        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(uuid, name);

        if (playerProfile == null) {
            return Brackets.UNRANKED;
        }

        final List<UUID> cache = new ArrayList<>(this.instance.getLeaderboardHandler().getSortedMap().keySet());
        int position = cache.indexOf(uuid);

        if (position == 1 || position == 2 || position == 3 || position == 4 || position == 5) {
            System.out.println(playerProfile.getName() + " is pos " + position);
            return Brackets.CHAMPION;
        }

        return findBracket(playerProfile);
    }

    public Brackets findBracket(PlayerProfile playerProfile) {
        if (playerProfile == null) {
            return Brackets.UNRANKED;
        }

        final List<UUID> cache = new ArrayList<>(this.instance.getLeaderboardHandler().getSortedMap().keySet());
        int position = cache.indexOf(playerProfile.getUuid());

        if (position == 1 || position == 2 || position == 3 || position == 4 || position == 5) {
            System.out.println(playerProfile.getName() + " is pos " + position);
            return Brackets.CHAMPION;
        }

        double reputation = playerProfile.getPlayerReputation();

        return Arrays.stream(Brackets.values()).filter(it -> it.getMinReputation() <= reputation && it.getMaxReputation() >= reputation).findFirst().orElse(Brackets.UNRANKED);
    }

    public double addReputation(UUID uuid, String name, double amount) {
        if (!UniverseAPI.getServerName().equalsIgnoreCase("Fasts")) {
            return 0;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(uuid, name);

        final Brackets beforeBracket = findBracket(playerProfile);

        if (playerProfile == null) {
            return 0;
        }

        if (playerProfile.getReputation() > 0) {
            playerProfile.setPlayerReputation(playerProfile.getPlayerReputation()+(playerProfile.getReputation()*0.05));
            playerProfile.setReputation(0);
        }

        playerProfile.setPlayerReputation(playerProfile.getPlayerReputation()+amount);

        final Brackets afterBracket = findBracket(playerProfile);

        if (!beforeBracket.equals(afterBracket)) {
            final Player player = Profiles.getInstance().getServer().getPlayer(uuid);

            if (player != null) {
                player.sendMessage("");
                player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Rank Up");
                player.sendMessage(ChatColor.translate("&7You have leveled up into a new bracket!"));
                player.sendMessage(ChatColor.translate("&cYou are now in the &f" + afterBracket.getChatColor() + afterBracket.getDisplayName() + " &cbracket!"));
                player.sendMessage("");

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            }
        }

        double currentReputation = playerProfile.getPlayerReputation();

        if (currentReputation <= 0) {
            playerProfile.setPlayerReputation(0);
        }

        playerProfile.save();

        return playerProfile.getPlayerReputation();
    }

    public double takeReputation(UUID uuid, String name, double amount) {
        return addReputation(uuid, name, amount*-1);
    }

}
