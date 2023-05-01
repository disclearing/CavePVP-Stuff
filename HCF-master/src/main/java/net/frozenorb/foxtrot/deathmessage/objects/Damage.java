package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;

public abstract class Damage {

    @Getter private String damaged;
    @Getter private double damage;
    @Getter private long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public abstract String getDeathMessage();

    public String wrapName(String player) {
        int kills = Foxtrot.getInstance().getMapHandler().isKitMap() ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills() : Foxtrot.getInstance().getKillsMap().getKills(UUIDUtils.uuid(player));
        return (ChatColor.RED + player + ChatColor.DARK_RED + "[" + kills + "]" + ChatColor.YELLOW);
    }

    public long getTimeDifference() {
        return System.currentTimeMillis() - time;
    }

}