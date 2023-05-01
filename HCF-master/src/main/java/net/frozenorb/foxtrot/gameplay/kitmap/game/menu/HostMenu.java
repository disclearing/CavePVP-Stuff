package net.frozenorb.foxtrot.gameplay.kitmap.game.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Host an Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (GameType gameType : GameType.values()) {
            buttons.put(buttons.size(), new GameTypeButton(gameType));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class GameTypeButton extends Button {
        private final GameType gameType;

        @Override
        public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + gameType.getDisplayName();
        }

        @Override
        public List<String> getDescription(Player player) {
            final List<String> description = new ArrayList<>(Formats.renderLines(ChatColor.GRAY.toString(), gameType.getDescription()));
            description.add("");

            if (gameType.isDisabled()) {
                description.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DISABLED");
                description.add(ChatColor.DARK_RED + "This event has been disabled!");
            } else if (gameType.canHost(player)) {
                description.add(ChatColor.GREEN + "Click to host this event!");
            } else {
                description.add("");
                description.add(ChatColor.RED + "You must purchase " + gameType.getRank() + ChatColor.RED + " to host this event!");
                description.add(ChatColor.GRAY + "You may purchase this rank at store.cavepvp.org");
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return gameType.getIcon().getType();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick()) {
                if (gameType.isDisabled()) {
                    player.sendMessage(ChatColor.RED + "That event is temporarily disabled!");
                    return;
                }
                if (!gameType.canHost(player)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to host " + gameType.getDisplayName() + " events.");
                    return;
                }

                if (!Foxtrot.getInstance().getMapHandler().getGameHandler().canStartGame(player, gameType)) {
                    return;
                }

                try {
                    player.closeInventory();

                    Game game = Foxtrot.getInstance().getMapHandler().getGameHandler().startGame(player, gameType);
                    game.addPlayer(player);

                    player.sendMessage(ChatColor.GREEN + "Started " + gameType.getDisplayName() + "! You can use /start to forcefully start the event.");
                } catch (IllegalStateException e) {
                    player.sendMessage(ChatColor.RED.toString() + e.getMessage());
                }
            }
        }
    }

}
