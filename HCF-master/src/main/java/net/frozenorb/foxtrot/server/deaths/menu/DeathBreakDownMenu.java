package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.serialization.LocationSerializer;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DateFormat;
import java.util.*;

@AllArgsConstructor
public class DeathBreakDownMenu extends Menu {
    private BasicDBObject object;
    private OfflinePlayer target;
    private DateFormat FORMAT;

    @Override
    public String getTitle(Player player) {
        return "Death Breakdown";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        toReturn.put(10, new Button() {
            final String victimName = UUIDUtils.name(UUIDfromString(object.getString("uuid")));

            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Victim Information";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.translate("&4&l┃ &fVictim: &c" + victimName));

                int ping = object.getInt("ping");

                toReturn.add(ChatColor.translate("&4&l┃ &fConnection: &c" + getPingStatus(ping) + " (" + ping + " ms)"));
                toReturn.add(ChatColor.translate("&4&l┃ &fFaction upon Death: &c" + object.getString("playerTeam")));

                final Location location = LocationSerializer.deserialize((BasicDBObject) object.get("playerLocation"));

                toReturn.add(ChatColor.translate("&4&l┃ &fLocation upon Death: &c" + location.getBlockX() + ", " + (location.getBlockY()+1) + ", " + location.getBlockZ()));

                if (!object.getString("playerTeam").startsWith("None Found")) {
                    toReturn.add(ChatColor.translate("&4&l┃ &fBefore DTR: &f" + object.getString("beforeDTR")));
                    toReturn.add(ChatColor.translate("&4&l┃ &fAfter DTR: &f" + formatDTR(object.getDouble("afterDTR"))));
                }

                if (object.containsKey("lastUsedLong") && object.containsKey("lastUsedAbility")) {
                    long systemTime = object.getLong("systemTime");
                    long lastUsedAbility = object.getLong("lastUsedLong");
                    long difference = systemTime - lastUsedAbility;

                    toReturn.add(ChatColor.translate("&4&l┃ &fLast Used Ability: &f" + object.getString("lastUsedAbility") + " (" + TimeUtils.formatIntoDetailedString((int) difference/1000) + " ago)"));
                }

                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click to view the victim's inventory upon death");

                return toReturn;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                final ItemStack itemStack = super.getButtonItem(player);
                final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwner(victimName);
                itemStack.setItemMeta(skullMeta);

                return itemStack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RestoreInventoryMenu(object, target, false).openMenu(player);
            }
        });

        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Generic Death Information";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                final double tps = object.getDouble("tps");

                toReturn.add(ChatColor.translate("&4&l┃ &fDate: &c" + object.getDate("when").toLocaleString()));
                toReturn.add(ChatColor.translate("&4&l┃ &fServer Lag: &c" + getServerLag(tps) + " (" + tps + " TPS)"));
                toReturn.add(ChatColor.translate("&4&l┃ &fDeath Cause: &c" + object.getString("reason")));

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }
        });

        toReturn.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Killer Information";
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                if (!object.containsKey("killerUUID")) {
                    toReturn.add(ChatColor.YELLOW + "No killer found for this death");
                    toReturn.add(ChatColor.GRAY + "Player most likely died to natural causes (rip bozo)");

                    return toReturn;
                }

                toReturn.add(ChatColor.translate("&4&l┃ &fKiller: &c" + UUIDUtils.name(UUIDfromString(object.getString("killerUUID")))));

                int ping = object.getInt("killerPing");

                toReturn.add(ChatColor.translate("&4&l┃ &fConnection: &f" + getPingStatus(ping) + " (" + ping + " ms)"));
                toReturn.add(ChatColor.translate("&4&l┃ &fHearts Left: &c" + (object.getInt("healthLeft")/2) + " hearts"));
                toReturn.add(ChatColor.translate("&4&l┃ &fFaction upon Kill: &c" + object.getString("killerTeam")));
                toReturn.add(ChatColor.translate("&4&l┃ &fFaction DTR upon Kill: &c" + object.getString("killerDTR")));

                final Location location = LocationSerializer.deserialize((BasicDBObject) object.get("killerLocation"));

                toReturn.add(ChatColor.translate("&4&l┃ &fLocation upon Kill: &c" + location.getBlockX() + ", " + location.getBlockY()+1 + ", " + location.getBlockZ()));

                if (object.containsKey("lastUsedLong") && object.containsKey("killerLastUsedAbility")) {
                    long systemTime = object.getLong("systemTime");
                    long lastUsedAbility = object.getLong("killerLastUsedLong");
                    long difference = systemTime - lastUsedAbility;

                    toReturn.add(ChatColor.translate("&4&l┃ &fLast Used Ability: &c" + object.getString("killerLastUsedAbility") + " (" + TimeUtils.formatIntoDetailedString((int) difference/1000) + " ago)"));
                }

                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click to view the killer's inventory upon death");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                final ItemStack itemStack = super.getButtonItem(player);

                if (!object.containsKey("killerUUID")) {
                    return itemStack;
                }

                final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwner(UUIDUtils.name(UUIDfromString(object.getString("killerUUID"))));
                itemStack.setItemMeta(skullMeta);

                return itemStack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RestoreInventoryMenu(object, target, true).openMenu(player);
            }
        });

        return toReturn;
    }

    public String getServerLag(double tps) {

        if (tps >= 19) {
            return ChatColor.DARK_GREEN + "None";
        }

        if (tps >= 18) {
            return ChatColor.YELLOW + "Slight";
        }

        if (tps >= 15) {
            return ChatColor.GOLD + "Moderate (Revivable)";
        }

        if (tps >= 10) {
            return ChatColor.RED + "Bad (Revivable)";
        }

        return ChatColor.DARK_RED + "Very Bad (Revivable)";
    }

    public String getPingStatus(int ping) {
        if (ping <= 30) {
            return ChatColor.DARK_GREEN + "Very Good";
        }

        if (ping <= 60) {
            return ChatColor.GREEN + "Good";
        }

        if (ping <= 150) {
            return ChatColor.GOLD + "Moderate";
        }

        if (ping <= 200) {
            return ChatColor.RED + "Bad";
        }

        return ChatColor.DARK_RED + "Very Bad";
    }

    public String formatDTR(double DTR) {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / 5.0 <= 0.25) {
            if (DTR <= 0) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                dtrColor = ChatColor.YELLOW;
            }
        }

        return dtrColor + "" + DTR;
    }

    private static UUID UUIDfromString(String string) {
        return UUID.fromString(
                string.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }
}
