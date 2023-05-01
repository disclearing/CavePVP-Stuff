package net.frozenorb.foxtrot.map;

import cc.fyre.proton.Proton;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelHandler;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.KillstreakHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.KitManager;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.KitUpgradesHandler;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsHandler;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.nametag.FoxtrotNametagProvider;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreboardConfiguration;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.deathban.Deathban;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import com.google.gson.JsonParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MapHandler {

    private transient File mapInfo;
    @Getter
    private boolean kitMap;
    @Getter
    private boolean halloween;
    @Getter
    private int allyLimit;
    @Getter @Setter
    private int teamSize;
    @Getter
    private long regenTimeDeath;
    @Getter
    private long regenTimeRaidable;
    @Getter
    private String scoreboardTitle;
    @Getter
    private String mapStartedString;
    @Getter
    private double baseLootingMultiplier;
    @Getter
    private double level1LootingMultiplier;
    @Getter
    private double level2LootingMultiplier;
    @Getter
    private double level3LootingMultiplier;

    @Getter
    private boolean craftingGopple;
    @Getter
    private boolean craftingReducedMelon;
    @Getter
    private int goppleCooldown;
    @Getter
    private int minForceInviteMembers = 10;
    @Getter
    private String endPortalLocation;
    @Getter
    private boolean fastSmeltEnabled;
    @Getter
    private AbilityHandler abilityHandler;
    @Getter
    @Setter
    private int netherBuffer;
    @Getter
    private GameHandler gameHandler;
    @Getter
    private DuelHandler duelHandler;
    @Getter
    @Setter
    private int worldBuffer;
    @Getter
    private float dtrIncrementMultiplier;
    @Getter
    private StatsHandler statsHandler;

    // Kit-Map only stuff:
    @Getter
    private KillstreakHandler killstreakHandler;
    @Getter
    private KitManager kitManager;
    @Getter
    private KitUpgradesHandler kitUpgradesHandler;

    public MapHandler() {
    }

    public void load() {
        reloadConfig();

        Proton.getInstance().getNameTagHandler().registerProvider(new FoxtrotNametagProvider());
        Proton.getInstance().getScoreboardHandler().setConfiguration(FoxtrotScoreboardConfiguration.create());

        if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            this.abilityHandler = new AbilityHandler(Foxtrot.getInstance());
        }

        Iterator<Recipe> recipeIterator = Foxtrot.getInstance().getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            // Disallow the crafting of gopples.
            if (!craftingGopple && recipe.getResult().getDurability() == (short) 1 && recipe.getResult().getType() == org.bukkit.Material.GOLDEN_APPLE) {
                recipeIterator.remove();
            }

            // Remove vanilla glistering melon recipe
            if (craftingReducedMelon && recipe.getResult().getType() == Material.SPECKLED_MELON) {
                recipeIterator.remove();
            }

            if (Foxtrot.getInstance().getConfig().getBoolean("rodPrevention") && recipe.getResult().getType() == Material.FISHING_ROD) {
                recipeIterator.remove();
            }

//            if (recipe.getResult().getType() == Material.EXPLOSIVE_MINECART) {
//                recipeIterator.remove();
//            }
        }

        // add our glistering melon recipe
        if (craftingReducedMelon) {
            Foxtrot.getInstance().getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON)).addIngredient(Material.MELON).addIngredient(Material.GOLD_NUGGET));
        }

        ShapedRecipe nametagRecipe = new ShapedRecipe(new ItemStack(Material.NAME_TAG));
        ShapedRecipe saddleRecipe = new ShapedRecipe(new ItemStack(Material.SADDLE));
        ShapedRecipe horseArmorRecipe = new ShapedRecipe(new ItemStack(Material.DIAMOND_BARDING));

        nametagRecipe.shape(" I ",
                " P ",
                " S ");
        nametagRecipe.setIngredient('I', Material.INK_SACK);
        nametagRecipe.setIngredient('P', Material.PAPER);
        nametagRecipe.setIngredient('S', Material.STRING);

        saddleRecipe.shape("  L",
                "LLL",
                "B B");
        saddleRecipe.setIngredient('L', Material.LEATHER);
        saddleRecipe.setIngredient('B', Material.LEASH);

        horseArmorRecipe.shape(" SD",
                "BBL",
                "LL ");
        horseArmorRecipe.setIngredient('S', Material.SADDLE);
        horseArmorRecipe.setIngredient('D', Material.DIAMOND);
        horseArmorRecipe.setIngredient('B', Material.DIAMOND_BLOCK);
        horseArmorRecipe.setIngredient('L', Material.LEATHER);

        Foxtrot.getInstance().getServer().addRecipe(nametagRecipe);
        Foxtrot.getInstance().getServer().addRecipe(saddleRecipe);
        Foxtrot.getInstance().getServer().addRecipe(horseArmorRecipe);

        statsHandler = new StatsHandler();

        if (isKitMap()) {
            killstreakHandler = new KillstreakHandler();
            kitManager = new KitManager();
            gameHandler = new GameHandler();
            duelHandler = new DuelHandler();
            kitUpgradesHandler = new KitUpgradesHandler();

            Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
                net.frozenorb.foxtrot.gameplay.events.EventHandler eventHandler = Foxtrot.getInstance().getEventHandler();
                List<Event> localEvents = new ArrayList<>(eventHandler.getEvents());

                if (localEvents.isEmpty()) {
                    return;
                }

                if (Foxtrot.getInstance().getServer().getOnlinePlayers().size() < 10) {
                    System.out.println();
                    System.out.println("[HCTeams] Couldn't start a valid event as there was less than 10 players online!");
                    System.out.println();
                    return;
                }

                List<KOTH> koths = new ArrayList<>();

                for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
                    if (otherKoth.isActive()) {
                        return;
                    }

                    if (otherKoth.getName().contains("Citadel") || otherKoth.getName().contains("Outpost")) {
                        continue;
                    }

                    if (otherKoth.getType() == EventType.KOTH) {
                        koths.add((KOTH) otherKoth);
                    }
                }

                KOTH selected = koths.get(ThreadLocalRandom.current().nextInt(koths.size()));
                selected.activate();
            }, 15 * 60 * 20, 15 * 60 * 20);

            TeamActionTracker.setDatabaseLogEnabled(false);
        }
    }

    public void reloadConfig() {
        try {
            mapInfo = new File(Foxtrot.getInstance().getDataFolder(), "mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbObject = getDefaults();

                FileUtils.write(mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            } else {
                // basically check for any new keys in the defaults which aren't contained in the actual file
                // if there are any, add them to the file.
                BasicDBObject file = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

                BasicDBObject defaults = getDefaults();

                defaults.keySet().stream().filter(key -> !file.containsKey(key)).forEach(key -> file.put(key, defaults.get(key)));

                FileUtils.write(mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(file.toString())));
            }

            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                this.kitMap = dbObject.getBoolean("kitMap", false);
                this.halloween = dbObject.getBoolean("halloween", false);
                this.allyLimit = dbObject.getInt("allyLimit", 0);
                this.teamSize = dbObject.getInt("teamSize", 30);
                this.regenTimeDeath = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeDeath", 60));
                this.regenTimeRaidable = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeRaidable", 60));
                this.scoreboardTitle = ChatColor.translateAlternateColorCodes('&', dbObject.getString("scoreboardTitle"));
                this.mapStartedString = dbObject.getString("mapStartedString");
                ServerHandler.WARZONE_RADIUS = dbObject.getInt("warzone", 1000);
                BorderListener.BORDER_SIZE = dbObject.getInt("border", 3000);
                this.goppleCooldown = dbObject.getInt("goppleCooldown");
                this.netherBuffer = dbObject.getInt("netherBuffer");
                this.worldBuffer = dbObject.getInt("worldBuffer");
                this.endPortalLocation = dbObject.getString("endPortalLocation");
                this.fastSmeltEnabled = dbObject.getBoolean("fastSmeltEnabled", true);

                BasicDBObject looting = (BasicDBObject) dbObject.get("looting");

                this.baseLootingMultiplier = looting.getDouble("base");
                this.level1LootingMultiplier = looting.getDouble("level1");
                this.level2LootingMultiplier = looting.getDouble("level2");
                this.level3LootingMultiplier = looting.getDouble("level3");

                BasicDBObject crafting = (BasicDBObject) dbObject.get("crafting");

                this.craftingGopple = crafting.getBoolean("gopple");
                this.craftingReducedMelon = crafting.getBoolean("reducedMelon");

                if (dbObject.containsKey("deathban")) {
                    BasicDBObject deathban = (BasicDBObject) dbObject.get("deathban");

                    Deathban.load(deathban);
                }

                if (dbObject.containsKey("minForceInviteMembers")) {
                    minForceInviteMembers = dbObject.getInt("minForceInviteMembers");
                }

                this.dtrIncrementMultiplier = (float) dbObject.getDouble("dtrIncrementMultiplier", 4.5F);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BasicDBObject getDefaults() {
        BasicDBObject dbObject = new BasicDBObject();

        BasicDBObject looting = new BasicDBObject();
        BasicDBObject crafting = new BasicDBObject();
        BasicDBObject deathban = new BasicDBObject();

        dbObject.put("kitMap", false);
        dbObject.put("allyLimit", 0);
        dbObject.put("teamSize", 30);
        dbObject.put("regenTimeDeath", 60);
        dbObject.put("regenTimeRaidable", 60);
        dbObject.put("scoreboardTitle", "&6&lCavePvP &c[Map 1]");
        dbObject.put("mapStartedString", "Map 3 - Started January 31, 2015");
        dbObject.put("warzone", 1000);
        dbObject.put("netherBuffer", 150);
        dbObject.put("worldBuffer", 300);
        dbObject.put("endPortalLocation", "2500, 2500");
        dbObject.put("border", 3000);
        dbObject.put("goppleCooldown", TimeUnit.HOURS.toMinutes(4));
        dbObject.put("fastSmeltEnabled", true);

        looting.put("base", 1D);
        looting.put("level1", 1.2D);
        looting.put("level2", 1.4D);
        looting.put("level3", 2D);

        dbObject.put("looting", looting);

        crafting.put("gopple", true);
        crafting.put("reducedMelon", true);

        dbObject.put("crafting", crafting);

        deathban.put("HIGHROLLER", 45);
        deathban.put("EPIC", 45);
        deathban.put("PRO", 90);
        deathban.put("VIP", 120);
        deathban.put("DEFAULT", 240);

        dbObject.put("deathban", deathban);

        dbObject.put("minForceInviteMembers", 10);

        dbObject.put("dtrIncrementMultiplier", 4.5F);
        return dbObject;
    }

    public void saveBorder() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("border", BorderListener.BORDER_SIZE); // update the border

                FileUtils.write(mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the border that was changed.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveNetherBuffer() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("netherBuffer", Foxtrot.getInstance().getMapHandler().getNetherBuffer()); // update the nether buffer

                FileUtils.write(mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveWorldBuffer() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("worldBuffer", Foxtrot.getInstance().getMapHandler().getWorldBuffer()); // update the world buffer

                FileUtils.write(mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}