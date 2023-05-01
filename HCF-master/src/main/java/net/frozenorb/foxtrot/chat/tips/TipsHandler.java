package net.frozenorb.foxtrot.chat.tips;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import lombok.Getter;

import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.tips.task.TipTask;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TipsHandler {

    public static String PREFIX = ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.BOLD + ChatColor.ITALIC + "TIP" + ChatColor.GRAY + "] " + ChatColor.GOLD;

    @Getter private Map<Integer, String> tips = new HashMap<>();
    @Getter @Setter private int interval;
    @Getter @Setter private TipTask tipTask;

    @Getter private File file;
    @Getter private FileConfiguration data;

    public TipsHandler(Foxtrot instance) {
        this.interval = instance.getConfig().getInt("tips.interval", 300);
        this.tipTask = new TipTask(instance, this);
        this.tipTask.runTaskTimer(instance, 20L * interval, 20L * interval);

        this.loadTips();
    }

    public void loadTips() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/tips.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("tips") == null) {
            return;
        }

        for (String key : this.data.getConfigurationSection("tips").getKeys(false)) {
            final String tip = this.data.getString("tips." + key + ".text");

            tips.put(Integer.parseInt(key.replace("id_", "")), tip);
        }
    }


    public void saveTips() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Map.Entry<Integer, String> tipEntry : this.tips.entrySet()) {
            this.data.set("tips.id_" + tipEntry.getKey() + ".text", tipEntry.getValue());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(names = {"tips setinterval"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "seconds")int seconds) {
        Foxtrot.getInstance().getTipsHandler().setInterval(seconds);

        Foxtrot.getInstance().getTipsHandler().getTipTask().cancel();
        Foxtrot.getInstance().getTipsHandler().setTipTask(new TipTask(Foxtrot.getInstance(), Foxtrot.getInstance().getTipsHandler()));
        Foxtrot.getInstance().getTipsHandler().getTipTask().runTaskTimer(Foxtrot.getInstance(), 20*seconds, 20*seconds);

        player.sendMessage(ChatColor.GOLD + "Set tip interval to " + ChatColor.WHITE + seconds);
    }

    @Command(names = {"tips add"}, permission = "op")
    public static void add(Player player, @Parameter(name = "id")int id, @Parameter(name = "tip", wildcard = true)String tip) {
        if (Foxtrot.getInstance().getTipsHandler().getTips().containsKey(id)) {
            player.sendMessage(ChatColor.RED + "Tip with the ID " + id + " already exists!");
            return;
        }

        Foxtrot.getInstance().getTipsHandler().getTipTask().getQueue().add(tip);
        Foxtrot.getInstance().getTipsHandler().getTips().put(id, tip);

        player.sendMessage(ChatColor.GOLD + "Added tip with the ID " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
        player.sendMessage("Sample: " + ChatColor.translate(PREFIX + tip));
    }

    @Command(names = {"tips delete"}, permission = "op")
    public static void delete(Player player, @Parameter(name = "id")int id) {
        if (!Foxtrot.getInstance().getTipsHandler().getTips().containsKey(id)) {
            player.sendMessage(ChatColor.RED + "Tip with the ID " + id + " doesn't exist!");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Deleted tip with the ID " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
        Foxtrot.getInstance().getTipsHandler().getTips().remove(id);
    }
}