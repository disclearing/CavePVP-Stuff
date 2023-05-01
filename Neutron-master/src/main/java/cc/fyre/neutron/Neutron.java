package cc.fyre.neutron;

import cc.fyre.neutron.command.parameter.*;
import cc.fyre.neutron.database.MongoHandler;
import cc.fyre.neutron.listener.AntiVPNListener;
import cc.fyre.neutron.listener.ChatListener;
import cc.fyre.neutron.listener.VoteListener;
import cc.fyre.neutron.packet.BroadcastPacket;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.prefix.PrefixHandler;
import cc.fyre.neutron.prevention.PreventionHandler;
import cc.fyre.neutron.prevention.PreventionListener;
import cc.fyre.neutron.profile.ProfileHandler;
import cc.fyre.neutron.profile.attributes.rollback.RollbackType;
import cc.fyre.neutron.profile.namemc.task.NameMCReminderTask;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.RankHandler;
import cc.fyre.neutron.security.SecurityHandler;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.neutron.vault.NeutronChatProvider;
import cc.fyre.neutron.vault.NeutronPermissionProvider;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.Packet;
import cc.fyre.proton.serialization.*;
import cc.fyre.universe.UniverseAPI;
import com.google.gson.Gson;
import com.google.gson.LongSerializationPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Neutron extends JavaPlugin {

    @Getter
    private static Neutron instance;

    @Getter
    private Network network;

    @Getter
    private MongoHandler mongoHandler;
    @Getter
    private RankHandler rankHandler;
    @Getter
    private PrefixHandler prefixHandler;
    @Getter private ProfileHandler profileHandler;
    @Getter private SecurityHandler securityHandler;
    @Getter
    private PreventionHandler preventionHandler;

    public static Map<UUID, Integer> cooldowns = new HashMap<>();

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
    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.network = Network.valueOf(this.getConfig().getString("network").toUpperCase());

        getServer().getPluginManager().registerEvents(new AntiVPNListener(), this);

        final Plugin plugin = this.getServer().getPluginManager().getPlugin("Votifier");

        this.mongoHandler = new MongoHandler(this);

        this.rankHandler = new RankHandler(this);
        this.prefixHandler = new PrefixHandler(this);
        this.profileHandler = new ProfileHandler(this);
        this.preventionHandler = new PreventionHandler(this);
        this.securityHandler = new SecurityHandler(this);

        Proton.getInstance().getCommandHandler().registerParameterType(Rank.class, new RankParameter());
        Proton.getInstance().getCommandHandler().registerParameterType(Prefix.class, new PrefixParameter());
        Proton.getInstance().getCommandHandler().registerParameterType(ChatColor.class, new ChatColorParameter());
        Proton.getInstance().getCommandHandler().registerParameterType(RollbackType.class, new RollbackParameter());
        Proton.getInstance().getCommandHandler().registerParameterType(DurationWrapper.class, new DurationWrapperParameter());

        Proton.getInstance().getCommandHandler().registerAll(this);

        new NameMCReminderTask().runTaskTimerAsynchronously(this, 20 * 60 * 5, 20 * 60 * 5);

        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null && vault.isEnabled()) {
            ServicesManager servicesManager = getServer().getServicesManager();
            Permission permission = new NeutronPermissionProvider();
            servicesManager.register(Permission.class, permission, this, ServicePriority.High);
            servicesManager.register(Chat.class, new NeutronChatProvider(permission), this, ServicePriority.High);
        }
    }

    public void sendPacketAsync(Packet packet) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> Proton.getInstance().getPidginHandler().sendPacket(packet));
    }

    @Override
    public void onDisable() {
        getPreventionHandler().save();
    }

    public void sendMessageToNetwork(String text) {
        this.sendMessageToNetwork(new FancyMessage(text));
    }

    public void sendMessageToNetwork(FancyMessage fancyMessage) {
        Proton.getInstance().getPidginHandler().sendPacket(new BroadcastPacket(fancyMessage));
    }

    public String getTeamspeak() {
        return this.network.getTeamSpeakLink();
    }
    public String getWebsite() {
        return this.network.getDomain();
    }
    public String getDiscord() {
        return this.network.getDiscordLink();
    }
    public String getServerName() {
        return this.network.getNetworkName();
    }
    public String getStore() {
        return this.network.getStoreLink();
    }

    @AllArgsConstructor
    public enum Network {
        CAVEPVP(
                "CavePvP", "CavePvP", ChatColor.DARK_RED, ChatColor.WHITE, ChatColor.RED,
                "cavepvp.org", "store.cavepvp.org", "discord.gg/cavepvp", "ts.cavepvp.org"
        ),
        CRYPTO(
                "CryptoMC", "CryptoMC", ChatColor.GOLD, ChatColor.WHITE, ChatColor.YELLOW,
                "cryptomc.org", "store.cryptomc.org", "cryptomc.org/discord", "cryptomc.org/discord"
        );

        @Getter
        private String name;
        @Getter
        private String networkName;
        @Getter
        private ChatColor mainColor;
        @Getter
        private ChatColor secondColor;
        @Getter
        private ChatColor alternativeColor;

        @Getter
        private String domain;
        @Getter
        private String storeLink;
        @Getter
        private String discordLink;
        @Getter
        private String teamSpeakLink;
    }

}
