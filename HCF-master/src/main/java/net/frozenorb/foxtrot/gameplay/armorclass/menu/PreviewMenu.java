package net.frozenorb.foxtrot.gameplay.armorclass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PreviewMenu extends Menu {
    private ArmorClass armorClass;
    
    @Override
    public String getTitle(Player player) {
        return "Kit Preview";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final ItemBuilder itemBuilder = ItemBuilder.of(Material.DIAMOND_SWORD)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchant(Enchantment.DURABILITY, 3)
                .name(armorClass.getDisplayName() + " Sword")
                .addToLore("", armorClass.getChatColor() + "Armor Class: &f" + ChatColor.stripColor(armorClass.getDisplayName()), armorClass.getChatColor() + "Perks:");
        for (String perk : armorClass.getPerks()) {
            itemBuilder.addToLore(armorClass.getChatColor() + "❙ &f" + perk);
        }

        toReturn.put(11, Button.fromItem(itemBuilder.build()));
        toReturn.put(12, Button.fromItem(armorClass.createPiece(ArmorPiece.HELMET)));
        toReturn.put(13, Button.fromItem(armorClass.createPiece(ArmorPiece.CHESTPLATE)));
        toReturn.put(14, Button.fromItem(armorClass.createPiece(ArmorPiece.LEGGINGS)));
        toReturn.put(15, Button.fromItem(armorClass.createPiece(ArmorPiece.BOOTS)));
        
        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public void onClose(Player player) {
        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> new ArmorClassesMenu(armorClass.getCategory()).openMenu(player), 1);
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }
}
