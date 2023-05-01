package org.cavepvp.profiles.playerProfiles;

import cc.fyre.proton.Proton;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import org.bson.Document;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.packet.type.ProfileUpdatePacket;
import org.cavepvp.profiles.playerProfiles.impl.Comment;
import org.cavepvp.profiles.playerProfiles.impl.Message;
import org.cavepvp.profiles.playerProfiles.listener.ProfileListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerProfileHandler {
    private Profiles instance;

    @Getter private Map<UUID, PlayerProfile> cache = new HashMap<>();
    @Getter private List<Comment> comments = new ArrayList<>();
    @Getter private List<Message> messages = new ArrayList<>();

    @Getter private MongoCollection<Document> collection;
    @Getter private MongoCollection<Document> commentCollection;
    @Getter private MongoCollection<Document> messageCollection;

    public PlayerProfileHandler(Profiles instance) {
        this.instance = instance;

        this.collection = this.instance.getDatabaseHandler().getMongoDatabase().getCollection("profiles");

        instance.getServer().getPluginManager().registerEvents(new ProfileListener(this.instance),this.instance);
    }

    public UpdateResult update(PlayerProfile playerProfile) {
        if (cache.containsKey(playerProfile.getUuid())) {
            cache.replace(playerProfile.getUuid(), playerProfile);
        }

        if (this.instance.getServer().isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> this.updateMethod(playerProfile)).join();
        }

        return this.updateMethod(playerProfile);
    }

    private UpdateResult updateMethod(PlayerProfile playerProfile) {
        final Document document = Document.parse(Profiles.GSON.toJson(playerProfile));

        final UpdateResult updateResult = this.collection.updateOne(new Document("_id", playerProfile.getUuid().toString()),new Document("$set",document),new UpdateOptions().upsert(true));

        Proton.getInstance().getPidginHandler().sendPacket(new ProfileUpdatePacket(playerProfile.getUuid()));

        return updateResult;
    }

    public Optional<PlayerProfile> fromUuid(UUID uuid) {
        return Optional.ofNullable(this.cache.get(uuid));
    }

    public Optional<PlayerProfile> fromName(String name) {
        return this.cache.values().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<PlayerProfile> fromDatabase(UUID uuid) {

        if (this.instance.getServer().isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> this.fromDatabaseMethod(uuid)).join();
        }

        return this.fromDatabaseMethod(uuid);
    }

    public Optional<PlayerProfile> fromDatabase(String name) {

        if (this.instance.getServer().isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> this.fromDatabaseMethod(name)).join();
        }

        return this.fromDatabaseMethod(name);
    }

    private Optional<PlayerProfile> fromDatabaseMethod(UUID uuid) {

        final Document document = this.collection.find(Filters.eq("_id", uuid.toString())).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Profiles.GSON.fromJson(document.toJson(), PlayerProfile.class));
    }

    private Optional<PlayerProfile> fromDatabaseMethod(String name) {

        final Document document = this.collection.find(Filters.eq("name",name)).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Profiles.GSON.fromJson(document.toJson(), PlayerProfile.class));
    }

    public Optional<PlayerProfile> requestProfile(String name) {

        final Optional<PlayerProfile> fromName = this.fromName(name);

        if (fromName.isPresent()) {
            return fromName;
        }

        return this.fromDatabase(name);
    }

    public Optional<PlayerProfile> requestProfile(UUID uuid) {

        if (this.cache.containsKey(uuid)) {
            return Optional.of(this.cache.get(uuid));
        }

        return this.fromDatabase(uuid);
    }

    public PlayerProfile fetchProfile(UUID uuid, String name) {

        if (this.cache.containsKey(uuid)) {
            return this.cache.get(uuid);
        }

        final Optional<PlayerProfile> toReturn = this.fromDatabase(uuid);

        if (toReturn.isPresent()) {
            this.cache.put(toReturn.get().getUuid(), toReturn.get());
            return toReturn.get();
        }

        final PlayerProfile playerProfile = new PlayerProfile(uuid,name);

        this.cache.put(uuid, playerProfile);
        this.updateMethod(playerProfile);

        return playerProfile;
    }

}
