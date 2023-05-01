package cc.fyre.neutron.rank;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.listener.RankListener;
import cc.fyre.neutron.rank.packet.RankDeletePacket;
import cc.fyre.neutron.rank.packet.RankUpdatePacket;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.rank.comparator.RankWeightComparator;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import lombok.Getter;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.Packet;

import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import cc.fyre.proton.uuid.UUIDCache;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RankHandler implements PacketListener {

    @Getter private Map<UUID,Rank> cache = new HashMap<>();

    @Getter private Neutron instance;

    @Getter private MongoCollection<Document> collection;

    @Getter private Rank defaultRank;

    public RankHandler(Neutron instance) {
        this.instance = instance;
        this.collection = instance.getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.RANKS_COLLECTION);

        instance.getServer().getPluginManager().registerEvents(new RankListener(),instance);

        Proton.getInstance().getPidginHandler().registerPacket(RankDeletePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(RankUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerListener(this);

        if (!instance.getMongoHandler().doesCollectionExist(NeutronConstants.RANKS_COLLECTION)) {
            this.defaultRank = new Rank(UUID.randomUUID(),"Default",UUIDCache.CONSOLE_UUID);
            this.cache.put(this.defaultRank.getUuid(),this.defaultRank);
            this.save(this.defaultRank);
            return;
        }

        //We have to load the inherits after all ranks have been loaded as some ranks may have not been loaded yet, causing some inherits to not load.
        final Map<UUID,List<UUID>> inherits = new HashMap<>();

        for (Document document : this.collection.find()) {

            final Rank rank = new Rank(UUID.fromString(document.getString("uuid")),document.getString("name"),UUIDCache.CONSOLE_UUID);

            final List<UUID> rankInherits = Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("inherits"),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());

            inherits.put(rank.getUuid(),rankInherits);

            this.load(rank);

            if (rank.getName().equalsIgnoreCase("Default")) {
                this.defaultRank = rank;
            }

        }

        for (Map.Entry<UUID,List<UUID>> entry : inherits.entrySet()) {

            final Rank rank = this.fromUuid(entry.getKey());

            rank.getInherits().clear();
            rank.getInherits().addAll(entry.getValue().stream().map(this::fromUuid).filter(inherit -> inherit != null).collect(Collectors.toList()));
        }

    }

    public void load(Rank rank) {

        final Document document = this.findDocument(rank);

        if (document == null) {
            this.save(rank);
            this.cache.put(rank.getUuid(),rank);
            return;
        }

        rank.setName(document.getString("name"));
        rank.getWeight().set(document.getInteger("weight"));

        if (document.containsKey("evictionRate")) {
            rank.setEvictionRate(document.getLong("evictionRate"));
        } else {
            rank.setEvictionRate(TimeUnit.MINUTES.toMillis(3L));
        }

        //rank.getWeight().set(document.getInteger("gemMultiplier"));
        rank.setPrefix(ChatColor.translateAlternateColorCodes('&',document.getString("prefix")));
        rank.setColor(ChatColor.valueOf(document.getString("color")));
        rank.setSecondColor(document.containsKey("secondColor") ? ChatColor.valueOf(document.getString("secondColor")):null);
        rank.setCreatedAt(document.getLong("createdAt"));
        rank.setCreatedBy(UUID.fromString(document.getString("createdBy")));
        rank.setMetaData(Proton.PLAIN_GSON.fromJson(document.getString("metaData"),JsonObject.class));
        rank.setInherits(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("inherits"),ArrayList.class).stream().map(UUID::fromString).map(this::fromUuid).filter(Objects::nonNull).collect(Collectors.toList()));
        rank.setPermissions(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("permissions"),ArrayList.class));

        this.cache.put(rank.getUuid(),rank);
    }

    public void save(Rank rank) {
        this.save(rank,new ArrayList<>());
    }

    public void save(Rank rank,Packet packet) {
        this.save(rank,Collections.singletonList(packet));
    }

    public void save(Rank rank,List<Packet> packets) {

        final Document document = new Document();

        document.put("uuid",rank.getUuid().toString());
        document.put("name",rank.getName());
        document.put("weight",rank.getWeight().get());
        document.put("gemMultiplier",rank.getWeight().get());
        document.put("prefix",rank.getPrefix());
        document.put("color",rank.getColor().name());

        if (rank.getSecondColor() != null) {
            document.put("secondColor",rank.getSecondColor().name());
        }

        document.put("evictionRate", rank.getEvictionRate());
        document.put("createdAt",rank.getCreatedAt());
        document.put("createdBy",rank.getCreatedBy().toString());
        document.put("metaData",Proton.PLAIN_GSON.toJson(rank.getMetaData()));
        document.put("permissions",Proton.PLAIN_GSON.toJson(rank.getPermissions()));
        document.put("inherits",Proton.PLAIN_GSON.toJson(rank.getInherits().stream().map(Rank::getUuid).map(UUID::toString).collect(Collectors.toList())));

        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance,() -> {

            // replace the document before we update the rank.
            this.collection.replaceOne(Filters.eq("uuid",rank.getUuid().toString()),document,new ReplaceOptions().upsert(true));

            // we do this so we can send a packet before the actual rank updates.
            if (!packets.isEmpty()) {
                packets.stream().filter(packet -> !(packet instanceof RankUpdatePacket)).forEach(xPacket -> Proton.getInstance().getPidginHandler().sendPacket(new RankUpdatePacket(rank.getUuid())));
            }

            // finally, send the update packet.
            Proton.getInstance().getPidginHandler().sendPacket(new RankUpdatePacket(rank.getUuid()));
        });

    }

    public void delete(Rank rank) {
        this.delete(rank,new ArrayList<>());
    }

    public void delete(Rank rank,Packet packet) {
        this.delete(rank,Arrays.asList(packet));
    }

    public void delete(Rank rank,List<Packet> packets) {

        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance,() -> {

            this.collection.deleteOne(Filters.eq("uuid",rank.getUuid().toString()));

            if (!packets.isEmpty()) {
                packets.stream().filter(packet -> !(packet instanceof RankUpdatePacket)).forEach(Proton.getInstance().getPidginHandler()::sendPacket);
            }

            Proton.getInstance().getPidginHandler().sendPacket(new RankDeletePacket(rank.getUuid()));

        });

    }

    public Document findDocument(Rank rank) {
        return this.collection.find(Filters.eq("uuid",rank.getUuid().toString())).first();
    }

    public Rank fromUuid(UUID uuid) {
        return this.cache.get(uuid);
    }

    public Rank fromName(String name) {
        return this.cache.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public List<Rank> getSortedValueCache() {
        return Collections.unmodifiableList(this.cache.values().stream().sorted(new RankWeightComparator().reversed().thenComparing(new RankWeightComparator())).collect(Collectors.toList()));
    }

    @IncomingPacketHandler
    public void onRankUpdate(RankUpdatePacket packet) {

        Rank rank = Neutron.getInstance().getRankHandler().fromUuid(packet.uuid());

        if (rank == null) {
            rank = new Rank(packet.uuid(),null,null);
        }

        rank.load();

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(NeutronConstants.MONITOR_PREFIX + " " + rank.getFancyName() + ChatColor.GRAY + " -> " + ChatColor.WHITE + "updated");
        }

    }

    @IncomingPacketHandler
    public void onRankDelete(RankDeletePacket packet) {

        final UUID uuid = packet.uuid();

        final Rank rank = Neutron.getInstance().getRankHandler().getCache().get(uuid);

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(loopPlayer.getUniqueId());

            profile.getGrants().stream().filter(grant -> grant.getRankUuid().equals(rank.getUuid())).collect(Collectors.toList()).forEach(grant -> profile.getGrants().remove(grant));
            profile.recalculateGrants();
            profile.save();

            if (!loopPlayer.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(NeutronConstants.MONITOR_PREFIX + " " + rank.getFancyName() + ChatColor.GRAY + " -> "+ChatColor.WHITE + "deleted.");
        }

        Neutron.getInstance().getRankHandler().getCache().remove(uuid);

    }

    public int getMenuRows() {

        final int ranks = this.cache.values().size();

        if (ranks < 9) {
            return 1;
        } else if (ranks >= 9 && ranks < 18) {
            return 2;
        } else if (ranks >= 18 && ranks < 27) {
            return 3;
        } else if (ranks >= 27 && ranks < 36) {
            return 4;
        } else if (ranks >= 36 && ranks < 45) {
            return 5;
        } else if (ranks >= 45 && ranks < 54) {
            return 6;
        }

        return 1;
    }

}
