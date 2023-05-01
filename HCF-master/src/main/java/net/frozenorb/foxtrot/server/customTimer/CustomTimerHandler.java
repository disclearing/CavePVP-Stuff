package net.frozenorb.foxtrot.server.customTimer;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.customTimer.task.CustomTimerTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CustomTimerHandler {
    @Getter @Setter private String activeTimer = "";

    private File file = new File(Foxtrot.getInstance().getDataFolder(), "data/custom-timers.yml");
    private FileConfiguration data = YamlConfiguration.loadConfiguration(this.file);

    public CustomTimerHandler() {

        new CustomTimerTask().runTaskTimer(Foxtrot.getInstance(), 20*6, 20*6);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (data.get("timers") == null) {
            return;
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            for (String configEntry : data.getConfigurationSection("timers").getKeys(false))
                CustomTimerCreateCommand.getCustomTimers().put(configEntry, data.getLong("timers." + configEntry));
        }, 30L);
    }

    public void saveData() {
        Map<String, Object> configValues = this.data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            this.data.set(entry.getKey(), null);

        for (Map.Entry<String, Long> mapEntry : CustomTimerCreateCommand.getCustomTimers().entrySet()) {
            data.set("timers." + mapEntry.getKey(), mapEntry.getValue());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
