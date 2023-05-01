package net.frozenorb.foxtrot.team.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import net.frozenorb.foxtrot.util.FallTrapGenerationTask;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.*;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.coinshop.CoinShop;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

@AllArgsConstructor
public class FallTrapMenu extends Menu {

    Team team;

    Claim claim;

    @Override
    public String getTitle(Player player) {
        return "Choose a color";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = Maps.newHashMap();

        for (int i = 0; i < 16; i++) {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, (byte) i);
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GREEN + "Click to select " + CraftItemStack.asNMSCopy(item).getName() + ".";
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (!VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        player.closeInventory();
                        return;
                    }

                    if (team.getFallTrapTokens() >= 1) {
                        team.setFallTrapTokens(team.getFallTrapTokens() - 1);

                        player.closeInventory();

                        new FallTrapGenerationTask(team, claim, Material.STAINED_CLAY, item.getData().getData()).start();
                    } else {
                        player.closeInventory();

                        player.sendMessage(ChatColor.RED + "You seem not to have enough faller tokens for that.");
                        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1F, 1F);
                    }
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Lists.newArrayList();
                }

                @Override
                public Material getMaterial(Player player) {
                    return item.getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return item.getData().getData();
                }
            });
        }

        for (int i = 0; i < 16; i++) {
            ItemStack item = new ItemStack(Material.WOOL, 1, (byte) i);
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GREEN + "Click to select " + CraftItemStack.asNMSCopy(item).getName() + ".";
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (!VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        player.closeInventory();
                        return;
                    }

                    if (team.getFallTrapTokens() >= 1) {
                        team.setFallTrapTokens(team.getFallTrapTokens() - 1);

                        player.closeInventory();

                        new FallTrapGenerationTask(team, claim, Material.WOOL, item.getData().getData()).start();
                    } else {
                        player.closeInventory();

                        player.sendMessage(ChatColor.RED + "You seem not to have enough faller tokens for that.");
                        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1F, 1F);
                    }
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Lists.newArrayList();
                }

                @Override
                public Material getMaterial(Player player) {
                    return item.getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return item.getData().getData();
                }
            });
        }

        buttons.put(buttons.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Click to select " + CraftItemStack.asNMSCopy(new ItemStack(Material.SMOOTH_BRICK)).getName() + ".";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                    player.closeInventory();
                    return;
                }

                if (team.getFallTrapTokens() >= 1) {
                    team.setFallTrapTokens(team.getFallTrapTokens() - 1);

                    player.closeInventory();

                    new FallTrapGenerationTask(team, claim, Material.SMOOTH_BRICK, (byte)5).start();
                } else {
                    player.closeInventory();

                    player.sendMessage(ChatColor.RED + "You seem not to have enough faller tokens for that.");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1F, 1F);
                }
            }

            @Override
            public List<String> getDescription(Player player) {
                return Lists.newArrayList();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SMOOTH_BRICK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }
        });

        buttons.put(buttons.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Click to select " + CraftItemStack.asNMSCopy(new ItemStack(Material.STONE)).getName() + ".";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                    player.closeInventory();
                    return;
                }

                if (team.getFallTrapTokens() >= 1) {
                    team.setFallTrapTokens(team.getFallTrapTokens() - 1);

                    player.closeInventory();

                    new FallTrapGenerationTask(team, claim, Material.STONE, (byte)5).start();
                } else {
                    player.closeInventory();

                    player.sendMessage(ChatColor.RED + "You seem not to have enough faller tokens for that.");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1F, 1F);
                }
            }

            @Override
            public List<String> getDescription(Player player) {
                return Lists.newArrayList();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EYE_OF_ENDER;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }
        });

        return buttons;
    }
}
