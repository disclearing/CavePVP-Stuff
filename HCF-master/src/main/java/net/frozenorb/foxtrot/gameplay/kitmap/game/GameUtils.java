package net.frozenorb.foxtrot.gameplay.kitmap.game;

import lombok.experimental.UtilityClass;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@UtilityClass
public class GameUtils {

    public static void resetPlayer(Player player) {
        InventoryUtils.resetInventoryNow(player);

        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(4, GameItems.LEAVE_EVENT);
        player.updateInventory();
    }

}
