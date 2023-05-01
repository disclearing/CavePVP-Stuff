package org.cavepvp.suge;

import cc.fyre.proton.Proton;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.cavepvp.suge.enchant.EnchantHandler;
import org.cavepvp.suge.kit.KitHandler;

public class Suge extends JavaPlugin {

    @Getter private static Suge instance;

    @Getter private int size = 54;

    @Getter private KitHandler kitHandler;
    @Getter private EnchantHandler enchantHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.size = this.getConfig().getInt("size", 54);
        this.kitHandler = new KitHandler(this);
        this.enchantHandler = new EnchantHandler(this);

        Proton.getInstance().getCommandHandler().registerAll(this);
    }

    @Override
    public void onDisable() {
        this.kitHandler.saveKits();
        this.kitHandler.saveCooldowns();
    }
}
