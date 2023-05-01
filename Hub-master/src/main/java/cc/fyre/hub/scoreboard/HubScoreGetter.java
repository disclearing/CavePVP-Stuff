package cc.fyre.hub.scoreboard;

import cc.fyre.hub.Hub;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.scoreboard.construct.ScoreGetter;
import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import com.minexd.quartz.Quartz;
import com.minexd.quartz.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.LinkedList;

public class HubScoreGetter implements ScoreGetter {

    public String[] getScores(LinkedList<String> scores, Player player) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        for (String score : Hub.getInstance().getScoreboardScores()) {

            scores.add(score
                    .replace("{online}", "" + Hub.getInstance().getPlayersOnline())
                    .replace("{secret-online}", Hub.getInstance().getSecretPlayers() + "")
                    .replace("{rank}", profile == null ? "Default" : (profile.hasSubscription() ? ChatColor.GOLD + "✪" + ChatColor.RESET : "") + profile.getActiveGrant().getRank().getFancyName())
            );

        }

        final Neutron.Network network = Neutron.getInstance().getNetwork();

        if (network.equals(Neutron.Network.CRYPTO)) {
            Server server = Universe.getInstance().getUniverseHandler().serverFromName("Prison");

            scores.add("&6&l┃ &fPrison: &e" + server.getOnlinePlayers().get() + "/" + server.getMaximumPlayers().get());

            scores.add("");
            scores.add("&6cryptomc.org");
            return scores.toArray(new String[0]);
        }

        final Queue queue = Quartz.get().getQuartzData().getQueueByPlayer(player.getUniqueId());

        if (queue != null) {
            scores.add("");
            scores.add(ChatColor.translate("&4&lQueued:"));
            scores.add(ChatColor.RED + queue.getName());
            scores.add(ChatColor.translate("&7Position: &f#" + queue.getPosition(player.getUniqueId()) + ChatColor.GRAY + "/" + ChatColor.WHITE + queue.getPlayers().size()));
        }

        scores.add("");
        scores.add(network.getMainColor() + network.getDomain());
        scores.add("&2&7&m--------------------");

        return scores.toArray(new String[0]);
    }
}