package net.frozenorb.foxtrot.gameplay.killtags;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillTagMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Kill Tags";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, ""));
        }

        final KillTags currentTag = Foxtrot.getInstance().getKillTagMap().getKillTag(player.getUniqueId());

        int i = 0;

        for (KillTags value : KillTags.values()) {
            if (value.equals(KillTags.NONE)) {
                continue;
            }

            i++;

            toReturn.put(i + 9, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + value.getDescription());
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&cSimplyTrash&4[0]&e" + value.getDeathTag() + "&c" + player.getName() + "&4[" + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()) + "]"));
                    toReturn.add("");

                    if (!player.hasPermission("killtags.access")) {
                        toReturn.add(ChatColor.RED + "You must have VIP status to use this Kill Tag!");
                        toReturn.add(ChatColor.GRAY + "Purchase VIP status at " + ChatColor.WHITE + "https://store.cavepvp.org" + ChatColor.GRAY + ".");
                    } else if (value.equals(currentTag)) {
                        toReturn.add(ChatColor.RED + "Click to deactivate this kill tag");
                    } else {
                        toReturn.add(ChatColor.GREEN + "Click to activate this kill tag");
                    }

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.NAME_TAG;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    final ItemStack itemStack = super.getButtonItem(player);

                    if (value.equals(currentTag)) {
                        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                    }

                    return itemStack.clone();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (!player.hasPermission("killtags.access")) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "You must have VIP status to use this Kill Tag!");
                        player.sendMessage(ChatColor.GRAY + "Purchase VIP status at " + ChatColor.WHITE + "https://store.cavepvp.org" + ChatColor.GRAY + ".");
                        return;
                    }

                    if (currentTag.equals(value)) {
                        player.closeInventory();
                        Foxtrot.getInstance().getKillTagMap().setKillTag(player.getUniqueId(), KillTags.NONE);
                        player.sendMessage(ChatColor.RED + "Reset your kill tag");
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.GOLD + "Kill Tag: " + ChatColor.WHITE + value.getDisplayName());
                    Foxtrot.getInstance().getKillTagMap().setKillTag(player.getUniqueId(), value);
                    player.closeInventory();
                }
            });
        }

        return toReturn;
    }
}
