package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.PlayerType;

import java.util.Arrays;

public class SoundsCommand {

    @Command(
            names = {"sounds"},
            permission = ""
    )
    public static void execute(Player player) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final PlayerType nextType = Arrays.stream(PlayerType.values()).filter(it -> it.getNumber() == (profile.getPreferences2().getSounds().getNumber()+1)).findFirst().orElse(PlayerType.EVERYONE);
        profile.getPreferences2().setSounds(nextType);
        profile.save();

        player.sendMessage(ChatColor.GOLD + "Sounds: " + nextType.getDisplayName());
    }

}
