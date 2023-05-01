package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.util.paginate.FancyPaginatedOutput;
import cc.fyre.proton.util.paginate.PaginatedOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionsMadeRaidableCommand {

    @Command(names = {"fmr", "factionsraidable", "factionsmaderaidable"},async = true, permission = "command.fmr")
    public static void execute(Player player, @Parameter(name = "team")Team team,@Parameter(name = "page",defaultValue = "1")Integer page,@Parameter(name = "time",defaultValue = "0")String timeValue) {
        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
            return;
        }

        final List<Bson> filters = new ArrayList<>();
        final MongoCollection<Document> collection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("TeamActions");

        filters.add(Filters.and(
                Filters.eq("teamId",team.getUniqueId().toString()),
                Filters.eq("type", TeamActionType.MADE_FACTION_RAIDABLE.getInternalName())
        ));

        long time = 0L;

        if (!timeValue.equals("0")) {

            try {
                time = TimeUtils.parseTime(timeValue) * 1000L;
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Invalid time format.");
                return;
            }

        }

        if (time != 0L) {
            filters.add(Filters.gt("time",new Date(System.currentTimeMillis() - time)));
        }

        final List<Document> documents = new ArrayList<>();

        collection.find(Filters.and(filters)).sort(Filters.eq("time",-1)).iterator().forEachRemaining(documents::add);

        if (documents.isEmpty()) {

            if (time != 0L) {
                player.sendMessage(ChatColor.RED + team.getName() + " has not made any factions raidable in the last " + TimeUtils.formatIntoDetailedString((int)(time / 1000L)) + ".");
                return;
            }

            player.sendMessage(ChatColor.RED + team.getName() + " has not made any factions raidable.");
            return;
        }

        player.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",53));

        new PaginatedOutput<Document>() {
            @Override
            public String getHeader(int i, int i1) {
                return ChatColor.GOLD + "You are viewing page " + ChatColor.WHITE + i + "/" + i1 + "\n" + ChatColor.RED + ChatColor.STRIKETHROUGH + StringUtils.repeat("-",53);
            }

            @Override
            public String format(Document data, int i) {

                final BasicDBObject params = BasicDBObject.parse(((Document)data.get("params")).toJson());

                final Team team = params.containsKey("factionRaidable") ? Foxtrot.getInstance().getTeamHandler().getTeam(new ObjectId(params.getString("factionRaidable"))) : null;

                String teamName = team != null ? team.getName() : "Unknown";

                return ChatColor.GREEN + UUIDUtils.name(UUID.fromString(params.getString("killerId"))) + ChatColor.YELLOW + " made " + ChatColor.RED + teamName + ChatColor.DARK_RED + ChatColor.BOLD + " RAIDABLE " + ChatColor.YELLOW + "by killing " + ChatColor.RED  + UUIDUtils.name(UUID.fromString(params.getString("playerId")))  + " " + ChatColor.DARK_GREEN + ChatColor.BOLD + "[" + TimeUtils.formatIntoCalendarString(data.getDate("time")) + "]";
            }

        }.display(player,page,documents);

        player.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",53));
    }

}
