package net.frozenorb.foxtrot.brewer

import org.bukkit.Material
import org.bukkit.potion.PotionEffectType

enum class FancyBrewerResource(
    private val slot: Int,
    private val index: Int,
    private val required: Boolean,
    private vararg val types: Material,
) : Comparator<FancyBrewerResource> {

    WART(10,0,true,Material.NETHER_STALK),
    TYPE(19,1,true,
        Material.SUGAR,
        Material.SPIDER_EYE,
        Material.GHAST_TEAR,
        Material.MAGMA_CREAM,
        Material.BLAZE_POWDER,
        Material.GOLDEN_CARROT,
        Material.SPECKLED_MELON,
        Material.FERMENTED_SPIDER_EYE,
        Material.RAW_FISH
    ),
    INCREASE(28,2,false,Material.GLOWSTONE_DUST,Material.REDSTONE,Material.FERMENTED_SPIDER_EYE),
    SPLASH(37,3,false,Material.SULPHUR);

    fun next():FancyBrewerResource {

        val values = getAllResources()

        if (this.ordinal >= values.lastIndex) {
            return values[0]
        }

        return values[this.ordinal + 1]
    }

    fun getSlot():Int {
        return this.slot
    }

    fun getIndex():Int {
        return this.index
    }

    fun isRequired():Boolean {
        return this.required
    }

    fun getAllTypes():Array<out Material> {
        return this.types
    }

    override fun compare(role: FancyBrewerResource,other: FancyBrewerResource): Int {

        if (role.ordinal >= other.ordinal) {
            return 1
        }

        return 0
    }

    companion object {

        private val values = values().associateBy{it.slot}
        private val valuesByType = mutableMapOf<Material,FancyBrewerResource>().apply {

            for (value in values()) {

                for (type in value.getAllTypes()) {
                    this[type] = value
                }

            }

        }

        fun getAllSlots():Set<Int> {
            return this.values.keys
        }

        fun getAllResources():List<FancyBrewerResource> {
            return this.values.values.toList()
        }

        fun getResourceBySlot(slot: Int):FancyBrewerResource? {
            return this.values[slot]
        }

        fun getResourceByType(type: Material):FancyBrewerResource? {
            return this.valuesByType[type]
        }

        val RESULT_TABLE = hashMapOf(
            Material.SUGAR to PotionEffectType.SPEED,
            Material.SPIDER_EYE to PotionEffectType.POISON,
            Material.GHAST_TEAR to PotionEffectType.REGENERATION,
            Material.MAGMA_CREAM to PotionEffectType.FIRE_RESISTANCE,
            Material.BLAZE_POWDER to PotionEffectType.INCREASE_DAMAGE,
            Material.GOLDEN_CARROT to PotionEffectType.NIGHT_VISION,
            Material.SPECKLED_MELON to PotionEffectType.HEAL,
            Material.FERMENTED_SPIDER_EYE to PotionEffectType.WEAKNESS,
            Material.RAW_FISH to PotionEffectType.WATER_BREATHING,
        )

    }
}