package cc.fyre.proton.tab.construct;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.util.TabUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class TabLayout {


    private static final AtomicReference<Object> TAB_LAYOUT_1_8 = new AtomicReference<>();
    private static final AtomicReference<Object> TAB_LAYOUT_DEFAULT = new AtomicReference<>();
    private static final String[] ZERO_VALUE_STRING = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private static final String[] ZERO_VALUE_STRING_18 = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    @Getter private static final Map<String, TabLayout> tabLayouts = new HashMap<>();
    protected static int WIDTH = 3;
    protected static int HEIGHT = 20;
    protected static final String EMPTY_TAB_HEADERFOOTER = "{\"translate\":\"\"}";
    private static List<String> emptyStrings = new ArrayList<>();
    @Getter private String[] tabNames;
    @Getter private int[] tabPings;
    @Getter private boolean is18;
    @Getter private String header;
    @Getter private String footer;

    private TabLayout(boolean is18) {
        this(is18,false);
    }

    private TabLayout(boolean is18, boolean fill) {
        this.header = "{\"translate\":\"\"}";
        this.footer = "{\"translate\":\"\"}";
        this.is18 = is18;
        this.tabNames = is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone();
        this.tabPings = is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
        if (fill) {

            for(int i = 0; i < this.tabNames.length; ++i) {
                this.tabNames[i] = genEmpty();
                this.tabPings[i] = 0;
            }

        }

        Arrays.sort(this.tabNames);
    }

    public void set(int x, int y, String name, int ping) {

        if (this.validate(x, y, true)) {
            int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
            this.tabNames[pos] = ChatColor.translateAlternateColorCodes('&', name);
            this.tabPings[pos] = ping;
        }

    }

    public void set(int x, int y, String name) {

        int ping = 0;

        if (Proton.getInstance().getServer().getPluginManager().getPlugin("LunarClientAPI") != null) {
            ping = -1;
        }

        this.set(x,y,name,ping);
    }

    public void set(int x, int y, Player player) {
        this.set(x, y, player.getName(), ((CraftPlayer)player).getHandle().ping);
    }

    public String getStringAt(int x, int y) {
        this.validate(x, y);
        int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
        return this.tabNames[pos];
    }

    public int getPingAt(int x, int y) {
        this.validate(x, y);
        int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
        return this.tabPings[pos];
    }

    public boolean validate(int x, int y, boolean silent) {
        if (x >= WIDTH) {
            if (!silent) {
                throw new IllegalArgumentException("x >= WIDTH (" + WIDTH + ")");
            } else {
                return false;
            }
        } else if (y >= HEIGHT) {
            if (!silent) {
                throw new IllegalArgumentException("y >= HEIGHT (" + HEIGHT + ")");
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean validate(int x, int y) {
        return this.validate(x, y, false);
    }

    private static String genEmpty() {
        String colorChars = "abcdefghijpqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < 8; ++i) {
            builder.append('§').append(colorChars.charAt(ThreadLocalRandom.current().nextInt(colorChars.length())));
        }

        String s = builder.toString();
        if (emptyStrings.contains(s)) {
            return genEmpty();
        } else {
            emptyStrings.add(s);
            return s;
        }
    }

    public void setHeader(String header) {
        this.header = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', header)));
    }

    public void setFooter(String footer) {
        this.footer = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', footer)));
    }

    public void reset() {
        this.tabNames = this.is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone();
        this.tabPings = this.is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
    }

    public static TabLayout create(Player player) {

        if (tabLayouts.containsKey(player.getName())) {

            final TabLayout layout = tabLayouts.get(player.getName());

            layout.reset();

            return layout;
        } else {
//            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)player).getHandle()));
            tabLayouts.put(player.getName(), new TabLayout(TabUtils.is18(player)));
            return tabLayouts.get(player.getName());
        }

    }

    public static void remove(Player player) {
        tabLayouts.remove(player.getName());
    }

    public static TabLayout createEmpty(Player player) {
        return TabUtils.is18(player) ? getTAB_LAYOUT_1_8() : getTAB_LAYOUT_DEFAULT();
    }

    public static TabLayout getTAB_LAYOUT_1_8() {

        Object value = TAB_LAYOUT_1_8.get();

        if (value == null) {

            synchronized(TAB_LAYOUT_1_8) {

                value = TAB_LAYOUT_1_8.get();

                if (value == null) {

                    final TabLayout actualValue = new TabLayout(true, true);

                    value = actualValue == null ? TAB_LAYOUT_1_8 : actualValue;

                    TAB_LAYOUT_1_8.set(value);
                }
            }
        }

        return ((TabLayout)(value == TAB_LAYOUT_1_8 ? null : value));
    }

    public static TabLayout getTAB_LAYOUT_DEFAULT() {
        Object value = TAB_LAYOUT_DEFAULT.get();

        if (value == null) {

            synchronized(TAB_LAYOUT_DEFAULT) {

                value = TAB_LAYOUT_DEFAULT.get();

                if (value == null) {

                    final TabLayout actualValue = new TabLayout(false, true);

                    value = actualValue == null ? TAB_LAYOUT_DEFAULT : actualValue;

                    TAB_LAYOUT_DEFAULT.set(value);
                }
            }
        }

        return ((TabLayout)(value == TAB_LAYOUT_DEFAULT ? null : value));
    }

}
