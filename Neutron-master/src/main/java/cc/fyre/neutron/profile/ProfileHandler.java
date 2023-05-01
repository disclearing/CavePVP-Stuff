package cc.fyre.neutron.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.command.profile.holiday.type.HolidayType;
import cc.fyre.neutron.packet.BroadcastPacket;
import cc.fyre.neutron.packet.IPChangePacket;
import cc.fyre.neutron.packet.ManagementBroadcastPacket;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.profile.attributes.Language;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantApplyPacket;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantRemovePacket;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.packet.NoteApplyPacket;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentPardonPacket;
import cc.fyre.neutron.profile.attributes.server.ServerProfile;
import cc.fyre.neutron.profile.disguise.DisguiseProfile;
import cc.fyre.neutron.profile.event.GrantExpireEvent;
import cc.fyre.neutron.profile.packet.AltLimitPacket;
import cc.fyre.neutron.profile.packet.PermissionAddPacket;
import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.security.packet.SecurityAlertUpdatePacket;
import cc.fyre.neutron.util.AntiVPNUtil;
import cc.fyre.neutron.util.DiscordWebhook;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import cc.fyre.proton.util.MojangUtil;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.uuid.UUIDCache;
import cc.fyre.universe.UniverseAPI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProfileHandler implements Listener,PacketListener {

    @Getter private final Map<UUID,Profile> cache = new HashMap<>();
    @Getter private final Map<UUID,String> currentIpSession = new HashMap<>();

    private static final String STAFF_ALERT_WEBHOOK = "https://discord.com/api/webhooks/925948030887288862/m4HVZkNau9Q_aXY3B0b0nDuPnBVHnVNQ8kBWf7kyk9hKmwSFirfoTIUEg7kZC5jhTLwx";

    @Getter private final Neutron instance;

    @Getter private final MongoCollection<Document> collection;
    @Getter private List<String> exempt = Arrays.asList("Dylan_", "iMakeMcVids");

    public ProfileHandler(Neutron instance) {
        this.instance = instance;
        this.collection = instance.getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.PROFILE_COLLECTION);

        Proton.getInstance().getPidginHandler().registerPacket(NoteApplyPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(GrantApplyPacket.class);

        Proton.getInstance().getPidginHandler().registerPacket(PermissionAddPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(BroadcastPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(PermissionRemovePacket.class);

        Proton.getInstance().getPidginHandler().registerPacket(PunishmentExecutePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(PunishmentPardonPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(ManagementBroadcastPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(SecurityAlertUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(IPChangePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(AltLimitPacket.class);

        Proton.getInstance().getPidginHandler().registerListener(new cc.fyre.neutron.packet.listener.PacketListener());
        Proton.getInstance().getPidginHandler().registerListener(this);

        instance.getServer().getPluginManager().registerEvents(this,instance);
        instance.getServer().getScheduler().runTaskTimerAsynchronously(instance,() -> {

            for (Profile profile : this.cache.values()) {

                if (profile.getActiveGrant().isActive()) {
                    continue;
                }

                final Grant oldGrant = profile.getActiveGrant();

                profile.recalculateGrants();

                if (profile.getPlayer() != null) {
                    new GrantExpireEvent(profile,oldGrant,profile.getActiveGrant()).call();
                }

            }
        },20,20);
    }

    public String findDisplayName(UUID uuid) {
        final Profile profile = this.fromUuid(uuid);

        if (profile == null) {
            return UUIDUtils.name(uuid);
        }

        if (profile.getActiveRank() == null) {
            return UUIDUtils.name(uuid);
        }

        return profile.getFancyName();
    }


    public String findDisplayName(UUID uuid, boolean outOfCache) {
        final Profile profile = this.fromUuid(uuid, outOfCache);

        if (profile == null) {
            return UUIDUtils.name(uuid);
        }

        if (profile.getActiveRank() == null) {
            return UUIDUtils.name(uuid);
        }

        return profile.getFancyName();
    }

    public void load(Profile profile) {
        this.load(profile,profile.findDocument());
    }

    public void load(Profile profile,Document document) {

        if (document == null) {

            final Grant grant = new Grant(Neutron.getInstance().getRankHandler().getDefaultRank(),UUIDCache.CONSOLE_UUID,(long)Integer.MAX_VALUE,"Profile Created");

            profile.setActiveGrant(grant);
            profile.getGrants().add(grant);
            profile.getServerProfile().setFirstLogin(System.currentTimeMillis());

            profile.save();
            return;
        }

        if (profile.getName() == null && document.containsKey("name")) {
            profile.setName(document.getString("name"));
        }

        if (document.containsKey("customPrefix")) {
            profile.setCustomPrefix(document.getString("customPrefix"));
        }

        if (profile.getIpAddress() == null && document.containsKey("ipAddress") && document.getInteger("ipAddress") != null) {
            profile.setIpAddress(this.ipIntegerToString(document.getInteger("ipAddress")));
        }

        if (document.containsKey("votes")) {
            profile.setVotes(document.getInteger("votes"));
        }

        if (document.containsKey("serverProfile")) {
            profile.setServerProfile(new ServerProfile(document.get("serverProfile",Document.class)));
        }

        if (document.containsKey("disguiseProfile")) {
            profile.setDisguiseProfile(new DisguiseProfile(document.get("disguiseProfile",Document.class)));
        }

        if (document.containsKey("holidayType")) {
            profile.setHolidayType(HolidayType.valueOf(document.getString("holidayType").toUpperCase()));
        }

        if (document.containsKey("language")) {
            profile.setLanguage(Language.valueOf(document.getString("language").toUpperCase()));
        }

        if (document.containsKey("chatColor")) {
            profile.setChatColor(ChatColor.valueOf(document.getString("chatColor")));
        }

        if (document.containsKey("siblings")) {
            profile.setSiblings(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("siblings"),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        }

        if (document.containsKey("notes")) {
            profile.setNotes(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("notes"),ArrayList.class).stream()
                    .map(Document::parse)
                    .map(Note::new)
                    .collect(Collectors.toList())
            );
        }

        if (document.containsKey("grants")) {
            profile.setGrants(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("grants"),ArrayList.class).stream()
                    .map(Document::parse)
                    .map(Grant::new)
                    .filter(grant -> grant.getRank() != null)
                    .collect(Collectors.toList())
            );
        }

        if (document.containsKey("activeGrant")) {

            final Grant grant = profile.getGrant(UUID.fromString(document.getString("activeGrant")));

            if (grant != null) {
                profile.setActiveGrant(grant);
            }
        }

        if (document.containsKey("activePrefix")) {

            final Prefix prefix = Neutron.getInstance().getPrefixHandler().fromUuid(UUID.fromString(document.getString("activePrefix")));

            if (prefix != null) {
                profile.setActivePrefix(prefix);
            }

        }

        if (document.containsKey("permissions")) {
            profile.setPermissions(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("permissions"),ArrayList.class));
        }

        if (document.containsKey("punishments")) {
            //TODO: Shitty but will do for now
            profile.setPunishments(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("punishments"),ArrayList.class).stream()
                    .map(string -> Document.parse(string).getString("iType").equalsIgnoreCase("NORMAL") ? new Punishment(Document.parse(string)):new RemoveAblePunishment(Document.parse(string)))
                    .collect(Collectors.toList())
            );
        }

        if (document.containsKey("authSecret")) {
            profile.setAuthSecret(document.getString("authSecret"));
        }

        profile.recalculateGrants();
    }

    public void save(Profile profile) {

        final Document document = new Document();

        document.put("uuid",profile.getUuid().toString());
        document.put("name",profile.getName());

        if (profile.getIpAddress() != null) {
            document.put("ipAddress",this.ipStringToInteger(profile.getIpAddress()));
        }

        document.put("serverProfile",profile.getServerProfile().toDocument());

        if (profile.getDisguiseProfile() != null) {
            document.put("disguiseProfile",profile.getDisguiseProfile().toDocument());
        }

        if (profile.getCustomPrefix() != null) {
            System.out.println("Saved custom prefix");
            document.put("customPrefix", profile.getCustomPrefix());
        }

        document.put("chatColor",profile.getChatColor().name());
        document.put("siblings",Proton.PLAIN_GSON.toJson(profile.getSiblings().stream().map(UUID::toString).collect(Collectors.toList())));

        document.put("activeGrant",profile.getActiveGrant().getUuid().toString());
        document.put("votes",profile.getVotes());

        if (profile.getHolidayType() != null) {
            document.put("holidayType", profile.getHolidayType().name());
        }

        if (profile.getLanguage() != null) {
            document.put("language", profile.getLanguage().name());
        }

        if (profile.getActivePrefix() != null) {
            document.put("activePrefix",profile.getActivePrefix().getUuid().toString());
        }

        document.put("notes",Proton.PLAIN_GSON.toJson(profile.getNotes().stream().map(note -> note.toDocument().toJson()).collect(Collectors.toList())));
        document.put("grants",Proton.PLAIN_GSON.toJson(profile.getGrants().stream().map(grant -> grant.toDocument().toJson()).collect(Collectors.toList())));
        document.put("permissions",Proton.PLAIN_GSON.toJson(profile.getPermissions()));
        document.put("punishments",Proton.PLAIN_GSON.toJson(profile.getPunishments().stream().map(iPunishment -> iPunishment.toDocument().toJson()).collect(Collectors.toList())));

        if (profile.getAuthSecret() != null) {
            document.put("authSecret",profile.getAuthSecret());
        }

        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance,() ->
                this.collection.replaceOne(Filters.eq("uuid",profile.getUuid().toString()),document,new ReplaceOptions().upsert(true))
        );

    }

    public int ipStringToInteger(String ip) {

        int value = 0;

        final String[] parts = ip.split("\\.");

        for (String part : parts) {
            value = (value << 8) + Integer.parseInt(part);
        }

        return value;
    }

    public String ipIntegerToString(int ip) {

        final String[] parts = new String[4];

        for (int i = 0; i < 4; i++) {
            parts[3 - i] = Integer.toString(ip & 0xff);
            ip >>= 8;
        }

        return parts[0] + '.' + parts[1] + '.' + parts[2] + '.' + parts[3];
    }

    public CompletableFuture<List<Document>> findAltsAsync(Profile profile) {
        return CompletableFuture.supplyAsync(() -> this.findAlts(profile));
    }

    public Profile fetchProfile(UUID uuid, String name) {

        if (this.cache.containsKey(uuid)) {
            return this.cache.get(uuid);
        }

        final Optional<Profile> toReturn = this.fromDatabase(uuid);

        if (toReturn.isPresent()) {
            this.cache.put(toReturn.get().getUuid(), toReturn.get());
            return toReturn.get();
        }

        return new Profile(uuid,name);
    }

    public Optional<Profile> fromDatabase(UUID uuid) {

        if (this.instance.getServer().isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> this.fromDatabaseMethod(uuid)).join();
        }

        return this.fromDatabaseMethod(uuid);
    }

    private Optional<Profile> fromDatabaseMethod(UUID uuid) {

        final Document document = this.collection.find(Filters.eq("_id", uuid.toString())).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Proton.GSON.fromJson(document.toJson(), Profile.class));
    }

    public List<Document> findAlts(Profile profile) {

        final List<Document> toReturn = new ArrayList<>();

        for (Document document : this.collection.find(Filters.eq("ipAddress",this.ipStringToInteger(profile.getIpAddress())))) {

            final UUID uuid = UUID.fromString(document.getString("uuid"));

            if (profile.getUuid().equals(uuid)) {
                continue;
            }

            toReturn.add(document);
        }

        return toReturn;

    }

    public Profile fromUuid(UUID uuid) {
        return this.fromUuid(uuid,false);
    }

    public Profile fromUuid(UUID uuid,boolean outsideOfCache) {

        if (this.cache.containsKey(uuid)) {
            return this.cache.get(uuid);
        }

        if (outsideOfCache) {
            return new Profile(uuid,null);
        }

        return null;
    }

    public Profile fromName(String name) {
        return this.fromName(name,false,false);
    }

    public Profile fromName(String name,boolean requestMojangAPI,boolean async) {
        return this.fromName(name,requestMojangAPI,requestMojangAPI,async);
    }

    public Profile fromName(String name,boolean requestUuidCache,boolean requestMojangAPI,boolean async) {

        final Profile cachedProfile = this.cache.values().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).findAny().orElse(null);

        if (cachedProfile != null) {
            return cachedProfile;
        }

        if (requestUuidCache) {

            if (async) {
                return this.fromUuidCacheAsync(name,requestMojangAPI).join();
            }

            return this.fromUuidCache(name,requestMojangAPI);
        }

        return null;
    }

    private Profile fromUuidCache(String name,boolean requestMojangAPI) {

        final UUID uuid = UUIDUtils.uuid(name);

        if (uuid != null) {
            return new Profile(uuid,name);
        }

        if (!requestMojangAPI) {
            return null;
        }

        try {
            final UUID uuidRequest = MojangUtil.getFromMojang(name);

            if (uuidRequest == null) {
                return null;
            }

            //Cache him inside redis so we don't have to send a request to Mojang for this player again
            Proton.getInstance().getUuidCache().updateAll(uuidRequest,name);

            return new Profile(uuidRequest,name);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public CompletableFuture<Profile> fromUuidCacheAsync(String name,boolean requestMojangAPI) {
        return CompletableFuture.supplyAsync(() -> this.fromUuidCache(name,requestMojangAPI));
    }

    //TODO: better way of fetching banned alts
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {

        final Profile profile = new Profile(event.getUniqueId(),event.getName());

        if (!exempt.contains(event.getName().toLowerCase()) && profile.getEffectivePermissions().contains(NeutronConstants.STAFF_PERMISSION)) {
            final String previousIp = profile.getIpAddress();
            final String currentIp = event.getAddress().getHostAddress();

            if (!previousIp.equalsIgnoreCase(currentIp)) {
                try {
                    final AntiVPNUtil.Result afterResult = AntiVPNUtil.getResult(currentIp);
                    final AntiVPNUtil.Result beforeResult = AntiVPNUtil.getResult(profile.getIpAddress());

                    Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), null, AlertType.IP_CHANGE, true, "New Address: " + currentIp + " [" + afterResult.getCountry() + "]", "Old Address: " + profile.getIpAddress() + " [" + beforeResult.getCountry() + "]");
                } catch (IOException ignored) {
                }

                Neutron.getInstance().sendPacketAsync(new ManagementBroadcastPacket(NeutronConstants.MANAGER_PERMISSION, event.getUniqueId(), ChatColor.translateAlternateColorCodes('&',
                        ChatColor.translateAlternateColorCodes('&',"&4&l[ALERT] &f" + event.getName() + " &chas logged on a new IP address!")
                )));
            }
        }

        profile.setIpAddress(event.getAddress().getHostAddress());

        RemoveAblePunishment punishment = null;

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
            punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);
        } else if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
            punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BAN);
        }

        if (punishment != null && !UniverseAPI.getServerName().equalsIgnoreCase("Banned")) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "Your account is " + punishment.getPunishType().getExecutedContext() + " from the " + Neutron.getInstance().getNetwork().getNetworkName() + " Network"
                    + (punishment.isPermanent() ? "":"\n\n" + ChatColor.RED + "Expires: " + ChatColor.YELLOW + punishment.getRemainingString() + "\n\n" + ChatColor.RED + (punishment.getType() == RemoveAblePunishment.Type.BLACKLIST ? "This type of punishment may not be appealed." : "Appeal this punishment at " + Neutron.getInstance().getWebsite() + "/appeal")
            ));
            return;
        }

        final List<Profile> alts = profile.findAlts().stream().map(Profile::new).collect(Collectors.toList());

        String altName = null;
        RemoveAblePunishment altPunishment = null;

        for (Profile alt : alts) {

            if (alt.getPunishments().isEmpty() || alt.getActivePunishments().isEmpty() || profile.isSibling(alt)) {
                continue;
            }

            if (alt.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
                altName = alt.getFancyName();
                altPunishment = alt.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);
                break;
            } else if (alt.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
                altName = alt.getFancyName();
                altPunishment = alt.getActivePunishment(RemoveAblePunishment.Type.BAN);
                break;
            }

        }

        if (altPunishment != null && altName != null && !UniverseAPI.getServerName().equalsIgnoreCase("Banned")) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "Your account is " + altPunishment.getPunishType().getExecutedContext() + " due to a punishment related to " + altName
                            + (altPunishment.isPermanent() ? "":"\n\n" + ChatColor.RED + "Expires: " + altPunishment.getRemainingString() + "\n\n" + ChatColor.RED + "Appeal this punishment at " + Neutron.getInstance().getWebsite() + "/appeal")
            );
            return;
        }

        this.cache.put(event.getUniqueId(),profile);
        this.currentIpSession.put(event.getUniqueId(),event.getAddress().getHostAddress());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();

        final Profile profile = this.fromUuid(player.getUniqueId());

        profile.setup(event);

        if (profile.getDisguiseProfile() != null) {
//            player.disguise(profile.getDisguiseProfile().getName(), profile.getDisguiseProfile().getTexture(), profile.getDisguiseProfile().getSignature());
        }

        Neutron.getInstance().getServer().getScheduler().runTaskLater(Neutron.getInstance(), () -> {
            profile.getServerProfile().setOnline(true);
            profile.getServerProfile().setLastLogin(System.currentTimeMillis());
            profile.getServerProfile().setCurrentServer(UniverseAPI.getServerName());
            profile.save();
        }, 12);


        if (Neutron.getInstance().getNetwork().equals(Neutron.Network.CRYPTO)) {
            return;
        }

        if (profile.getActiveRank() != null && profile.getActiveRank().getName().equalsIgnoreCase("Default")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Free Rank");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou have a free &7&lIron Rank &cwaiting for you!"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7Type &f/freerank &7to claim your free Iron Rank&7!"));
            player.sendMessage("");
        }
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if(event.getPlayer().isOp())
                event.allow();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        final Profile profile = this.fromUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        profile.getServerProfile().setOnline(false);
        profile.getServerProfile().setLastLogin(System.currentTimeMillis());
        profile.getServerProfile().setLastServer(UniverseAPI.getServerName());

        profile.save();

        this.cache.remove(player.getUniqueId());

