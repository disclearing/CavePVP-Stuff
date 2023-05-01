package org.cavepvp.profiles;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.piston.Piston;
import cc.fyre.proton.Proton;
import cc.fyre.proton.serialization.*;
import cc.fyre.universe.UniverseAPI;
import com.google.gson.Gson;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.cavepvp.profiles.database.DatabaseHandler;
import org.cavepvp.profiles.leaderboard.LeaderboardHandler;
import org.cavepvp.profiles.listener.NotificationListener;
import org.cavepvp.profiles.listener.SwitchListener;
import org.cavepvp.profiles.packet.ProfilesPacketListener;
import org.cavepvp.profiles.packet.StaffBroadcastPacket;
import org.cavepvp.profiles.packet.type.*;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.PlayerProfileHandler;
import org.cavepvp.profiles.playerProfiles.ReputationHandler;
import org.cavepvp.profiles.playerProfiles.impl.PlayerType;

import java.util.*;

public class Profiles extends JavaPlugin {
    public static final Gson GSON = new com.google.gson.GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @Getter private static Profiles instance;

    @Getter private DatabaseHandler databaseHandler;
    @Getter private ReputationHandler reputationHandler;
    @Getter private LeaderboardHandler leaderboardHandler;
    @Getter private PlayerProfileHandler playerProfileHandler;
    @Getter private Map<UUID,UUID> conversationCache = new HashMap<>();
    @Getter private List<UUID> toggleStaff = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.databaseHandler = new DatabaseHandler(this);
        this.reputationHandler = new ReputationHandler(this);
        this.leaderboardHandler = new LeaderboardHandler(this);
        this.playerProfileHandler = new PlayerProfileHandler(this);

        if (UniverseAPI.getServerName().equalsIgnoreCase("Fasts")) {
            this.getServer().getScheduler().runTaskTimer(this, () -> {
                System.out.println("Dispatching alert cross network");
                Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket("color.white", "&d[&4SimplyTrash&d] Enter our huge $200 PayPal, 5x Custom Rank, 10x Cave Rank, 10x Summer Ranks, 20x Illuminated Chests giveaway at https://twitter.com/CavePvPorg/status/1552739136301318144"));
            },20*3, 20*60);
        }

        this.getServer().getPluginManager().registerEvents(new NotificationListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SwitchListener(), this);

        Proton.getInstance().getPidginHandler().registerListener(new ProfilesPacketListener());

        Proton.getInstance().getPidginHandler().registerPacket(FriendRequestAcceptPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(FriendRequestSendPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(NotificationSendPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(FriendSessionPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(ProfileUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(ProfileUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(StaffBroadcastPacket.class);

        Proton.getInstance().getCommandHandler().registerAll(this);
    }

    public boolean hasStaffChatEnabled(Player player) {
        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        return playerProfile.getPreferences2().isStaffChat() || playerProfile.getPreferences2().isManagerChat() || playerProfile.getPreferences2().isAdminChat();
    }

    public boolean canMessage(Player player, Player target, boolean reply) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        final PlayerProfile playerProfile = this.getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final PlayerProfile targetProfile = this.getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Your profile has failed to load! Contact an administrator!");
            return false;
        }

        final RemoveAblePunishment removeAblePunishment = profile.getActivePunishment(RemoveAblePunishment.Type.MUTE);

        if (removeAblePunishment != null && !reply) {
            player.sendMessage(ChatColor.RED + "You are currently muted.");
            player.sendMessage(ChatColor.RED + "Expires: " + ChatColor.YELLOW + removeAblePunishment.getRemainingString());
            player.sendMessage(ChatColor.RED + "Reason: " + ChatColor.YELLOW + removeAblePunishment.getExecutedReason());
            return false;
        }

        if (player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            return true;
        }

        if (targetProfile.getPreferences2().getMessages().equals(PlayerType.NOBODY)) {
            player.sendMessage(target.getDisplayName() + ChatColor.RED + " has private messages disabled for everyone.");
            return false;
        }

        if (!targetProfile.getFriends().contains(player.getUniqueId()) && targetProfile.getPreferences2().getMessages().equals(PlayerType.FRIENDS_ONLY)) {
            player.sendMessage(target.getDisplayName() + ChatColor.RED + " has restricted messages to friends only.");
            return false;
        }

        return true;
    }
}