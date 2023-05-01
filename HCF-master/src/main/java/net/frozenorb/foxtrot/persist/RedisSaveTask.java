package net.frozenorb.foxtrot.persist;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.Proton;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(null, false);
    }

    public static int save(final CommandSender issuer, final boolean forceAll) {
        long startMs = System.currentTimeMillis();
        int teamsSaved = Proton.getInstance().runRedisCommand(redis -> {

            DBCollection teamsCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
            
            int changed = 0;

            for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                if (team.isNeedsSave() || forceAll) {
                    changed++;

                    redis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                    teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                }
                
                if (forceAll) {
                    for (UUID member : team.getMembers()) {
                        Foxtrot.getInstance().getTeamHandler().setTeam(member, team, true);
                    }
                }
            }

            redis.set("RostersLocked", String.valueOf(Foxtrot.getInstance().getTeamHandler().isRostersLocked()));
            if (issuer != null && forceAll) redis.save();
            return (changed);
        });

        int time = (int) (System.currentTimeMillis() - startMs);

        if (teamsSaved != 0) {
            System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");

            if (issuer != null) {
                issuer.sendMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
            }
        }

        return (teamsSaved);
    }

}