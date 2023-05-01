package net.frozenorb.foxtrot.gameplay.events.outposts;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.command.parameter.OutpostParameterType;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.RoadOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.kitmap.KitmapOutpost;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutpostHandler {
    @Getter private Foxtrot instance;
    @Getter private File file;
    @Getter private FileConfiguration data;
    @Getter private List<Outpost> outposts = new ArrayList<>();

    public OutpostHandler(Foxtrot instance) {
        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(new OutpostListener(this), instance);

        Proton.getInstance().getCommandHandler().registerParameterType(Outpost.class, new OutpostParameterType());

        new OutpostTask(this).runTaskTimer(instance, 30, 20);

        this.loadOutposts();
        this.loadData();
    }

    public void loadData() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/outpost.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.getConfigurationSection("outposts") == null) {
            return;
        }

        for (String key : this.data.getConfigurationSection("outposts").getKeys(false)) {
            final Outpost outpost = this.findOutpost(key);

            if (outpost == null) {
                continue;
            }

            if (this.data.contains("outposts." + key + ".controller")) {
                final ObjectId controlId = new ObjectId(this.data.getString("outposts." + key + ".controller"));
                final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(controlId);

                if (team != null) {
                    outpost.setControl(controlId);
                }
            }

            outpost.setPercentage(new AtomicDouble(this.data.getDouble("outposts." + key + ".percentage")));
        }
    }

    public void saveData() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Outpost outpost : this.outposts) {
            if (outpost.getControl() != null) {
                this.data.set("outposts." + outpost.getId() + ".controller", outpost.getControl().toString());
            }
            this.data.set("outposts." + outpost.getId() + ".percentage", outpost.getPercentage().get());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.findRoadOutpost() != null) {
            this.findRoadOutpost().saveOutpostLoot();
        }
    }

    public void loadOutposts() {

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            this.outposts.add(new KitmapOutpost());
            return;
        }

        this.outposts.add(new RoadOutpost());
        this.outposts.add(new NetherOutpost());
        this.outposts.add(new EndOutpost());
    }

    public Outpost findOutpost(String id) {
        return this.outposts.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public RoadOutpost findRoadOutpost() {
        return (RoadOutpost) this.findOutpost("Road");
    }

    public RoadOutpost findKitsOutpost() {
        return (RoadOutpost) this.findOutpost("Kits");
    }

    public List<Outpost> findControllingOutposts(Team team) {
        return this.findControllingOutpost(team.getUniqueId());
    }

    public List<Outpost> findControllingOutpost(ObjectId objectId) {
        return this.getOutposts().stream()
                .filter(outpost -> outpost.getControl() != null && outpost.getControl().toString().equals(objectId.toString())).collect(Collectors.toList());
    }

    public Outpost findOutpost(Location location) {
        return outposts.stream().filter(it -> it.onCapzone(location)).findFirst().orElse(null);
    }
}
