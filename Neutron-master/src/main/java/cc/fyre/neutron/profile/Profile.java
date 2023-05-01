package cc.fyre.neutron.profile;

import cc.fyre.neutron.command.profile.holiday.type.HolidayType;
import cc.fyre.neutron.profile.attributes.Language;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.comparator.GrantDateComparator;
import cc.fyre.neutron.profile.attributes.grant.comparator.GrantWeightComparator;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.comparator.PunishmentDateComparator;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.server.ServerProfile;
import cc.fyre.neutron.profile.disguise.DisguiseProfile;
import cc.fyre.neutron.Neutron;

import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.profile.attributes.ProfilePermissible;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.util.qr.TotpUtil;
import cc.fyre.universe.UniverseAPI;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import cc.fyre.proton.uuid.UUIDCache;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;

//TODO: kinda wanna clean this up
public class Profile {

    @Getter private UUID uuid;
    @Getter @Setter private String name;

    @Getter @Setter private String ipAddress;

    @Getter @Setter private HolidayType holidayType;
    @Getter @Setter private Language language;

    @Getter @Setter private ServerProfile serverProfile;
    @Getter @Setter private DisguiseProfile disguiseProfile;

    @Getter @Setter private ChatColor chatColor;
    @Getter @Setter private String customPrefix;

    @Getter @Setter private List<UUID> siblings;

    @Setter private Grant activeGrant;
    @Getter @Setter private Prefix activePrefix;

    @Getter @Setter private List<Note> notes;
    @Getter @Setter private List<Grant> grants;
    @Getter @Setter private List<String> permissions;
    @Getter @Setter private List<IPunishment> punishments;

    @Getter @Setter private String authSecret;

    @Getter @Setter private int votes;

    /* Non-storage */
    @Getter @Setter private ProfilePermissible permissible;

    public Profile(UUID uuid,String name) {
        this.uuid = uuid;
        this.name = name;

        this.ipAddress = null;

        this.serverProfile = new ServerProfile(false,System.currentTimeMillis(),0L,
                Neutron.getInstance().getConfig().getString("server.name"), "");
        this.disguiseProfile = null;

        this.chatColor = ChatColor.WHITE;

        this.siblings = new ArrayList<>();

        this.activeGrant = new Grant(Neutron.getInstance().getRankHandler().getDefaultRank(),UUIDCache.CONSOLE_UUID,(long)Integer.MAX_VALUE,"Default Grant");
        this.activePrefix = null;
        this.customPrefix = null;

        this.notes = new ArrayList<>();
        this.grants = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.punishments = new ArrayList<>();

        this.authSecret = null;

        this.load();
    }

    public Profile(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.load(document);
    }

    public void load() {
        this.getHandler().load(this);
    }

    public void load(Document document) {
        this.getHandler().load(this,document);
    }

    public void save() {
        this.getHandler().save(this);
    }

    public List<Document> findAlts() {
        return this.getHandler().findAlts(this);
    }

    public List<Document> findAltsAsync() {
        return this.getHandler().findAltsAsync(this).join();
    }

    public boolean hasSubscription() {
        return this.grants.stream().anyMatch(it -> it.getRank().getName().equalsIgnoreCase("VIP") && it.isActive());
    }

    public Document findDocument() {
        return this.getHandler().getCollection().find(Filters.eq("uuid",this.uuid.toString())).first();
    }

    public ProfileHandler getHandler() {
        return Neutron.getInstance().getProfileHandler();
    }

