package net.frozenorb.foxtrot.team.upgrade;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class UpgradeTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

            final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(onlinePlayer);

            if (team == null) {
                continue;
            }

            if (team.getPurchasedEffects().isEmpty()) {
                continue;
            }

            final Team teamAt = LandBoard.getInstance().getTeam(onlinePlayer.getLocation());

            if (teamAt == null || !teamAt.equals(team)) {
                continue;
            }

            for (PotionEffectType purchasedEffect : team.getPurchasedEffects()) {
                if (onlinePlayer.hasPotionEffect(purchasedEffect)) {
                    continue;
                }

                onlinePlayer.addPotionEffect(new PotionEffect(purchasedEffect, 20*6, (purchasedEffect.equals(PotionEffectType.SPEED) ? 1 : 0)));
            }
        }
    }
}
