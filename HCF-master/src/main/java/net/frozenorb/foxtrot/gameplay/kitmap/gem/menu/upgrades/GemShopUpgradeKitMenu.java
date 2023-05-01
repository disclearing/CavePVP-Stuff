package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.blockshop.menu.BackButton;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.GlassButton;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemShopUpgradeKitMenu extends Menu {

    public static final Map<String, Integer> LEVEL_MAP = new HashMap<>();
    private static final Map<String, ItemStack> ICON_MAP = new HashMap<>();
    private static final Map<String, Integer> COST_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put("PROTECTION_ENVIRONMENTAL", 2);
        LEVEL_MAP.put("DAMAGE_ALL", 2);
        LEVEL_MAP.put("ARROW_DAMAGE", 5);
        LEVEL_MAP.put("DIG_SPEED", 6);

        LEVEL_MAP.put("FireResistance", 1);
        LEVEL_MAP.put("Glowing", 1);
        LEVEL_MAP.put("HellForged", 4);
        LEVEL_MAP.put("Implants", 5);
        LEVEL_MAP.put("Mermaid", 1);
        LEVEL_MAP.put("Recover", 1);
        LEVEL_MAP.put("Speed", 2);

        ICON_MAP.put("PROTECTION_ENVIRONMENTAL", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.GOLD + CC.BOLD + "Protection II").build());
        ICON_MAP.put("DAMAGE_ALL", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.RED + CC.BOLD + "Sharpness II").build());
        ICON_MAP.put("ARROW_DAMAGE", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.DARK_RED + CC.BOLD + "Power V").build());
        ICON_MAP.put("DIG_SPEED", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.YELLOW + CC.BOLD + "Efficiency VI").build());

        ICON_MAP.put("FireResistance", ItemBuilder.of(Material.MAGMA_CREAM).name(CC.GOLD + CC.BOLD + "Fire Resistance I").build());
        ICON_MAP.put("Glowing", ItemBuilder.of(Material.TORCH).name(CC.DARK_BLUE + CC.BOLD + "Night Vision I").build());
        ICON_MAP.put("HellForged", ItemBuilder.of(Material.ANVIL).name(CC.YELLOW + CC.BOLD + "Repair IV").build());
        ICON_MAP.put("Implants", ItemBuilder.of(Material.COOKED_BEEF).name(CC.GOLD + CC.BOLD + "Saturation V").build());
        ICON_MAP.put("Mermaid", ItemBuilder.of(Material.WATER_BUCKET).name(CC.BLUE + CC.BOLD + "Bubble I").build());
        ICON_MAP.put("Recover", ItemBuilder.of(Material.GOLDEN_APPLE).name(CC.PINK + CC.BOLD + "Recover I").build());
        ICON_MAP.put("Speed", ItemBuilder.of(Material.SUGAR).name(CC.AQUA + CC.BOLD + "Speed II").build());

        COST_MAP.put("PROTECTION_ENVIRONMENTAL", 750);
        COST_MAP.put("DAMAGE_ALL", 1000);
        COST_MAP.put("ARROW_DAMAGE", 1000);
        COST_MAP.put("DIG_SPEED", 200);

        COST_MAP.put("FireResistance", 1000);
        COST_MAP.put("Glowing", 100);
        COST_MAP.put("HellForged", 200);
        COST_MAP.put("Implants", 300);
        COST_MAP.put("Mermaid", 100);
        COST_MAP.put("Recover", 200);
        COST_MAP.put("Speed", 2000);
    }

    private final String kitName;
    private final Map<Material, Upgrades> map;

    public GemShopUpgradeKitMenu(String kitName, Map<Material, Upgrades> map) {
        this.kitName = kitName;
        this.map = map;
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < map.size() * 9; i++) {
            buttons.put(i, new GlassButton(7));
        }

        buttons.put(map.size() * 9 - 1, new BackButton((p, i, clickType) -> new GemShopUpgradesMenu().openMenu(player)));

        Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(kitName);

        if (kit == null) {
            return buttons;
        }

        ItemStack[] items = kit.getAllContents();
        int index = 0;

        Map<Material, Upgrades> upgradesMap = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getUpgrades(player);

        for (Map.Entry<Material, Upgrades> entry : map.entrySet()) {
            ItemStack icon = null;

            for (ItemStack stack : items) {
                if (stack != null && entry.getKey() == stack.getType()) {
                    icon = stack.clone();
                    break;
                }
            }

            if (icon == null) continue; // Item is not in the kit

            if (upgradesMap != null) {
                Kit.doKitUpgradesMagic(upgradesMap, icon);
            }

            final ItemStack finalIcon = icon;

            buttons.put(index, new Button() {

                @Override
                public ItemStack getButtonItem(Player player) {
                    return finalIcon;
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
            });

            int x = 1;

            for (Enchantment enchantment : entry.getValue().getEnchantmentList()) {
                Integer cost = COST_MAP.get(enchantment.getName());
                if (cost == null) continue;

                buttons.put(index + 1 + x++, new EnchantmentUpgradeButton(
                        entry.getKey(),
                        enchantment,
                        cost,
                        ICON_MAP.get(enchantment.getName())
                ));
            }

            for (String enchantment : entry.getValue().getCustomEnchantmentList()) {
                Integer cost = COST_MAP.get(enchantment);
                if (cost == null) continue;

                buttons.put(index + 1 + x++, new CustomEnchantmentUpgradeButton(
                        entry.getKey(),
                        enchantment,
                        cost,
                        ICON_MAP.get(enchantment)
                ));
            }

            index += 9;
        }

        return buttons;
    }
}
