package net.splodgebox.monthlycrates.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.splodgebox.monthlycrates.Core;
import net.splodgebox.monthlycrates.utils.Util;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyCrateCommand {

    @Command(names = {"mcrate give"}, permission = "mcrate.give")
    public static void execute(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "crateName") String crateName, @Parameter(name = "amount") int amount) {
        if (!Core.getInstance().getConfig().getConfigurationSection("crates").getKeys(false).contains(crateName)) {
            sender.sendMessage(Util.c("&c&l(!) &cThat is an invalid crate name!"));
            return;
        }
        Core.getInstance().crateUtils.giveCrate(crateName, target, amount, sender);
    }

    @Command(names = {"mcrate giveall"}, permission = "mcrate.giveall")
    public static void giveall(CommandSender sender, @Parameter(name = "crateName") String crateName, @Parameter(name = "amount") int amount) {
        if (!Core.getInstance().getConfig().getConfigurationSection("crates").getKeys(false).contains(crateName)) {
            sender.sendMessage(Util.c("&c&l(!) &cThat is an invalid crate name!"));
            return;
        }

        for (Player onlinePlayer : Core.getInstance().getServer().getOnlinePlayers()) {
            Core.getInstance().crateUtils.giveCrate(crateName, onlinePlayer, amount, sender);

        }
    }

    @Command(names = {"mcrate view"}, permission = "")
    public static void view(Player player, @Parameter(name = "crate")String crateName) {
        new Menu() {

            @Override
            public String getTitle(Player player) {
                return "Mystery Box";
            }

            @Override
            public boolean isPlaceholder() {
                return true;
            }

            @Override
            public int size(Player player) {
                return 27;
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                toReturn.put(13, new Button() {
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
                        final List<String> lores = new ArrayList<>();
                        for (final String string : Core.getInstance().getConfig().getStringList("crates." + crateName + ".crate.Lores")) {
                            lores.add(string.replace("%player%", player.getName()));
                        }

                        return Util.createItemStack(Material.valueOf(Core.getInstance().getConfig().getString("crates." + crateName + ".crate.Material")), 1, Core.getInstance().getConfig().getString("crates." + crateName + ".crate.Name"), Core.getInstance().getConfig().getBoolean("crates." + crateName + ".crate.Glow"), Core.getInstance().getConfig().getInt("crates." + crateName + ".crate.ItemData"), lores).clone();
                    }
                });



                return toReturn;
            }
        }.openMenu(player);
    }

}
