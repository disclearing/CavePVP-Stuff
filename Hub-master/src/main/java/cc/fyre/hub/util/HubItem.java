package cc.fyre.hub.util;

import cc.fyre.hub.Hub;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class HubItem {

    private String menu;

    @Getter
    private ItemStack itemStack;

    public Menu getMenu() {
        return Hub.getInstance().getMenus().get(this.menu);
    }

    public boolean hasMenu() {
        return this.menu != null;
    }

}
