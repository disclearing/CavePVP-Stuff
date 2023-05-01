package cc.fyre.neutron.prefix;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.prefix.packet.PrefixDeletePacket;
import cc.fyre.neutron.prefix.packet.PrefixUpdatePacket;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.prefix.comparator.PrefixWeightComparator;

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
import java.util.stream.Collectors;

public class PrefixHandler implements PacketListener {

    @Getter private Map<UUID,Prefix> cache = new HashMap<>();

    @Getter private Neutron instance;

    @Getter private MongoCollection<Document> collection;

    public PrefixHandler(Neutron instance) {
        this.instance = instance;
        this.collection = instance.getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.PREFIX_COLLECTION);

        Proton.getInstance().getPidginHandler().registerPacket(PrefixDeletePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(PrefixUpdatePacket.class);

        Proton.getInstance().getPidginHandler().registerListener(this);

        for (Document document : this.collection.find()) {

            final Prefix prefix = new Prefix(UUID.fromString(document.getString("uuid")),document.getString("name"),UUIDCache.CONSOLE_UUID);

            this.load(prefix);
        }

    }

    public void load(Prefix prefix) {

        final Document document = this.findDocument(prefix);

        if (document == null) {
            this.save(prefix);
            cache.put(prefix.getUuid(),prefix);
            return;
        }

        prefix.setName(document.getString("name"));
        prefix.getWeight().set(document.getInteger("weight"));
        prefix.setDisplay(ChatColor.translateAlternateColorCodes('&',document.getString("display")));
        prefix.setCreatedAt(document.getLong("createdAt"));
        prefix.setCreatedBy(UUID.fromString(document.getString("createdBy")));

        cache.put(prefix.getUuid(),prefix);
    }

    public void save(Prefix prefix) {
        this.save(prefix,new ArrayList<>());
    }

    public void save(Prefix prefix,Packet packet) {
        this.save(prefix,Collections.singletonList(packet));
    }

    public void save(Prefix prefix,List<Packet> packets) {

        final Document document = new Document();

        document.put("uuid",prefix.getUuid().toString());
        document.put("name",prefix.getName());
        document.put("weight",prefix.getWeight().get());
        document.put("display",prefix.getDisplay());
        document.put("createdAt",prefix.getCreatedAt());
        document.put("createdBy",prefix.getCreatedBy().toString());

        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance,() -> {

            // replace the document before we update the prefix.
            this.collection.replaceOne(Filters.eq("uuid",prefix.getUuid().toString()),document,new ReplaceOptions().upsert(true));

            // we do this so we can send a packet before the actual prefix updates.
            if (!packets.isEmpty()) {
                packets.stream().filter(packet -> !(packet instanceof PrefixUpdatePacket)).forEach(xPacket -> Proton.getInstance().getPidginHandler().sendPacket(new PrefixUpdatePacket(prefix.getUuid())));
            }

            // finally, send the update packet.
            Proton.getInstance().getPidginHandler().sendPacket(new PrefixUpdatePacket(prefix.getUuid()));
        });

    }

    public void delete(Prefix prefix) {
        this.delete(prefix,new ArrayList<>());
    }

    public void delete(Prefix prefix,Packet packet) {
        this.delete(prefix,Arrays.asList(packet));
    }

    public void delete(Prefix prefix,List<Packet> packets) {

        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance,() -> {

            this.collection.deleteOne(Filters.eq("uuid",prefix.getUuid().toString()));

            if (!packets.isEmpty()) {
                packets.stream().filter(packet -> !(packet instanceof PrefixDeletePacket)).forEach(Proton.getInstance().getPidginHandler()::sendPacket);
            }

            Proton.getInstance().getPidginHandler().sendPacket(new PrefixDeletePacket(prefix.getUuid()));
        });

    }

    public Document findDocument(Prefix prefix) {
        return this.collection.find(Filters.eq("uuid",prefix.getUuid().toString())).first();
    }

    public Prefix fromUuid(UUID uuid) {
        return this.cache.get(uuid);
    }

    public Prefix fromName(String name) {
        return this.cache.values().stream().filter(prefix -> prefix.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public List<Prefix> getSortedValueCache() {
        return Collections.unmodifiableList(this.cache.values().stream().sorted(new PrefixWeightComparator().reversed().thenComparing(new PrefixWeightComparator())).collect(Collectors.toList()));
    }

    @IncomingPacketHandler
    public void onPrefixUpdate(PrefixUpdatePacket packet) {

        Prefix prefix = Neutron.getInstance().getPrefixHandler().fromUuid(packet.uuid());

        if (prefix == null) {
            prefix = new Prefix(packet.uuid(),null,null);
        }

        prefix.load();

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(NeutronConstants.MONITOR_PREFIX + " " + ChatColor.WHITE + prefix.getName() + ChatColor.GRAY + " -> " + ChatColor.WHITE + "updated.");
        }

    }

    @IncomingPacketHandler
    public void onPrefixDelete(PrefixDeletePacket packet) {

        final UUID uuid = packet.uuid();

        final Prefix prefix = Neutron.getInstance().getPrefixHandler().getCache().get(uuid);

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(loopPlayer.getUniqueId());

            if (profile.getActivePrefix() == null || !profile.getActivePrefix().getUuid().equals(prefix.getUuid())) {
                continue;
            }

            profile.setActivePrefix(null);
            profile.save();

            if (!loopPlayer.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(NeutronConstants.MONITOR_PREFIX + " " + ChatColor.WHITE + prefix.getName() + ChatColor.GRAY + " -> " + ChatColor.WHITE + "deleted.");
        }

        Neutron.getInstance().getPrefixHandler().getCache().remove(uuid);
    }


}
