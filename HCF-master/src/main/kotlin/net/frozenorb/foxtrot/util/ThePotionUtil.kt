package net.frozenorb.foxtrot.util

import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.TimeUnit

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
object ThePotionUtil {

    @JvmStatic val DEBUFFS = arrayListOf(
            PotionEffectType.HARM,PotionEffectType.SLOW,PotionEffectType.HUNGER,
            PotionEffectType.POISON,PotionEffectType.WITHER,PotionEffectType.WEAKNESS,
            PotionEffectType.CONFUSION,PotionEffectType.BLINDNESS,PotionEffectType.SLOW_DIGGING
    )

    @JvmStatic val MAX_POTION_DURATION = TimeUnit.MINUTES.toMillis(8L)
    @JvmStatic val MAX_POTION_DURATION_TICK = TimeUnit.MINUTES.toSeconds(8L) * 20L

    private val names = hashMapOf<PotionEffectType,String>()
    private val colors = hashMapOf<PotionEffectType,ChatColor>()

    init {
        this.names[PotionEffectType.HEAL] = "Healing"
        this.names[PotionEffectType.HARM] = "Harming"
        this.names[PotionEffectType.SLOW] = "Slowness"
        this.names[PotionEffectType.JUMP] = "Jump Boost"
        this.names[PotionEffectType.SPEED] = "Speed"
        this.names[PotionEffectType.HUNGER] = "Hunger"
        this.names[PotionEffectType.POISON] = "Poison"
        this.names[PotionEffectType.WITHER] = "Wither"
        this.names[PotionEffectType.WEAKNESS] = "Weakness"
        this.names[PotionEffectType.CONFUSION] = "Nausea"
        this.names[PotionEffectType.BLINDNESS] = "Blindness"
        this.names[PotionEffectType.SATURATION] = "Saturation"
        this.names[PotionEffectType.ABSORPTION] = "Absorption"
        this.names[PotionEffectType.REGENERATION] = "Regeneration"
        this.names[PotionEffectType.FAST_DIGGING] = "Haste"
        this.names[PotionEffectType.SLOW_DIGGING] = "Mining Fatigue"
        this.names[PotionEffectType.NIGHT_VISION] = "Night Vision"
        this.names[PotionEffectType.INVISIBILITY] = "Invisibility"
        this.names[PotionEffectType.HEALTH_BOOST] = "Health Boost"
        this.names[PotionEffectType.INCREASE_DAMAGE] = "Strength"
        this.names[PotionEffectType.WATER_BREATHING] = "Water Breathing"
        this.names[PotionEffectType.FIRE_RESISTANCE] = "Fire Resistance"
        this.names[PotionEffectType.DAMAGE_RESISTANCE] = "Resistance"

        this.colors[PotionEffectType.HEAL] = ChatColor.RED
        this.colors[PotionEffectType.HARM] = ChatColor.DARK_RED
        this.colors[PotionEffectType.SLOW] = ChatColor.GRAY
        this.colors[PotionEffectType.JUMP] = ChatColor.GREEN
        this.colors[PotionEffectType.SPEED] = ChatColor.AQUA
        this.colors[PotionEffectType.HUNGER] = ChatColor.DARK_GREEN
        this.colors[PotionEffectType.POISON] = ChatColor.DARK_GREEN
        this.colors[PotionEffectType.WITHER] = ChatColor.DARK_PURPLE
        this.colors[PotionEffectType.WEAKNESS] = ChatColor.DARK_GRAY
        this.colors[PotionEffectType.CONFUSION] = ChatColor.DARK_PURPLE
        this.colors[PotionEffectType.BLINDNESS] = ChatColor.DARK_GRAY
        this.colors[PotionEffectType.SATURATION] = ChatColor.RED
        this.colors[PotionEffectType.ABSORPTION] = ChatColor.YELLOW
        this.colors[PotionEffectType.REGENERATION] = ChatColor.LIGHT_PURPLE
        this.colors[PotionEffectType.FAST_DIGGING] = ChatColor.BLUE
        this.colors[PotionEffectType.SLOW_DIGGING] = ChatColor.RED
        this.colors[PotionEffectType.NIGHT_VISION] = ChatColor.DARK_BLUE
        this.colors[PotionEffectType.INVISIBILITY] = ChatColor.GRAY
        this.colors[PotionEffectType.HEALTH_BOOST] = ChatColor.RED
        this.colors[PotionEffectType.INCREASE_DAMAGE] = ChatColor.RED
        this.colors[PotionEffectType.WATER_BREATHING] = ChatColor.BLUE
        this.colors[PotionEffectType.FIRE_RESISTANCE] = ChatColor.GOLD
        this.colors[PotionEffectType.DAMAGE_RESISTANCE] = ChatColor.GRAY
    }

    @JvmStatic
    fun getName(type: PotionEffectType):String {
        return names.getOrPut(type) { WordUtils.capitalizeFully(type.name.lowercase().replace(" ","")) }
    }

    @JvmStatic
    fun getColor(type: PotionEffectType):ChatColor {
        return this.colors[type] ?: ChatColor.RED
    }

    @JvmStatic
    fun getDisplayName(type: PotionEffectType):String {
        return "${this.colors[type] ?: ChatColor.WHITE}${getName(type)}"
    }

    @JvmStatic
    fun isDebuff(type: PotionEffectType):Boolean {
        return DEBUFFS.contains(type)
    }

}