package org.cavepvp.suge.enchant.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;
import org.cavepvp.suge.kit.data.Category;
import org.cavepvp.suge.util.RomanUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CEListMenu extends Menu {
    private Tier tier;

    @Override
    public String getTitle(Player player) {
        return tier.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (CustomEnchant customEnchant : Suge.getInstance().getEnchantHandler().getCustomEnchants()) {

            if (!customEnchant.getLevel().equals(tier)) {
                continue;
            }

            toReturn.put(toReturn.size() + 10, new Button() {
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
                    final String romanNumber = RomanUtil.map.entrySet().stream().filter(it -> it.getValue() == customEnchant.getAmplifier()).map(Map.Entry::getKey).findFirst().orElse(null);

                    return Suge.getInstance().getEnchantHandler().getEnchantBookHandler().getBook(customEnchant, romanNumber).clone();
                }
            });
        }

        return toReturn;
    }

    @Override
    public void onClose(Player player) {
        Suge.getInstance().getServer().getScheduler().runTaskLater(Suge.getInstance(), () -> {
            new CEMenu().openMenu(player);
        },1);
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }
}
