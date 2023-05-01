package cc.fyre.piston.command.player;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.piston.Piston;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand {

    @Command(
            names = {"list","online","players"},
            permission = ""
    )
    public static void execute(CommandSender sender) {

        if (!sender.isOp()) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        sender.sendMessage(StringUtils.join(Neutron.getInstance().getRankHandler().getSortedValueCache()
                .stream()
                .filter(it -> !it.hasMetaData("HIDDEN"))
                .map(Rank::getFancyName)
                .collect(Collectors.toList()), new StringBuilder().append(ChatColor.WHITE).append(", ").toString())
        );

        sender.sendMessage(ChatColor.WHITE + "(" + getSortedPlayers(sender,true).size() + "/" + Piston.getInstance().getServer().getMaxPlayers() + ")" + " " + "[" + StringUtils.join(getSortedPlayers(sender,true).stream()
                        .map(Player::getDisplayName)
                        .collect(Collectors.toList()),
                ChatColor.WHITE + ", ") + ChatColor.WHITE + "]"
        );

        if (Piston.getInstance().getServer().getOnlinePlayers().size() > 600) {
            sender.sendMessage(ChatColor.RED + "List may not show all players as there seems to be a cap at 600 players. (including vanished staff)");
        }

    }

    public static List<Player> getSortedPlayers(CommandSender sender,boolean canSeeOnly) {

        final List<Player> toReturn = new ArrayList<>();

        for (Player loopPlayer : Piston.getInstance().getServer().getOnlinePlayers()) {

            if ((sender instanceof Player) && canSeeOnly && !Proton.getInstance().getVisibilityHandler().treatAsOnline(loopPlayer,(Player)sender)) {
                continue;
            }

            toReturn.add(loopPlayer);
        }


        return toReturn.stream().sorted(new PlayerComparator().reversed()).collect(Collectors.toList());
    }

    static class PlayerComparator implements Comparator<Player> {

        @Override
        public int compare(Player player,Player otherPlayer) {
            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
            final Profile otherProfile = Neutron.getInstance().getProfileHandler().fromUuid(otherPlayer.getUniqueId());

            return Integer.compare(profile == null ? 0 : profile.getActiveGrant().getRank().getWeight().get(),otherProfile == null ? 0 : otherProfile.getActiveGrant().getRank().getWeight().get());
        }
    }

}
