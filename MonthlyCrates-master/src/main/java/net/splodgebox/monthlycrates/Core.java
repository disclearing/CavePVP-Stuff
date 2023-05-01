package net.splodgebox.monthlycrates;

import cc.fyre.proton.Proton;
import net.splodgebox.monthlycrates.animation.FinalAnimationService;
import net.splodgebox.monthlycrates.animation.ScrambleService;
import net.splodgebox.monthlycrates.command.MonthlyCrateCommand;
import net.splodgebox.monthlycrates.events.CrateListener;
import net.splodgebox.monthlycrates.utils.CrateUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Core extends JavaPlugin {
    public static Core instance;
    public String prefix;
    public CrateUtils crateUtils;

    public void onEnable() {
        Core.instance = this;
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }
        this.saveConfig();
        this.getServer().getPluginManager().registerEvents(new CrateListener(), this);
        this.crateUtils = new CrateUtils();
        new ScrambleService().runTaskTimer(this, 1L, this.getConfig().getInt("time.ScrambleAnimationSpeedInTicks"));
        new FinalAnimationService().runTaskTimer(this, 1L, this.getConfig().getInt("time.FinalAnimationSpeedInTicks"));
        this.prefix = this.getConfig().getString("messages.prefix");

        Proton.getInstance().getCommandHandler().registerClass(MonthlyCrateCommand.class);
    }

    public static Core getInstance() {
        return Core.instance;
    }
}
