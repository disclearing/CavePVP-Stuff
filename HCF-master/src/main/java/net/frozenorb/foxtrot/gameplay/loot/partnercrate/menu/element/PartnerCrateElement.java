package net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.element;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerCrateCommand;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.PreviewMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class PartnerCrateElement extends Button {
    private PartnerType partnerType;

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
    public void clicked(Player player, int i, ClickType clickType) {

        if (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT) {
            new PreviewMenu(partnerType).openMenu(player);
            return;
        }

        ItemStack itemStack = Arrays.stream(player.getInventory().getContents()).filter(it -> PartnerCrateCommand.isSimilar(partnerType, it)).findFirst().orElse(null);

        if (itemStack == null) {
            player.sendMessage(ChatColor.RED + "You do not have a " + partnerType.getCrateName() + " key in your inventory, purchase one at https://store.cavepvp.org");
            return;
        }

        if (itemStack.getAmount() == 1) {
            player.getInventory().remove(itemStack);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "cr forceopen " + player.getName() + " " + partnerType.getCrateName());
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return partnerType.getItemStack(false);
    }
}
