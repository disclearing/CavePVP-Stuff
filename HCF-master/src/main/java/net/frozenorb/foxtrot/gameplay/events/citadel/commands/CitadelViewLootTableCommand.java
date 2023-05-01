package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitadelViewLootTableCommand {

    @Command(names = {"citadel viewloot", "citadel loot", "citadelloot"}, permission = "")
    public static void execute(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(ChatColor.RED + "This command can only be used on Kits!");
            return;
        }

        new Menu() {
            @Override
            public String getTitle(Player player) {
                return "Citadel Loot";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                for (ItemStack itemStack : Foxtrot.getInstance().getCitadelHandler().getCitadelLoot()) {

                    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                        continue;
                    }

                    toReturn.put(toReturn.size(), new Button() {
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
                        public ItemStack getButtonItem(Player player) {
                            return itemStack.clone();
                        }
                    });
                }

                return toReturn;
            }
        }.openMenu(player);

    }

}
