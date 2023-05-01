package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.EntityUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class KillEntityChallenge extends Challenge {

    private EntityType entityType;
    private int kills;

    public KillEntityChallenge(String id, String name, int experience, boolean daily, EntityType entityType, int kills) {
        super(id, name, experience, daily);

        this.entityType = entityType;
        this.kills = kills;
    }

    public int getKilled(Player player) {
        return getProgress(player).getEntitiesKilled(entityType, isDaily());
    }

    @Override
    public Type getAbstractType() {
        return KillEntityChallenge.class;
    }

    @Override
    public String getText() {
        return "Kill " + kills + " " + getEntityName();
    }

    @Override
    public boolean hasStarted(Player player) {
        return getKilled(player) > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return getKilled(player) >= kills;
    }

    @Override
    public String getProgressText(Player player) {
        int remaining = kills - getKilled(player);
        return "You need to kill " + Formats.formatNumber(remaining) + " more " + (remaining == 1 ? getEntityName() : getEntityNamePlural()).toLowerCase() + ".";
    }

    private String getEntityName() {
        if (entityType == EntityType.ENDERMAN) {
            return "Endermen";
        } else {
            return EntityUtils.getName(entityType);
        }
    }

    private String getEntityNamePlural() {
        if (entityType == EntityType.ENDERMAN) {
            return "Endermen";
        } else {
            return EntityUtils.getName(entityType) + "s";
        }
    }

}
