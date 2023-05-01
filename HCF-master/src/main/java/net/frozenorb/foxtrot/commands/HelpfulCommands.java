package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.armorclass.menu.ArmorClassesMenu;
import net.frozenorb.foxtrot.gameplay.armorclass.menu.ArmorMainMenu;
import net.frozenorb.foxtrot.gameplay.events.outposts.menu.OutpostMenu;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreGetter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class HelpfulCommands {
    @Command(names = {"basebuild", "buildcomp", "basebuildcompetition"}, permission = "")
    public static void baseBuild(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "Sign up for the base build competition through this form!");
        player.sendMessage(ChatColor.GRAY + "https://cavepvp.org/buildcomp");
        player.sendMessage("");
    }

    @Command(names = {"schedule"}, permission = "")
    public static void schedule(Player player) {

        final int date = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);

        new Menu() {
            @Override
            public String getTitle(Player player) {
                return "Schedule";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                for (int i = 0; i < 27; i++) {
                    toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, ""));
                }

                toReturn.put(10, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Saturday [SOTW]";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Saturday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f2:00 PM EST: &cSOTW (Server Opens)"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f2:30 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f3:00 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:30 PM EST: &cLootbox All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f5:15 PM EST: &cSOTW Timer Ends"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f6:15 PM EST: &5Enderdragon Event"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f7:30 PM EST: &6Lootbox-All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f8:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f9:00 PM EST: &cFirst KOTH"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f10:30 PM EST: &5Items Key-All"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fHourly Key-Alls &cafter 4:30 PM EST"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.SATURDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.SATURDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(11, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Sunday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Sunday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f2:00 PM EST: &c3x Airdrops/Perk/Mystery Box"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f2:00 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f2:30 PM EST: &cLootbox All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f3:00 PM EST: &4Rage Event"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &5Overworld Citadel"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.SUNDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.SUNDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(12, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Monday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Monday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f12:00 PM EST: &4Nether Citadel"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &cRaider Event"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.MONDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.MONDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(13, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Tuesday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Tuesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f12:00 PM EST: &dMayhem Event"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f5:00 PM EST: &4Nether Citadel"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.TUESDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.TUESDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(14, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Wednesday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Wednesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &bTraveller Event"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &cFaction Tournament (Practice)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.WEDNESDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.WEDNESDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(15, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Thursday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Wednesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f12:00 AM EST: &d2x Points Starts"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f1:00 PM EST: &5Overworld Citadel"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &4Rage Event"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f11:59 PM EST: &d2x Points Ends"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.THURSDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.THURSDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(16, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friday [EOTW]";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Friday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translate("&4&l┃ &f5:00 PM EST: &4&lEOTW starts (Map End)"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fWhen EOTW KOTH is over: &4&lCave Nite"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translate("&4&l┃ &fDiscount Drops every &c3 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.FRIDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.FRIDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                return toReturn;
            }
        }.openMenu(player);

    }

    @Command(names = {"discountdrop"}, permission = "")
    public static void discount(Player player) {

        final long difference = Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() - System.currentTimeMillis();

        if (Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() != 0 && difference > 0) {
            final Location location = Foxtrot.getInstance().getCouponDropsHandler().getLocation().clone();
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Discount Drop");
            player.sendMessage(ChatColor.GRAY + "A random store discount drops in " + ChatColor.WHITE + FoxtrotScoreGetter.getTimerScore(Foxtrot.getInstance().getCouponDropsHandler().getDropsIn()));
            player.sendMessage(ChatColor.RED + "Location: " + ChatColor.WHITE + location.getBlockX() + ", " + location.getBlockZ());
            player.sendMessage("");
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lDiscount Drop"));
        player.sendMessage(ChatColor.translate("&cA random store discount drops every 3 hours!"));
        player.sendMessage("");
    }

    @Command(names = {"classes", "class", "armorclasses", "armorclass"}, permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            new ArmorMainMenu().openMenu(player);
            return;
        }

        new ArmorClassesMenu(Category.ALL).openMenu(player);
    }

    @Command(names = {"outpost", "outposts"}, permission = "")
    public static void outpost(Player player) {
        new OutpostMenu().openMenu(player);
    }
}
