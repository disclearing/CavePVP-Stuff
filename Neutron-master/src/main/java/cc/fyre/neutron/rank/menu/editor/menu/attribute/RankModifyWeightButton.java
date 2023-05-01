package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.RankEditorMenu;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankModifyWeightButton extends Button {

    @Getter private Rank rank;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Weight: " + ChatColor.WHITE + this.rank.getWeight();
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK to increment weight by 1");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK to decrement weight by 1");
        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT-CLICK to increment weight by 10");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT-RIGHT-CLICK to decrement weight by 10");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD;
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {

        if (clickType.isLeftClick() && !clickType.isShiftClick()) {
            this.rank.getWeight().incrementAndGet();
        } else if (clickType.isRightClick() && !clickType.isShiftClick()) {
            this.rank.getWeight().decrementAndGet();
        } else if (clickType.isLeftClick() && clickType.isShiftClick()) {
            this.rank.getWeight().set(this.rank.getWeight().get()+10);
        } else if (clickType.isRightClick() && clickType.isShiftClick()) {
            this.rank.getWeight().set(this.rank.getWeight().get()-10);
        }
        this.rank.save();

        RankModifyAttributesMenu.getIgnoreSave().add(this.rank.getUuid());

        player.sendMessage(ChatColor.GOLD + "Modified " + this.rank.getFancyName() + ChatColor.GOLD + "'s weight to: " + ChatColor.WHITE + this.rank.getWeight().get());
    }

}
