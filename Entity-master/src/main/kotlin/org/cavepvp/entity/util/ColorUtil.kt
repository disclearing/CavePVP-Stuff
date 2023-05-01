package cc.fyre.shard.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor
import org.bukkit.DyeColor

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
object ColorUtil {

    private val colorToDyeColor = mutableMapOf(
        ChatColor.BLACK to DyeColor.BLACK,
        ChatColor.DARK_BLUE to DyeColor.BLUE,
        ChatColor.DARK_GREEN to DyeColor.GREEN,
        ChatColor.DARK_AQUA to DyeColor.CYAN,
        ChatColor.DARK_RED to DyeColor.RED,
        ChatColor.DARK_PURPLE to DyeColor.PURPLE,
        ChatColor.GOLD to DyeColor.ORANGE,
        ChatColor.GRAY to DyeColor.SILVER,
        ChatColor.DARK_GRAY to DyeColor.GRAY,
        ChatColor.BLUE to DyeColor.BLUE,
        ChatColor.GREEN to DyeColor.LIME,
        ChatColor.AQUA to DyeColor.LIGHT_BLUE,
        ChatColor.RED to DyeColor.RED,
        ChatColor.LIGHT_PURPLE to DyeColor.MAGENTA,
        ChatColor.YELLOW to DyeColor.YELLOW,
    )
    private val dyeColorToColor = mutableMapOf<DyeColor,ChatColor>()
    private val colorToDisplayName = mutableMapOf<ChatColor,String>()

    init {

        for (color in ChatColor.values()) {

            if (this.colorToDyeColor.containsKey(color)) {
                this.dyeColorToColor[this.colorToDyeColor[color]!!] = color
            }

            this.colorToDisplayName[color] = WordUtils.capitalizeFully(color.name.toLowerCase().replace("_"," "))
        }

    }

    @JvmStatic
    fun getName(color: ChatColor):String {
        return colorToDisplayName[color]!!
    }

    @JvmStatic
    fun getName(color: DyeColor):String {
        return getName(dyeColorToColor[color] ?: ChatColor.WHITE)
    }
    
    @JvmStatic
    fun getDyeColor(color: ChatColor):DyeColor {
        return colorToDyeColor[color] ?: DyeColor.WHITE
    }

    @JvmStatic
    fun getWoolColor(color: ChatColor):Byte {
        return (colorToDyeColor[color] ?: DyeColor.WHITE).woolData
    }

    @JvmStatic
    fun getChatColorByChar(char: Char):ChatColor {
        return ChatColor.getByChar(char) ?: ChatColor.WHITE
    }

    @JvmStatic
    fun convertColorValue(value: Double): Double {

        var toReturn = value

        if (toReturn <= 0.0) {
            toReturn = -1.0
        }

        return toReturn / 255.0
    }

    @JvmStatic
    fun getChatColorByIndex(index: Int):ChatColor {
        return ChatColor.values()[index]
    }

}