    public Note getNote(UUID uuid) {
        return this.notes.stream().filter(note -> note.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public Grant getGrant(UUID uuid) {
        return this.grants.stream().filter(grant -> grant.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(Grant::isActive).collect(Collectors.toList());
    }

    public Grant getActiveGrant(Rank rank) {
        return this.getActiveGrants().stream().filter(grant -> grant.getRank().getUuid() == rank.getUuid()).sorted(new GrantWeightComparator().thenComparing(new GrantDateComparator())).findFirst().orElse(null);
    }

    public IPunishment getPunishment(UUID uuid) {
        return this.punishments.stream().filter(iPunishment -> iPunishment.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public List<RemoveAblePunishment> getActivePunishments() {
        return this.punishments.stream().filter(iPunishment -> iPunishment instanceof RemoveAblePunishment).map(RemoveAblePunishment.class::cast).filter(RemoveAblePunishment::isActive).collect(Collectors.toList());
    }

    public RemoveAblePunishment getActivePunishment(RemoveAblePunishment.Type type) {
        return this.getActivePunishments().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == type).sorted(new PunishmentDateComparator()).findFirst().orElse(null);
    }

    public String getFancyName() {

        String tag = "";

        if (this.getActivePrefix() != null) {
            String display = this.getActivePrefix().getDisplay();
            String noColor = ChatColor.stripColor(this.getActivePrefix().getDisplay());

            if (display.endsWith(ChatColor.DARK_GRAY + "]") && display.startsWith(ChatColor.DARK_GRAY + "[") && noColor.length() == 3) {
                tag = display.replace(ChatColor.DARK_GRAY + "[", "").replace(ChatColor.DARK_GRAY + "]", "");
            } else if (noColor.length() == 1) {
                tag = display;
            }
        }

        if (tag.equalsIgnoreCase("") && this.hasSubscription()) {
            tag = ChatColor.GOLD + "✪";
        }

        if (customPrefix != null) {
            return tag + ChatColor.getLastColors(customPrefix) + this.name;
        }

        if (this.holidayType != null) {
            return tag + this.holidayType.getDisplayColor().toString() + this.name;
        }

        String name = this.name;

//        if (this.name.equalsIgnoreCase("BlondeLoverJames")) {
//            name = "LilManJames";
//        }

        return tag + this.getActiveRank().getColor().toString() + (this.getActiveRank().getSecondColor() != null ? this.getActiveRank().getSecondColor():"") + name;
    }

    public Player getPlayer() {
        return Neutron.getInstance().getServer().getPlayer(this.uuid);
    }

    public boolean isSibling(Profile profile) {
        return this.siblings.contains(profile.getUuid()) || profile.getSiblings().contains(this.uuid);
    }

    public List<String> getEffectivePermissions() {

        final List<String> toReturn = new ArrayList<>(this.permissions);

        this.getActiveGrants().stream().map(Grant::getRank).map(Rank::getEffectivePermissions).forEach(toReturn::addAll);

        if (this.getHolidayType() != null) {
            toReturn.add("color." + this.getHolidayType().getChatColor().name().toLowerCase());
        }

        return toReturn;
    }

    public void recalculateGrants() {
        final List<Grant> grants = this.getActiveGrants().stream().sorted(new GrantWeightComparator().reversed().thenComparing(new GrantDateComparator().reversed())).collect(Collectors.toList());

        if (grants.size() == 0) {
            grants.add(new Grant(Neutron.getInstance().getRankHandler().getDefaultRank(), UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Default Grant"));
        }

        final Grant grant = grants.get(0);

        this.setActiveGrant(grant);
        this.refreshDisplayName(this.getPlayer());
    }

    public void setup(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.permissible = new ProfilePermissible(player);

        this.permissible.recalculatePermissions();

        this.refreshDisplayName(player);
    }

    public Rank getActiveRank() {
        return this.getActiveGrant().getRank();
    }

    public Grant getActiveGrant() {
        return this.disguiseProfile == null ? this.activeGrant:new Grant(this.disguiseProfile.getRank(),UUIDCache.CONSOLE_UUID,(long)Integer.MAX_VALUE,"Disguised");
    }

    public void refreshDisplayName() {
        this.refreshDisplayName(this.getPlayer());
    }

    public void refreshDisplayName(Player player) {

        if (player == null) {
            return;
        }

        String displayName = this.getFancyName();

        if (Neutron.getInstance().getConfig().getBoolean("fancyName.displayName")) {
            player.setDisplayName(displayName);
        }

        if (Neutron.getInstance().getConfig().getBoolean("fancyName.tabListName")) {

            if (player.getName().length() <= 14) {
                player.setPlayerListName(displayName);
            }

        }

    }

    public boolean verifyCode(int code) {

        if (this.authSecret == null) {
            return false;
        }

        try {
            return TotpUtil.validateCurrentNumber(this.authSecret,code,250);
        } catch (GeneralSecurityException ex) {
            return false;
        }

    }

}
