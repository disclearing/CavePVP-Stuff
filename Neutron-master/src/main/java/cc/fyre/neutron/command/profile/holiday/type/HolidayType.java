package cc.fyre.neutron.command.profile.holiday.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@AllArgsConstructor
public enum HolidayType {
    NEW_YEARS(ChatColor.translateAlternateColorCodes('&',"&e&l2022 &f&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&f&k:&e&l2022&f&k:&8]&e"), ChatColor.YELLOW, ChatColor.YELLOW, Material.FIREWORK, 10),
    VALENTINES(ChatColor.translateAlternateColorCodes('&',"&4&lValentines Themed"), ChatColor.translateAlternateColorCodes('&',"&8[&f&k:&c&lCUPID&f&k:&8]&c"), ChatColor.RED, ChatColor.RED, Material.BOW, 11),
    ST_PATRICKS_DAY(ChatColor.translateAlternateColorCodes('&',"&2&lSt. Patrick's Day Themed"), ChatColor.translateAlternateColorCodes('&',"&8[&2&k:&a&lLEPRECHAUN&2&k:&8]&a"), ChatColor.GREEN, ChatColor.GREEN, Material.GREEN_RECORD, 12),
    EASTER(ChatColor.translateAlternateColorCodes('&',"&d&lEaster Themed"), ChatColor.translateAlternateColorCodes('&',"&8[&f&k:&d&lBUNNY&f&k:&8]&d"), ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE, Material.EGG, 13),
    EARTH_DAY(ChatColor.translateAlternateColorCodes('&',"&b&lEarth &a&lDay &b&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&b&k:&a&lEARTH&b&k:&8]&b"), ChatColor.AQUA, ChatColor.GREEN, Material.GRASS, 14),
    SUMMER(ChatColor.translateAlternateColorCodes('&',"&6&lSummer &e&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&e&k:&6&lSUMMER&e&k:&8]&6"), ChatColor.GOLD, ChatColor.YELLOW, Material.DOUBLE_PLANT, 15),
    FALL(ChatColor.translateAlternateColorCodes('&',"&6&lFall &e&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&e&k:&6&lFALL&e&k:&8]&6"), ChatColor.GOLD, ChatColor.YELLOW, Material.PUMPKIN, 16),
    HALLOWEEN(ChatColor.translateAlternateColorCodes('&',"&6&lHalloween Themed"), ChatColor.translateAlternateColorCodes('&',"&8[&d&k:&5&lSPOOKY&d&k:&8]&5"), ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, Material.JACK_O_LANTERN, 19),
    THANKS_GIVING(ChatColor.translateAlternateColorCodes('&',"&e&lThanks Giving Themed"), ChatColor.translateAlternateColorCodes('&',"&8[&6&k:&e&lTURKEY&6&k:&8]&e"), ChatColor.YELLOW, ChatColor.YELLOW, Material.COOKED_CHICKEN, 20),
    WINTER(ChatColor.translateAlternateColorCodes('&',"&b&lWinter &f&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&f&k:&b&lSNOWMAN&f&k:&8]&b"), ChatColor.AQUA, ChatColor.AQUA, Material.SNOW_BLOCK, 21),
    CHRISTMAS(ChatColor.translateAlternateColorCodes('&',"&c&lChristmas &2&lThemed"), ChatColor.translateAlternateColorCodes('&',"&8[&f&k:&c&lSANTA&f&k:&8]&c"), ChatColor.RED, ChatColor.RED, Material.SNOW_BALL, 22);

    @Getter final String displayName;
    @Getter final String prefix;
    @Getter final ChatColor displayColor;
    @Getter final ChatColor chatColor;
    @Getter final Material material;
    @Getter final int slot;
}
