package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.GemFlipHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipWager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WagerListMenu extends Menu {

    private GemFlipHandler gemFlipHandler = Foxtrot.getInstance().getGemFlipHandler();

    @Override
    public String getTitle(Player player) {
        return "Wagers";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for(int i = 0; i < 8; i++) {
            if (i + 1 > gemFlipHandler.getQueue().size()) {
                continue;
            }

            GemFlipEntry entry = gemFlipHandler.getQueue().get(i);

            if (entry == null) {
                continue;
            }

            Player creator = entry.getCreator();

            toReturn.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + creator.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&2&l❙ &fWager: &a" + entry.getAmount()));
                    toReturn.add(ChatColor.translate("&2&l❙ &fChosen Side: &a" + entry.getChosenSide().getFriendlyName()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to accept this gem flip wager.");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) SkullType.PLAYER.ordinal();
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.copyOf(super.getButtonItem(player)).skull(creator.getName()).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    final Player opponent = entry.getCreator();

                    if (opponent == null || !opponent.isOnline()) {
                        return;
                    }

                    if (opponent.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You cannot make a wager against yourself.");
                        return;
                    }

                    final GemFlipEntry entry = gemFlipHandler.getEntry(opponent);

                    if (entry == null || entry.isStarted()) {
                        player.sendMessage(ChatColor.RED + "This wager is no longer available.");
                        return;
                    }

                    long amount = entry.getAmount();
                    if (Foxtrot.getInstance().getGemMap().getGems(player.getUniqueId()) < amount) {
                        player.sendMessage(ChatColor.RED + "Insufficient Gems!");
                        return;
                    }

                    gemFlipHandler.getQueue().remove(entry);
                    entry.setStarted(true);

                    Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), amount);

                    entry.getCreator().sendMessage(ChatColor.translate(player.getName() + " &ahas accepted your wager!"));
                    player.sendMessage(ChatColor.translate("&aYou have accepted &f" + entry.getCreator().getName() + "'s &awager!"));

                    Foxtrot.getInstance().getGemFlipHandler().setTotalWagered(Foxtrot.getInstance().getGemFlipHandler().getTotalWagered()+(entry.getAmount()*2));

                    GemFlipWager wager = new GemFlipWager(entry.getAmount() * 2,entry.getCreator(), player, entry.getChosenSide(), entry.getChosenSide().getOpposite());
                    wager.start();
                }
            });
        }

        toReturn.put(8, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&2&lGem Flip");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "How to wager?",
                        ChatColor.translate("&2&l┃ &f/gf wager <amount> - &aAdd a wager"),
                        ChatColor.translate("&2&l┃ &f/gf cancel - &aRemove a wager"),
                        "",
                        ChatColor.GREEN + "Click an active wager to place your bet.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }
        });

        return toReturn;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
