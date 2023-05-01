package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class EnchantmentUpgradeButton extends Button {

    private final Material material;
    private final Enchantment enchantment;
    private final int cost;
    private final ItemStack icon;

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Map<Material, Upgrades> map = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
        Upgrades upgrades = map.get(material);

        if (upgrades != null && upgrades.getEnchantmentList().contains(enchantment)) {
            player.sendMessage(CC.RED + "You already unlocked this upgrade!");
            return;
        }

        if (!Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), cost)) {
            player.sendMessage(CC.RED + "You need at least " + cost + " gems to buy this upgrade!");
            return;
        }

        map.computeIfAbsent(material, mat -> new Upgrades()).getEnchantmentList().add(enchantment);
        player.sendMessage(CC.GREEN + "Upgrade successfully purchased!");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder esmee = ItemBuilder.copyOf(icon.clone())
                .addToLore("")
                .addToLore(CC.GOLD + "Cost: " + CC.GREEN + cost + " Gems");

        Map<Material, Upgrades> map = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
        Upgrades upgrades = map.get(material);

        esmee.addToLore("");

        if (upgrades != null && upgrades.getEnchantmentList().contains(enchantment)) {
            esmee.addToLore(CC.RED + "You already unlocked this upgrade!");
        } else {
            esmee.addToLore(CC.GREEN + "Click to purchase this upgrade!");
        }

        return esmee.build();
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}
