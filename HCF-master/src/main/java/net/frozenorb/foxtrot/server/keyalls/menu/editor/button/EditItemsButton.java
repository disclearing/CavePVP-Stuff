package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EditItemsButton extends Button {
    private KeyAll keyAll;

    @Override
    public String getName(Player player) {
        return ChatColor.translate("&6&lEdit Items");
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6&lItems:"));
        for (ItemStack item : keyAll.getItems()) {
            String name = item.getType().name();

            if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) {
                name = ChatColor.translate(item.getItemMeta().getDisplayName());
            }

            toReturn.add(ChatColor.translate("&6❙ &f" + item.getAmount() + "x " + name));
        }
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Type /keyall setitems to set the items");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {

        if (keyAll.getItems().isEmpty()) {
            return Material.PAPER;
        }

        return keyAll.getItems().get(0).getType();
    }
}
