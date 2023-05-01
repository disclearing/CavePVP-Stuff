package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.menu.page.ItemPaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.profiles.menu.FriendsMenu;
import org.cavepvp.profiles.menu.MenuType;
import org.cavepvp.profiles.menu.ProfilesSharedButtons;

import java.util.*;

@AllArgsConstructor
public class PartnerItemMenu extends Menu {
    private Category category;

    @Override
    public String getTitle(Player player) {
        return category.getSimpleName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final List<Ability> abilities = new ArrayList<>();

        for (Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {

            if (!category.isCategory(value, category)) {
                continue;
            }

            if (value.getCategory() == Category.PORTABLE_BARD) {
                continue;
            }

            if (!Foxtrot.getInstance().getMapHandler().isKitMap() && value.getCategory() == Category.KIT_MAP) {
                continue;
            }

            abilities.add(value);
        }

        int rows = (int) Math.ceil(abilities.size()/8)+3;

        for (int i = 0; i < (rows*9); i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, category.getGlassData(), ""));
        }

        toReturn.put(0, Button.placeholder(Material.STAINED_GLASS_PANE, category.getGlassData(), ""));

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "View All";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.RED + "Click to go back to the main menu");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new PartnerItemMainMenu().openMenu(player);
            }
        });

        int i = 0;
        int addition = 9;
        for (Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {

            if (!category.isCategory(value, category)) {
                continue;
            }

            if (value.getCategory() == Category.PORTABLE_BARD || !Foxtrot.getInstance().getMapHandler().isKitMap() && value.getCategory() == Category.KIT_MAP) {
                continue;
            }

            i++;

            if (i % 8 == 0) {
                i = 1;
                addition += 9;
            }

            toReturn.put(i+addition, new Button() {
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
                    return value.hassanStack.clone();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {

                    if (player.isOp()) {
                        player.getInventory().addItem(value.hassanStack.clone());
                    }

                }
            });
        }

        return toReturn;
    }
}
