package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Crate {
    @Getter private String id;
    @Getter private String displayName;
    @Getter private Material material;
    @Getter private Color fireworkColor;
    @Getter
    @Setter private List<String> lore;
    @Getter private List<CrateItem> items;

    public ItemStack getItemStack() {
        final List<String> newLore = new ArrayList<>();
        lore.forEach(it -> newLore.add(ChatColor.translate(it)));

        return ItemBuilder.of(this.material).name(ChatColor.translate(this.displayName)).setLore(newLore).build();
    }

    public void saveCrate(File file, FileConfiguration data, Crate crate) {
        Map<String, Object> configValues = data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            data.set(entry.getKey(), null);

        data.set("id", crate.getId());
        data.set("displayName", crate.getDisplayName());
        data.set("material", crate.getMaterial().name());
        data.set("fireworkColor", crate.getFireworkColor().asRGB());
        data.set("lore", crate.getLore());

        int i = 0;
        for (CrateItem crateItem : crate.getItems()) {
            i++;
            data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".itemStack", crateItem.getItemStack());
            data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".chance", crateItem.getChance());
            data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".command", crateItem.getCommand());
            data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".giveItem", crateItem.isGiveItem());
            data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".broadcast", crateItem.isBroadcast());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
