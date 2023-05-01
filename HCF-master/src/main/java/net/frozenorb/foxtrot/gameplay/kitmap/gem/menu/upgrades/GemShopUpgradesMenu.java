package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.blockshop.menu.BackButton;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.GemShopMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.GlassButton;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GemShopUpgradesMenu extends Menu {

    private static final Map<Material, Upgrades> ARCHER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> BARD_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> DIAMOND_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> ROGUE_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> MINER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> WITHER_SKELETON = new LinkedHashMap<>();

    static {
        ARCHER_MAP.put(Material.LEATHER_HELMET, new Upgrades().protection().repair().bubble());
        ARCHER_MAP.put(Material.LEATHER_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        ARCHER_MAP.put(Material.LEATHER_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        ARCHER_MAP.put(Material.LEATHER_BOOTS, new Upgrades().protection().repair());
        ARCHER_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());
        ARCHER_MAP.put(Material.BOW, new Upgrades().power().repair());

        WITHER_SKELETON.put(Material.LEATHER_HELMET, new Upgrades().protection().repair().bubble());
        WITHER_SKELETON.put(Material.DIAMOND_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        WITHER_SKELETON.put(Material.CHAINMAIL_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        WITHER_SKELETON.put(Material.CHAINMAIL_BOOTS, new Upgrades().protection().repair());
        WITHER_SKELETON.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        BARD_MAP.put(Material.GOLD_HELMET, new Upgrades().protection().repair().bubble());
        BARD_MAP.put(Material.GOLD_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        BARD_MAP.put(Material.GOLD_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        BARD_MAP.put(Material.GOLD_BOOTS, new Upgrades().protection().repair());
        BARD_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        DIAMOND_MAP.put(Material.DIAMOND_HELMET, new Upgrades().protection().repair().bubble());
        DIAMOND_MAP.put(Material.DIAMOND_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        DIAMOND_MAP.put(Material.DIAMOND_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        DIAMOND_MAP.put(Material.DIAMOND_BOOTS, new Upgrades().protection().repair().speed());
        DIAMOND_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        ROGUE_MAP.put(Material.CHAINMAIL_HELMET, new Upgrades().protection().repair().bubble());
        ROGUE_MAP.put(Material.CHAINMAIL_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        ROGUE_MAP.put(Material.CHAINMAIL_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        ROGUE_MAP.put(Material.CHAINMAIL_BOOTS, new Upgrades().protection().repair());
        ROGUE_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        MINER_MAP.put(Material.IRON_HELMET, new Upgrades().protection().repair().bubble());
        MINER_MAP.put(Material.IRON_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        MINER_MAP.put(Material.IRON_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        MINER_MAP.put(Material.IRON_BOOTS, new Upgrades().protection().repair().speed());
        MINER_MAP.put(Material.DIAMOND_PICKAXE, new Upgrades().efficiency().repair());
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            buttons.put(i, new GlassButton(i % 2 == 0 ? 5 : 13));
        }

        buttons.put(35, new BackButton((player1, i, clickType) -> new GemShopMenu().openMenu(player)));

        UpgradableKit[] upgradableKits = new UpgradableKit[]{
                new UpgradableKit("Diamond",
                        ItemBuilder.of(Material.DIAMOND_HELMET)
                                .name(CC.AQUA + CC.BOLD + "Upgrade Diamond Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        DIAMOND_MAP
                ),
                new UpgradableKit("Archer",
                        ItemBuilder.of(Material.LEATHER_HELMET)
                                .name(CC.PINK + CC.BOLD + "Upgrade Archer Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        ARCHER_MAP
                ),
                new UpgradableKit("Bard",
                        ItemBuilder.of(Material.GOLD_HELMET)
                                .name(CC.GOLD + CC.BOLD + "Upgrade Bard Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        BARD_MAP
                ),
                new UpgradableKit("Rogue",
                        ItemBuilder.of(Material.CHAINMAIL_HELMET)
                                .name(CC.DARK_AQUA + CC.BOLD + "Upgrade Rogue Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        ROGUE_MAP
                ),
                new UpgradableKit("Miner",
                        ItemBuilder.of(Material.IRON_HELMET)
                                .name(CC.WHITE + CC.BOLD + "Upgrade Miner Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        MINER_MAP
                ),
                new UpgradableKit("WitherSkeleton",
                        ItemBuilder.of(Material.LEATHER_HELMET)
                                .name(CC.DARK_RED + CC.BOLD + "Upgrade Wither Skeleton Kit")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        WITHER_SKELETON
                )
        };

        int[] slots = new int[]{12, 13, 14, 21, 22, 23};

        for (int i = 0; i < upgradableKits.length; i++) {
            UpgradableKit upgradableKit = upgradableKits[i];

            buttons.put(slots[i], new Button() {

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new GemShopUpgradeKitMenu(upgradableKit.getKitName(), upgradableKit.getUpgrades()).openMenu(player);
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return upgradableKit.getIcon();
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
        }
        return buttons;
    }
}
