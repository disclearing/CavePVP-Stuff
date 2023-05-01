package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class PlayTimeChallenge extends Challenge {

    private long playTime;

    public PlayTimeChallenge(String id, String name, int experience, boolean daily, long playTime) {
        super(id, name, experience, daily);

        this.playTime = playTime;
    }

    @Override
    public Type getAbstractType() {
        return PlayTimeChallenge.class;
    }

    @Override
    public String getText() {
        return "Reach " + TimeUtils.formatIntoDetailedString((int) (playTime / 1000)) + " of play time";
    }

    @Override
    public boolean hasStarted(Player player) {
        return true;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId()) + Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId()) >= playTime;
    }

    @Override
    public String getProgressText(Player player) {
        long playerTime = Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId()) + Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId());
        long remaining = playTime - playerTime;
        return "You need to play for another " + TimeUtils.formatIntoDetailedString((int) (remaining / 1000L)) + ".";
    }

}
