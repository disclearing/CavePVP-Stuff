package cc.fyre.proton.util;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnchantUtil {

    public static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    public static final Map<String, Enchantment> ALIAS_ENCHANTMENTS = new HashMap<>();

    static {
        ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
        ALIAS_ENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
        ENCHANTMENTS.put("sharpness", Enchantment.DAMAGE_ALL);
        ALIAS_ENCHANTMENTS.put("sharp", Enchantment.DAMAGE_ALL);
        ALIAS_ENCHANTMENTS.put("dal", Enchantment.DAMAGE_ALL);

        ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
        ENCHANTMENTS.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
        ALIAS_ENCHANTMENTS.put("baneofarthropod", Enchantment.DAMAGE_ARTHROPODS);
        ALIAS_ENCHANTMENTS.put("arthropod", Enchantment.DAMAGE_ARTHROPODS);
        ALIAS_ENCHANTMENTS.put("dar", Enchantment.DAMAGE_ARTHROPODS);

        ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
        ENCHANTMENTS.put("smite", Enchantment.DAMAGE_UNDEAD);
        ALIAS_ENCHANTMENTS.put("du", Enchantment.DAMAGE_UNDEAD);

        ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
        ENCHANTMENTS.put("efficiency", Enchantment.DIG_SPEED);
        ALIAS_ENCHANTMENTS.put("minespeed", Enchantment.DIG_SPEED);
        ALIAS_ENCHANTMENTS.put("cutspeed", Enchantment.DIG_SPEED);
        ALIAS_ENCHANTMENTS.put("ds", Enchantment.DIG_SPEED);
        ALIAS_ENCHANTMENTS.put("eff", Enchantment.DIG_SPEED);

        ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
        ALIAS_ENCHANTMENTS.put("dura", Enchantment.DURABILITY);
        ENCHANTMENTS.put("unbreaking", Enchantment.DURABILITY);
        ALIAS_ENCHANTMENTS.put("d", Enchantment.DURABILITY);

        ENCHANTMENTS.put("thorns", Enchantment.THORNS);
        ENCHANTMENTS.put("highcrit", Enchantment.THORNS);
        ALIAS_ENCHANTMENTS.put("thorn", Enchantment.THORNS);
        ALIAS_ENCHANTMENTS.put("highercrit", Enchantment.THORNS);
        ALIAS_ENCHANTMENTS.put("t", Enchantment.THORNS);

        ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
        ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
        ALIAS_ENCHANTMENTS.put("meleefire", Enchantment.FIRE_ASPECT);
        ALIAS_ENCHANTMENTS.put("meleeflame", Enchantment.FIRE_ASPECT);
        ALIAS_ENCHANTMENTS.put("fa", Enchantment.FIRE_ASPECT);

        ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("kback", Enchantment.KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("kb", Enchantment.KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("k", Enchantment.KNOCKBACK);

        ALIAS_ENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
        ENCHANTMENTS.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        ALIAS_ENCHANTMENTS.put("fort", Enchantment.LOOT_BONUS_BLOCKS);
        ALIAS_ENCHANTMENTS.put("lbb", Enchantment.LOOT_BONUS_BLOCKS);

        ALIAS_ENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
        ENCHANTMENTS.put("mobloot", Enchantment.LOOT_BONUS_MOBS);
        ENCHANTMENTS.put("looting", Enchantment.LOOT_BONUS_MOBS);
        ALIAS_ENCHANTMENTS.put("lbm", Enchantment.LOOT_BONUS_MOBS);

        ALIAS_ENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
        ENCHANTMENTS.put("respiration", Enchantment.OXYGEN);
        ALIAS_ENCHANTMENTS.put("breathing", Enchantment.OXYGEN);
        ENCHANTMENTS.put("breath", Enchantment.OXYGEN);
        ALIAS_ENCHANTMENTS.put("o", Enchantment.OXYGEN);

        ENCHANTMENTS.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIAS_ENCHANTMENTS.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
        ENCHANTMENTS.put("protect", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIAS_ENCHANTMENTS.put("p", Enchantment.PROTECTION_ENVIRONMENTAL);

        ALIAS_ENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("explosionprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("bprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("bprotect", Enchantment.PROTECTION_EXPLOSIONS);
        ENCHANTMENTS.put("blastprotect", Enchantment.PROTECTION_EXPLOSIONS);
        ALIAS_ENCHANTMENTS.put("pe", Enchantment.PROTECTION_EXPLOSIONS);

        ALIAS_ENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
        ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
        ENCHANTMENTS.put("featherfall", Enchantment.PROTECTION_FALL);
        ALIAS_ENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
        ALIAS_ENCHANTMENTS.put("pfa", Enchantment.PROTECTION_FALL);

        ALIAS_ENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
        ALIAS_ENCHANTMENTS.put("flameprotection", Enchantment.PROTECTION_FIRE);
        ENCHANTMENTS.put("fireprotect", Enchantment.PROTECTION_FIRE);
        ALIAS_ENCHANTMENTS.put("flameprotect", Enchantment.PROTECTION_FIRE);
        ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
        ALIAS_ENCHANTMENTS.put("flameprot", Enchantment.PROTECTION_FIRE);
        ALIAS_ENCHANTMENTS.put("pf", Enchantment.PROTECTION_FIRE);

        ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
        ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
        ALIAS_ENCHANTMENTS.put("pp", Enchantment.PROTECTION_PROJECTILE);

        ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
        ALIAS_ENCHANTMENTS.put("softtouch", Enchantment.SILK_TOUCH);
        ALIAS_ENCHANTMENTS.put("st", Enchantment.SILK_TOUCH);

        ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
        ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
        ALIAS_ENCHANTMENTS.put("watermine", Enchantment.WATER_WORKER);
        ALIAS_ENCHANTMENTS.put("ww", Enchantment.WATER_WORKER);

        ALIAS_ENCHANTMENTS.put("firearrow", Enchantment.ARROW_FIRE);
        ENCHANTMENTS.put("flame", Enchantment.ARROW_FIRE);
        ENCHANTMENTS.put("flamearrow", Enchantment.ARROW_FIRE);
        ALIAS_ENCHANTMENTS.put("af", Enchantment.ARROW_FIRE);

        ENCHANTMENTS.put("arrowdamage", Enchantment.ARROW_DAMAGE);
        ENCHANTMENTS.put("power", Enchantment.ARROW_DAMAGE);
        ALIAS_ENCHANTMENTS.put("arrowpower", Enchantment.ARROW_DAMAGE);
        ALIAS_ENCHANTMENTS.put("ad", Enchantment.ARROW_DAMAGE);

        ENCHANTMENTS.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("arrowkb", Enchantment.ARROW_KNOCKBACK);
        ENCHANTMENTS.put("punch", Enchantment.ARROW_KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("arrowpunch", Enchantment.ARROW_KNOCKBACK);
        ALIAS_ENCHANTMENTS.put("ak", Enchantment.ARROW_KNOCKBACK);

        ALIAS_ENCHANTMENTS.put("infinitearrows", Enchantment.ARROW_INFINITE);
        ENCHANTMENTS.put("infarrows", Enchantment.ARROW_INFINITE);
        ENCHANTMENTS.put("infinity", Enchantment.ARROW_INFINITE);
        ALIAS_ENCHANTMENTS.put("infinite", Enchantment.ARROW_INFINITE);
        ALIAS_ENCHANTMENTS.put("unlimited", Enchantment.ARROW_INFINITE);
        ALIAS_ENCHANTMENTS.put("unlimitedarrows", Enchantment.ARROW_INFINITE);
        ALIAS_ENCHANTMENTS.put("ai", Enchantment.ARROW_INFINITE);

        ENCHANTMENTS.put("luck", Enchantment.LUCK);
        ALIAS_ENCHANTMENTS.put("luckofsea", Enchantment.LUCK);
        ALIAS_ENCHANTMENTS.put("luckofseas", Enchantment.LUCK);
        ALIAS_ENCHANTMENTS.put("rodluck", Enchantment.LUCK);

        ENCHANTMENTS.put("lure", Enchantment.LURE);
        ALIAS_ENCHANTMENTS.put("rodlure", Enchantment.LURE);
    }

    public static Enchantment getByName(String name) {

        Enchantment enchantment;

        if (isInt(name)) {
            enchantment = Enchantment.getById(Integer.parseInt(name));
        } else {
            enchantment = Enchantment.getByName(name.toUpperCase(Locale.ENGLISH));
        }

        if (enchantment == null) {
            enchantment = ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
        }

        if (enchantment == null) {
            enchantment = ALIAS_ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
        }

        return enchantment;
    }

    private static boolean isInt(String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}
