package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button.EditKitMenu;
import net.frozenorb.foxtrot.util.InventoryUtils;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public final class KitsMenu extends Menu {

    public KitsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Edit Kits";
    }

    @Override
    public void onOpen(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().getKitManager().awaitingRestore(player)) {
            Foxtrot.getInstance().getMapHandler().getKitManager().saveState(player);
        }

        InventoryUtils.resetInventoryNow(player);
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryNow(player);

        Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            Menu menu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());
            boolean stillEditing = menu instanceof KitsMenu || menu instanceof EditKitMenu;

            if (!stillEditing) {
                Foxtrot.getInstance().getMapHandler().getKitManager().restoreState(player);
            }
        }, 2L);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        List<DefaultKit> kits = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKits();
        kits.sort(Comparator.comparingInt(DefaultKit::getOrder));

        for (DefaultKit originalKit : kits) {
            Optional<Kit> kitOpt = Optional.ofNullable(Foxtrot.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit));

            int column = index;

            buttons.put(getSlot(column, 0), new KitIconButton(kitOpt, originalKit));
            buttons.put(getSlot(column, 2), new KitEditButton(kitOpt, originalKit));

            if (kitOpt.isPresent()) {
                buttons.put(getSlot(column, 3), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 4), new KitDeleteButton(kitOpt.get()));
            } else {
                buttons.put(getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
                buttons.put(getSlot(column, 4), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
            }

            index += 2;

            if (index > 9) break;
        }

        for (int i = 0; i < 44; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
            }
        }

        return buttons;
    }

}