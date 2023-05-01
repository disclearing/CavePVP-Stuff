package cc.fyre.hub;

import cc.fyre.hub.adapter.ServerAdapter;
import cc.fyre.hub.command.UpdateCommand;
import cc.fyre.hub.listener.DoubleJumpListener;
import cc.fyre.hub.listener.EnderButtListener;
import cc.fyre.hub.listener.HidePlayersListener;
import cc.fyre.hub.listener.HubListener;
import cc.fyre.hub.scoreboard.HubScoreGetter;
import cc.fyre.hub.tab.HubTabLayout;
import cc.fyre.hub.util.HubItem;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.scoreboard.config.ScoreboardConfiguration;
import cc.fyre.proton.scoreboard.construct.TitleGetter;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import de.minetick.Migot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;
import org.cavepvp.entity.EntityHandler;
import org.cavepvp.entity.type.hologram.line.HologramLine;
import org.cavepvp.entity.type.npc.NPC;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Hub extends JavaPlugin {

    @Getter
    private static Hub instance;

    @Getter
    private Integer border;
    @Getter
    private Integer yTeleport;
    @Getter
    private Location spawnPoint;
    @Getter
    private int playersOnline;
    @Getter @Setter
    private int secretPlayers;

    @Getter
    private final List<String> joinMessage = new ArrayList<>();
    @Getter
    private final List<String> scoreboardScores = new ArrayList<>();

    @Getter
    private final Map<String, Menu> menus = new HashMap<>();
    @Getter
    private final Map<Integer, HubItem> items = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.border = this.getConfig().getInt("border");
        this.yTeleport = this.getConfig().getInt("y-teleport");
        this.spawnPoint = this.getServer().getWorld("world").getSpawnLocation().add(0.5, 0, 0.5);
        this.spawnPoint.setYaw(-90);
        this.spawnPoint.setPitch(0);

        for (String message : this.getConfig().getStringList("join-message")) {
            this.joinMessage.add(ChatColor.translate(message));
        }

        for (String score : Hub.getInstance().getConfig().getStringList("scoreboard.scores")) {
            this.scoreboardScores.add(ChatColor.translate(score));
        }

        this.getServer().getPluginManager().registerEvents(new HubListener(), this);
        this.getServer().getPluginManager().registerEvents(new HidePlayersListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EnderButtListener(), this);
        this.getServer().getPluginManager().registerEvents(new DoubleJumpListener(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        EntityHandler.INSTANCE.getAdapters().add(new ServerAdapter());

        final ScoreboardConfiguration scoreboardConfiguration = new ScoreboardConfiguration();

        scoreboardConfiguration.setTitleGetter(new TitleGetter(ChatColor.translate(this.getConfig().getString("scoreboard.title").replace("{hub}", Universe.getInstance().getServerName()))));
        scoreboardConfiguration.setScoreGetter(new HubScoreGetter());

        if (this.getConfig().getBoolean("secret", false)) {
//            Proton.getInstance().getTabHandler().setLayoutProvider(new SecretTabLayout());
        } else {
            Proton.getInstance().getTabHandler().setLayoutProvider(new HubTabLayout());
        }
        Proton.getInstance().getScoreboardHandler().setConfiguration(scoreboardConfiguration);

        Proton.getInstance().getCommandHandler().registerClass(UpdateCommand.class);

        this.loadItems();
        this.loadMenus();

        this.getServer().getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            final AtomicInteger toReturn = new AtomicInteger(0);

            for (Server server : Universe.getInstance().getUniverseHandler().getServers()) {
                toReturn.addAndGet(server.getOnlinePlayers().get());
            }

            playersOnline = toReturn.get();
        }, 20 * 5, 20);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {

            for (NPC npc : EntityHandler.INSTANCE.getAllEntities().stream().filter(it -> it instanceof NPC).map(it -> (NPC)it).collect(Collectors.toList())) {

                if (!npc.getName().equalsIgnoreCase("Fasts") && !npc.getName().equalsIgnoreCase("Kits") && !npc.getName().equalsIgnoreCase("Practice") && !npc.getName().equalsIgnoreCase("Bunkers")) {
                    continue;
                }

                for (HologramLine line : npc.getHologram().getLines()) {
                    for (Player onlinePlayer : this.getServer().getOnlinePlayers()) {
                        line.render(onlinePlayer);
                    }
                }
            }

        }, 20 * 5, 20 * 5);
    }

    private void loadItems() {

        for (String integerString : this.getConfig().getConfigurationSection("items").getKeys(false)) {

            try {

                final Integer integer = Integer.valueOf(integerString);

                final String direction = "items." + integer + ".";

                final List<String> lore = new ArrayList<>();

                if (this.getConfig().contains(direction + "lore")) {

                    for (String loreLine : this.getConfig().getStringList(direction + "lore")) {
                        lore.add(ChatColor.translate(loreLine));
                    }

                }

                this.items.put(integer, new HubItem((this.getConfig().contains(direction + "menu") ? this.getConfig().getString(direction + "menu") : null), ItemBuilder.of(Material.valueOf(this.getConfig().getString(direction + "material")))
                        .name(this.getConfig().getString(direction + "name"))
                        .amount(!this.getConfig().contains(direction + "amount") ? 1 : this.getConfig().getInt(direction + "amount"))
                        .data(!this.getConfig().contains(direction + "durability") ? 0 : (short) this.getConfig().getInt(direction + "durability"))
                        .setLore(lore)
                        .build())
                );

            } catch (NumberFormatException ex) {
                this.getLogger().warning("Ignoring item " + integerString + " not a valid integer.");
            }

        }

    }

    private void loadMenus() {

        for (String name : this.getConfig().getConfigurationSection("menus").getKeys(false)) {

            this.menus.put(name, new Menu() {

                @Override
                public int size(Player player) {
                    return getConfig().getInt("menus." + name + ".rows") * 9;
                }

                @Override
                public boolean isPlaceholder() {
                    return getConfig().getBoolean("menus." + name + ".placeholder");
                }

                @Override
                public boolean isAutoUpdate() {
                    return getConfig().getBoolean("menus." + name + ".auto-update");
                }

                @Override
                public boolean isUpdateAfterClick() {
                    return getConfig().getBoolean("menus." + name + ".update-after-click");
                }

                @Override
                public String getTitle(Player player) {
                    return ChatColor.translate(getConfig().getString("menus." + name + ".title"));
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {

                    final Map<Integer, Button> toReturn = new HashMap<>();

                    for (String integerString : getConfig().getConfigurationSection("menus." + name + ".buttons").getKeys(false)) {
                        try {
                            final Integer integer = Integer.valueOf(integerString);
                            final String direction = "menus." + name + ".buttons." + integer + ".";
                            if (getConfig().contains(direction + "type") && getConfig().getString(direction + "type").equalsIgnoreCase("Multi")) {
                                String menu = getConfig().getString(direction + "opengui");
                                toReturn.put(integer, new Button() {

                                    @Override
                                    public String getName(Player player) {
                                        return ChatColor.translate(getConfig().getString(direction + "name"));
                                    }

                                    @Override
                                    public List<String> getDescription(Player player) {
                                        final List<String> toReturn = new ArrayList<>();

                                        for (String lore : getConfig().getStringList(direction + "lore")) {
                                            toReturn.add(ChatColor.translate(lore
                                                            .replace("{players}", "" + 0)
                                                            .replace("{meetupPlayers}", "" + 0)
                                                    )
                                            );
                                        }

                                        return toReturn;
                                    }

                                    @Override
                                    public Material getMaterial(Player player) {
                                        if (direction.contains("14")) {
                                            return Material.BEACON;
                                        }

                                        if (getConfig().contains(direction + "id") && getConfig().getInt(direction + "id") != 0) {
                                            return Material.getMaterial(getConfig().getInt(direction + "id"));
                                        } else
                                            return Material.valueOf(getConfig().getString(direction + "material"));
                                    }

                                    @Override
                                    public int getAmount(Player player) {
                                        return getConfig().getInt(direction + "amount");
                                    }

                                    @Override
                                    public byte getDamageValue(Player player) {
                                        return (byte) getConfig().getInt(direction + "durability");
                                    }

                                    @Override
                                    public void clicked(Player player, int slot, ClickType clickType) {
                                        Menu menu1 = getMenus().get(menu);
                                        if (menu1 != null) {
                                            menu1.openMenu(player);
                                        }
                                    }
                                });
                            } else {
                                final Server server = !getConfig().contains(direction + "server") ? null : Universe.getInstance().getUniverseHandler().serverFromName(getConfig().getString(direction + "server"));

                                toReturn.put(integer, new Button() {

                                    @Override
                                    public String getName(Player player) {
                                        return ChatColor.translate(getConfig().getString(direction + "name." + (server == null ? "offline" : server.getStatus().name().toLowerCase())));
                                    }

                                    @Override
                                    public Material getMaterial(Player player) {
                                        if (getConfig().contains(direction + "id") && getConfig().getInt(direction + "id") != 0) {
                                            return Material.getMaterial(getConfig().getString(direction + "material." + (server == null ? "offline.id" : server.getStatus().name().toLowerCase())));
                                        } else {
                                            return Material.valueOf(getConfig().getString(direction + "material." + (server == null ? "offline" : server.getStatus().name().toLowerCase())));
                                        }
                                    }

                                    @Override
                                    public int getAmount(Player player) {

                                        return getConfig().getInt(direction + "amount." + (server == null ? "offline" : server.getStatus().name().toLowerCase()));
                                    }

                                    @Override
                                    public byte getDamageValue(Player player) {
                                        return (byte) getConfig().getInt(direction + "durability." + (server == null ? "offline" : server.getStatus().name().toLowerCase()));
                                    }

                                    @Override
                                    public List<String> getDescription(Player player) {

                                        final List<String> toReturn = new ArrayList<>();

                                        for (String lore : getConfig().getStringList(direction + "lore." + (server == null ? "offline" : server.getStatus().name().toLowerCase()))) {
                                            toReturn.add(ChatColor.translate(lore
                                                    .replace("{players}", "" + (server == null ? 0 : server.getName().contains("Bunkers") ? 0 : server.getOnlinePlayers().get()))
                                                    .replace("{max-players}", "" + (server == null ? 0 : server.getMaximumPlayers().get()))
                                            ));
                                        }

                                        return toReturn;
                                    }

                                    @Override
                                    public void clicked(Player player, int slot, ClickType clickType) {
                                        if (server == null) {
                                            player.sendMessage(ChatColor.RED + "Server is offline!");
                                            return;
                                        }

                                        player.chat("/joinqueue " + server.getName());
                                    }
                                });
                            }


                        } catch (NumberFormatException ex) {
                            getLogger().warning("Ignoring item " + integerString + "in menu " + name + " not a valid integer.");
                        }

                    }

                    return toReturn;
                }

            });
        }
    }

}
