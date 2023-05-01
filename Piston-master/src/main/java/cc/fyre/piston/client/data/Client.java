package cc.fyre.piston.client.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor @Getter
public enum Client {
    VANILLA("Vanilla", null, null,ChatColor.WHITE + "Vanilla", ChatColor.WHITE + "[V]", ""),
    OC_MC("OCMC-Client", "OCMC",null, ChatColor.GREEN + "OCMC", ChatColor.GREEN + "[OCMC]", ""),
    FORGE("Forge", "FML", null,ChatColor.YELLOW + "Forge", ChatColor.GOLD + "[F]", ""),
    CAVECLIENT("Cave Client", null, "Cave Client", ChatColor.RED + "Cave Client", ChatColor.RED + "[Cave]", ""),


    LUNAR_CLIENT("Lunar-Client", "Lunar-Client",null, ChatColor.AQUA + "Lunar Client", ChatColor.AQUA + "[LC]", "Lunar-Client"),
    CHEAT_BREAKER("Cheat-Breaker", "CB-Client", null,ChatColor.RED + "Cheat" + ChatColor.WHITE + "Breaker", ChatColor.RED + "[C" + ChatColor.WHITE + "B]", "CB-Client"),
    COSMIC_CLIENT("Cosmic-Client", "CC", null,ChatColor.LIGHT_PURPLE + "Cosmic Client", ChatColor.LIGHT_PURPLE + "[CC]", ""),
    LABYMOD("LabyMod", null, null,ChatColor.BLUE + "Labyod", ChatColor.BLUE + "[LabyMod]", "LABYMOD"),
    LABYMODV3("LabyMod", null, null,ChatColor.BLUE + "LabyMod", ChatColor.BLUE + "[LabyMod]", "LMC"),
    LiteLoader("LiteLoader", null, null,ChatColor.BLUE + "LiteLoader", ChatColor.BLUE + "[LiteLoader]", ""),



   // From Carnage
    HACKED_CLIENT_A("Hacked-Client (Type A)","LOLIMAHCKER",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type A)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_B("Hacked-Client (Type B)","cock",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type B)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_C("Hacked-Client (Type C)","customGuiOpenBspkrs",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type C)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_C2("Hacked-Client (Type C2)","0SO1Lk2KASxzsd",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type D)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_C3("Hacked-Client (Type C3)","mincraftpvphcker",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type E)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_D("Hacked-Client (Type D)","lmaohax",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type F)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_E("Hacked-Client (Type E)","MCnetHandler",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type G)",ChatColor.DARK_RED + "[HA]",""),
    HACKED_CLIENT_M("Hacked-Client (Type M)","L0LIMAHCKER",null,ChatColor.DARK_RED.toString() + "Hacked-Client (Type H)",ChatColor.DARK_RED + "[HA]","");


    String name;
    String payload;
    String brand;
    String displayName;
    String symbol;
    String channel;

    public boolean usesLunarPackets() {
        return this == CHEAT_BREAKER || this == LUNAR_CLIENT;
    }
}
