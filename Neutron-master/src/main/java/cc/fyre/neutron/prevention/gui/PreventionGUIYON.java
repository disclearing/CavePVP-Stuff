package cc.fyre.neutron.prevention.gui;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.neutron.prevention.packets.PreventionResolvePacket;
import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreventionGUIYON extends Menu {
    private Prevention prevention;
    public PreventionGUIYON(Prevention prevention) {
        this.prevention = prevention;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Are you sure";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = Maps.newHashMap();
        for(int i =0; i<9; i++) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player var1) {
                    return "";
                }

                @Override
                public List<String> getDescription(Player var1) {
                    return new ArrayList<>();
                }

                @Override
                public Material getMaterial(Player var1) {
                    return Material.STAINED_GLASS_PANE;
                }
                @Override
                public byte getDamageValue(Player player) {
                    return 15;
                }
            });
        }
        toReturn.put(3, new Button() {
            @Override
            public String getName(Player var1) {
                return ChatColor.GREEN + "Yes";
            }

            @Override
            public List<String> getDescription(Player var1) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player var1) {
                return Material.WOOL;
            }
            @Override
            public byte getDamageValue(Player player) {
                return 5;
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {

                new PreventionGUI().openMenu(player);
                Bukkit.getScheduler().runTaskAsynchronously(Neutron.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Proton.getInstance().getPidginHandler().sendPacket(new PreventionResolvePacket(prevention.getTime()));
                    }
                });
            }
        });
        toReturn.put(5, new Button() {
            @Override
            public String getName(Player var1) {
                return ChatColor.RED + "No";
            }

            @Override
            public List<String> getDescription(Player var1) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player var1) {
                return Material.WOOL;
            }
            @Override
            public byte getDamageValue(Player player) {
                return 14;
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new PreventionGUI().openMenu(player);
            }
        });

        return toReturn;
    }
}
