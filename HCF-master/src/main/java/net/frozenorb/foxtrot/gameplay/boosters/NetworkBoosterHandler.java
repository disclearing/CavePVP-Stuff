package net.frozenorb.foxtrot.gameplay.boosters;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.parameter.BoosterParameter;
import net.frozenorb.foxtrot.gameplay.boosters.service.BoosterService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NetworkBoosterHandler implements Listener {
    private Foxtrot instance;

    @Getter private List<Booster> boostersTypes = new ArrayList<>();

    @Getter @Setter private Map<UUID, List<Booster>> boostersQueued = new HashMap<>();
    @Getter @Setter private Map<UUID, Map<Booster, Integer>> boosterBalances = new HashMap<>();

    @Getter private File file;
    @Getter private FileConfiguration data;

    public NetworkBoosterHandler(Foxtrot instance) {
        this.instance = instance;

        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),"net.frozenorb.foxtrot.gameplay.boosters.type")) {

            if (!Booster.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                this.boostersTypes.add((Booster)clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        Proton.getInstance().getCommandHandler().registerParameterType(Booster.class, new BoosterParameter());

        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);

        new BoosterService(this.instance, this).runTaskTimer(this.instance, 20, 20);

        this.loadBoosters();
    }

    public void loadBoosters() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/boosters.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("activeBoosters") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
                for (String boosterName : this.data.getConfigurationSection("activeBoosters").getKeys(false)) {
                    final Booster booster = this.findBooster(boosterName);

                    final UUID activatedBy = UUID.fromString(this.data.getString("activeBoosters." + boosterName + ".activatedBy"));
                    final long activatedAt = this.data.getLong("activeBoosters." + boosterName + ".activatedAt");

                    booster.activate(activatedBy);
                    booster.setActivatedAt(activatedAt);
                }
            }, 5);
        }

        if (this.data.get("boostersQueued") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
                for (String playerID : this.data.getConfigurationSection("boostersQueued").getKeys(false)) {
                    final UUID uuid = UUID.fromString(playerID);

                    final List<Booster> boosters = new ArrayList<>();

                    for (String boosterName : this.data.getConfigurationSection("boostersQueued." + playerID).getKeys(false)) {
                        final Booster booster = this.findBooster(boosterName);

                        if (booster == null) {
                            System.out.println("***WARNING*** " + uuid + " has an invalid booster. What is " + boosterName + "?");
                            continue;
                        }

                        boosters.add(booster);
                    }

                    boostersQueued.put(uuid, boosters);
                }
            }, 5);
        }

        if (this.data.get("boosterBalance") == null) {
            return;
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            for (String playerID : this.data.getConfigurationSection("boosterBalance").getKeys(false)) {
                final UUID uuid = UUID.fromString(playerID);

                final Map<Booster, Integer> boosterMap = new HashMap<>();

                for (String boosterName : this.data.getConfigurationSection("boosterBalance." + playerID).getKeys(false)) {
                    final Booster booster = this.findBooster(boosterName);

                    if (booster == null) {
                        System.out.println("***WARNING*** " + uuid + " has an invalid booster. What is " + boosterName + "?");
                        continue;
                    }

                    boosterMap.put(booster, this.data.getInt("boosterBalance." + playerID + "." + boosterName));
                }

                boosterBalances.put(uuid, boosterMap);
            }
        }, 5);
    }

    public void saveBoosters() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Map.Entry<UUID, Map<Booster, Integer>> mainEntry : this.boosterBalances.entrySet()) {
            for (Map.Entry<Booster, Integer> entry : mainEntry.getValue().entrySet()) {
                this.data.set("boosterBalance." + mainEntry.getKey().toString() + "." + entry.getKey().getId(), entry.getValue());
            }
        }

        for (Booster booster : this.getActiveBoosters().keySet()) {
            this.data.set("activeBoosters." + booster.getId() + ".activatedBy", booster.getActivatedBy().toString());
            this.data.set("activeBoosters." + booster.getId() + ".activatedAt", booster.getActivatedAt());
        }

        for (Map.Entry<UUID, List<Booster>> entry : this.boostersQueued.entrySet()) {
            for (Booster booster : entry.getValue()) {
                this.data.set("boostersQueued." + entry.getKey() + "." + booster.getId(), 1);
            }
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Booster, UUID> getActiveBoosters() {
        final Map<Booster, UUID> toReturn = new HashMap<>();

        for (Booster boostersType : boostersTypes) {
            if (boostersType.isActive() && boostersType.getActivatedBy() != null) {
                toReturn.put(boostersType, boostersType.getActivatedBy());
            }
        }

        return toReturn;
    }

    public boolean isFrenzy() {
        return this.getActiveBoosters().containsKey(this.findBooster("Frenzy"));
    }

    public boolean isDoublePoints() {
        return this.getActiveBoosters().containsKey(this.findBooster("2xPoints"));
    }

    public boolean isReducedEnderpearl() {
        return this.getActiveBoosters().containsKey(this.findBooster("ReducedEnderpearl"));
    }

    public Booster findBooster(String id) {
        return this.boostersTypes.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
