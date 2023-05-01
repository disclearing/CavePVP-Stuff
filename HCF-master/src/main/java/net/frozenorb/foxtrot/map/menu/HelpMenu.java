package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Help";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lVote");
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Vote for us every day for free rewards!");
                toReturn.add("");
                toReturn.add("&4&lRewards:");
                toReturn.add("&4&l┃ &fEach Vote: &b&l3x Rare Keys");
                toReturn.add("");
                toReturn.add("&aClick here to view all vote links");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQxZmJlYTljMmQxOTA0MGU1NjdmMzg3YWI0NmIyZjhhM2ExZGE4ZWVjOWQzOTllMmU0YWRjZjA1YWRhOGEyYSJ9fX0=";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/vote");
            }
        });

        toReturn.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lStore");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Purchase coins, ranks, keys, loot, items");
                toReturn.add("&7and more to gain items and get setup easier.");
                toReturn.add("");
                toReturn.add("&aClick here to open the store");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQxZmJlYTljMmQxOTA0MGU1NjdmMzg3YWI0NmIyZjhhM2ExZGE4ZWVjOWQzOTllMmU0YWRjZjA1YWRhOGEyYSJ9fX0=";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/buy");
            }
        });

        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&6&lCoin Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Purchase ranks, keys, airdrops and more");
                toReturn.add("&7through the Coin Shop! Vote for us or buy");
                toReturn.add("&7coins on our store! https://store.cavepvp.org");
                toReturn.add("");
                toReturn.add("&aClick here to open the Coin Shop");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DOUBLE_PLANT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/coinshop");
            }
        });

        toReturn.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&5&lDiscord");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Join our Discord for announcements, events");
                toReturn.add("&7all updates and many giveaways!");
                toReturn.add("");
                toReturn.add("&aLink - https://discord.gg/cavepvp");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3M2MxMmJmZmI1MjUxYTBiODhkNWFlNzVjNzI0N2NiMzlhNzVmZjFhODFjYmU0YzhhMzliMzExZGRlZGEifX19";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/discord");
            }
        });

        toReturn.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lSettings");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Modify your experience through settings!");
                toReturn.add("");
                toReturn.add("&aClick here to modify settings");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U3ZDhjMjQyZDJlNGY4MDI4ZjkzMGJlNzZmMzUwMTRiMjFiNTI1NTIwOGIxYzA0MTgxYjI1NzQxMzFiNzVhIn19fQ==";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/settings");
            }
        });

        toReturn.put(28, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lAbility Items");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View all our ability items");
                toReturn.add("&7what they do and how to get them.");
                toReturn.add("");
                toReturn.add("&aClick here to view all ability items");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/abilityitems");
            }
        });

        toReturn.put(29, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lArmor Classes");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View all our armor classes and what they do.");
                toReturn.add("");
                toReturn.add("&aClick here to view all our Armor Classes.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_HELMET;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/classes");
            }
        });

        toReturn.put(30, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lCustom Enchants");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7Purchase custom enchants using XP!");
                toReturn.add("");
                toReturn.add("&aClick here to view all our Custom Enchants.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/ce");
            }
        });

        toReturn.put(31, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lOutpost");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View all our outposts, who captured it");
                toReturn.add("&7what the rewards are and where they are.");
                toReturn.add("");
                toReturn.add("&aClick here to view all Outposts.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/outposts");
            }
        });

        toReturn.put(32, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lKits");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View all the kits and their rewards!");
                toReturn.add("");
                toReturn.add("&aClick here to view all Kits.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ENDER_CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/kits");
            }
        });

        toReturn.put(33, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lXP Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View the XP Shop, the rewards");
                toReturn.add("&7the prices and what you can buy.");
                toReturn.add("");
                toReturn.add("&aClick here to view the XP Shop.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EXP_BOTTLE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/xpshop");
            }
        });

        toReturn.put(34, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lSkins");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View all skins, what they look");
                toReturn.add("&7like and how you can get them.");
                toReturn.add("");
                toReturn.add("&aClick here to view all skins.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SADDLE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage(ChatColor.RED + "Skins will be enabled soon!");
            }
        });

        toReturn.put(39, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lSchedule");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View everything that's supposed");
                toReturn.add("&7to happen during the map.");
                toReturn.add("");
                toReturn.add("&aClick here to view the schedule.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZlOGNmZjc1ZjdkNDMzMjYwYWYxZWNiMmY3NzNiNGJjMzgxZDk1MWRlNGUyZWI2NjE0MjM3NzlhNTkwZTcyYiJ9fX0=";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/schedule");
            }
        });

        toReturn.put(40, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lLeaderboards");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View the leaderboards for this map.");
                toReturn.add("");
                toReturn.add("&aClick here to view the leaderboards.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiYWYwYzU4OWE2YjU4MzUxMWQ4M2MyNjgyNDA4NDJkMzM2NDc3NGVjOWY1NjZkMWZkNGQzNDljZjQyZmIifX19";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/lb");
            }
        });

        toReturn.put(41, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&6&lBattle Pass");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("&7View the battle pass tiers, challenges");
                toReturn.add("&7and the rewards.");
                toReturn.add("");
                toReturn.add("&aClick here to view the Battle Pass.");

                return toReturn.stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public String getSkullTexture(Player player) {
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg5MDQyMDgyYmI3YTc2MThiNzg0ZWU3NjA1YTEzNGM1ODgzNGUyMWUzNzRjODg4OTM3MTYxMDU3ZjZjNyJ9fX0=";
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) 3;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/bp");
            }
        });

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 54;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }
}