//        if (player.isDisguised()) {
//            player.undisguise();
//        }
    }

    @IncomingPacketHandler
    public void onPunishmentExecute(PunishmentExecutePacket packet) {

        final Document document = packet.document();

        final IPunishment.Type iType = IPunishment.Type.valueOf(document.getString("iType"));

        IPunishment punishment = null;

        if (iType == IPunishment.Type.NORMAL) {
            punishment = new Punishment(document);
        } else if (iType == IPunishment.Type.REMOVE_ABLE) {
            punishment = new RemoveAblePunishment(document);
        }

        punishment.broadcast(packet.punishedFancyName());

        if (packet.broadCastOnly()) {
            return;
        }

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        profile.getPunishments().add(punishment);

        punishment.execute(player);

    }

    @IncomingPacketHandler
    public void onPunishmentPardon(PunishmentPardonPacket packet) {

        final Document document = packet.document();

        final RemoveAblePunishment punishment = new RemoveAblePunishment(document);

        punishment.broadcast(packet.punishedFancyName());

        if (packet.broadCastOnly()) {
            return;
        }

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        final RemoveAblePunishment activePunishment = profile.getActivePunishment(punishment.getType());

        activePunishment.setPardoner(punishment.getPardoner());
        activePunishment.setPardonedAt(punishment.getPardonedAt());
        activePunishment.setPardonedReason(punishment.getPardonedReason());
        activePunishment.setPardonedSilent(punishment.getPardonedSilent());

    }

    @IncomingPacketHandler
    public void onGrantApply(GrantApplyPacket packet) {

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        final Grant grant = new Grant(packet.document());

        profile.getGrants().add(grant);
        profile.recalculateGrants();
    }

    @IncomingPacketHandler
    public void onGrantRemove(GrantRemovePacket packet) {

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        final Grant grant = new Grant(packet.document());

        final Grant toRemove = profile.getGrant(grant.getUuid());

        toRemove.setPardoner(toRemove.getPardoner());
        toRemove.setPardonedAt(toRemove.getPardonedAt());
        toRemove.setPardonedReason(toRemove.getPardonedReason());

    }

    @IncomingPacketHandler
    public void onPermissionAdd(PermissionAddPacket packet) {

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(packet.uuid());

        if (profile.getPermissions().contains(packet.permission())) {
            return;
        }

        profile.getPermissions().add(packet.permission());

        player.recalculatePermissions();
    }

    @IncomingPacketHandler
    public void onPermissionRemove(PermissionRemovePacket packet) {

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(packet.uuid());

        if (!profile.getPermissions().contains(packet.permission())) {
            return;
        }

        profile.getPermissions().remove(packet.permission());

        player.recalculatePermissions();
    }

    @IncomingPacketHandler
    public void onNoteApply(NoteApplyPacket packet) {

        final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

        if (player == null) {
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        final Note note = new Note(packet.document());

        profile.getNotes().add(note);
    }
    @IncomingPacketHandler
    public void onAltLimitChange(AltLimitPacket packet) {
        packet.execute();
    }

    private String generateGeoLocationURL(String address) {
        return "https://www.ip2location.com/demo/" + address;
    }
}
