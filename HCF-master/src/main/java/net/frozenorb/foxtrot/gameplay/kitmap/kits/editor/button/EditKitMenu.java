package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public final class EditKitMenu extends Menu {

    private static final int EDITOR_X_OFFSET = 2;
    private static final int EDITOR_Y_OFFSET = 2;

    private final Kit kit;

    public EditKitMenu(Kit kit) {
        setNoncancellingInventory(true);
        setUpdateAfterClick(false);

        this.kit = kit;
    }

    @Override
    public void onOpen(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            player.getInventory().setContents(kit.getInventoryContents());
            player.updateInventory();
        }, 1L);
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryNow(player);

        Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            new KitsMenu().openMenu(player);
        }, 1L);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // The vertical row
        for (int i = 0; i <= 5; i++) {
            buttons.put(getSlot(1, i), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));
        }

        // The horizontal row
        for (int i = 0; i <= 8; i++) {
            buttons.put(getSlot(i, 1), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));
        }

        // Fill up the 3 empty slots up top. they can be used to dupe items
        buttons.put(getSlot(3, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));
        buttons.put(getSlot(4, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));
        buttons.put(getSlot(5, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));

        buttons.put(getSlot(0, 0), new KitInfoButton(kit));
        buttons.put(getSlot(2, 0), new SaveButton(kit));

        buttons.put(getSlot(6, 0), new LoadDefaultKitButton(kit.getOriginal()));
        buttons.put(getSlot(7, 0), new ClearInventoryButton());
        buttons.put(getSlot(8, 0), new CancelKitEditButton());

        for (ItemStack armorItem : kit.getOriginal().getArmorContents()) {
            int armorYOffset = 2;
            int armorSlot = -1;

            if (armorItem.getType().name().contains("HELMET")) {
                armorSlot = 0;
            } else if (armorItem.getType().name().contains("CHESTPLATE")) {
                armorSlot = 1;
            } else if (armorItem.getType().name().contains("LEGGINGS")) {
                armorSlot = 2;
            } else if (armorItem.getType().name().contains("BOOTS")) {
                armorSlot = 3;
            }

            buttons.put(getSlot(0, armorSlot + armorYOffset), new ArmorButton(armorItem));
        }

        short splashHealPotionDura = -1;
        int x = 0;
        int y = 0;

        for (ItemStack editorItem : kit.getOriginal().getEditorItems()) {
            if (editorItem != null) {
                if (editorItem.getType() == Material.POTION && editorItem.getDurability() != 0) {
                    Potion potion = Potion.fromItemStack(editorItem);

                    if (potion.isSplash() && potion.getType() == PotionType.INSTANT_HEAL) {
                        splashHealPotionDura = editorItem.getDurability();
                    }
                }

                if (editorItem.getType() != Material.AIR) {
                    buttons.put(getSlot(x + EDITOR_X_OFFSET, y + EDITOR_Y_OFFSET), new TakeItemButton(editorItem));
                }
            }

            x++;

            if (x > 6) {
                x = 0;
                y++;

                if (y >= 4) {
                    break;
                }
            }
        }

        // Set fill all button with the potion id we detected
        if (splashHealPotionDura > 0) {
            buttons.put(getSlot(8, 5), new FillHealPotionsButton(splashHealPotionDura));
        }

        return buttons;
    }

}