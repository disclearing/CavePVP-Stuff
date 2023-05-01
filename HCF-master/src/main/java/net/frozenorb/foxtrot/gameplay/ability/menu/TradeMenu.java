package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TradeMenu extends Menu {
    private ItemStack itemStack;
    private Ability ability;

    @Override
    public String getTitle(Player player) {
        return "Pick another item";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final Category category = ability.getCategory();

        for (Ability chosen : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().filter(it -> it.getCategory().equals(category)).collect(Collectors.toList())) {
            toReturn.put(toReturn.size(), new Button() {
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

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.copyOf(chosen.hassanStack.clone()).amount(itemStack.getAmount()).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (!player.getInventory().contains(itemStack)) {
                        player.sendMessage(ChatColor.RED + "You don't have enough items anymore!");
                        return;
                    }

                    int amount = itemStack.getAmount();

                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.translate("&aTransferred " + ability.getDisplayName() + " &ato &f" + chosen.getDisplayName()));
                    player.closeInventory();

                    player.getInventory().remove(itemStack);
                    player.getInventory().addItem(ItemBuilder.copyOf(chosen.hassanStack.clone()).amount(amount).build());
                }
            });
        }

        return toReturn;
    }
}
