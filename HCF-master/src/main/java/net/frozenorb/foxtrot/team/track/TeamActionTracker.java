package net.frozenorb.foxtrot.team.track;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.io.Files;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import com.mongodb.client.MongoCollection;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;

import org.bson.Document;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public final class TeamActionTracker {

    @Getter @Setter private static boolean databaseLogEnabled = true;
    @Getter private static final MongoCollection<Document> mongoCollection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("TeamActions");
    private static final File logFileRoot = new File(new File("foxlogs"), "teamactiontracker");

    public static void logActionAsync(Team team, TeamActionType actionType, Map<String, Object> params) {
        if (team.isLoading()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            logActionToFile(team, actionType, params);

            if (databaseLogEnabled && actionType.isLoggedToDatabase()) {
                logActionToDatabase(team, actionType, params);
            }
        });
    }

    private static void logActionToFile(Team team, TeamActionType actionType, Map<String, Object> params) {
        File teamLogFolder = new File(logFileRoot, team.getUniqueId().toString());
        File teamLogFile = new File(teamLogFolder, (actionType.isLoggedToDatabase() ? "general" : "misc") + ".log");

        try {
            StringBuilder logLine = new StringBuilder();

            logLine.append('[');
            logLine.append(DateTimeFormatter.ISO_INSTANT.format(Instant.now())); // ISO 8601
            logLine.append(", ");
            logLine.append(actionType.getInternalName());
            logLine.append("] ");

            params.forEach((key, value) -> {
                logLine.append(key);
                logLine.append(": ");
                logLine.append(value);
                logLine.append(' ');
            });

            logLine.append('\n');

            teamLogFile.getParentFile().mkdirs();
            teamLogFile.createNewFile();

            Files.append(
                logLine.toString(),
                teamLogFile,
                Charsets.UTF_8
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void logActionToDatabase(Team team, TeamActionType actionType, Map<String, Object> params) {
        BasicDBObject entry = new BasicDBObject();

        entry.put("teamId", team.getUniqueId().toString());
        entry.put("teamName", team.getName());
        entry.put("time", new Date());
        entry.put("type", actionType.name());

        BasicDBObject paramsJson = new BasicDBObject();

        // we manually serialize this so we use .toString
        // instead of Mongo's serialization (ex UUID -> binary)
        params.forEach((key, value) -> paramsJson.put(key, value.toString()));

        entry.put("params", paramsJson);

        // we embed the entire team json here :(
        // this is bad and uses a lot of data, but
        // the web dev team (Ariel) will have to do
        // less work if we embed it.
        entry.put("teamAfterAction", team.toJSON());

        DB db = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME);
        db.getCollection("TeamActions").insert(entry);
    }

}