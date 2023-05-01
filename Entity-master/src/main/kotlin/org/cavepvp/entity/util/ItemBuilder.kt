package org.cavepvp.entity.util

import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

class ItemBuilder(private val item: ItemStack) {

    private constructor(material: Material, amount: Int):this(ItemStack(material,if (amount < 1) 1 else amount))

    fun name(displayName: String): ItemBuilder {
        this.editMeta{
            it.displayName = ChatColor.translateAlternateColorCodes('&',displayName)
        }
        return this
    }

    fun data(data: Int): ItemBuilder {
        return this.data(data.toShort())
    }

    fun data(data: Byte): ItemBuilder {
        return this.data(data.toShort())
    }

    fun data(data: Short): ItemBuilder {
        this.item.durability = data
        return this
    }

    fun lore(vararg lore: String,translate: Boolean = true): ItemBuilder {
        return this.lore(lore.toList(),translate)
    }

    fun lore(lore: Collection<String>,translate: Boolean = true): ItemBuilder {
        this.editMeta{
            it.lore = if (translate) lore.map{text -> ChatColor.translateAlternateColorCodes('&',text)}.toList() else lore.toList()
        }
        return this
    }

    fun addToLore(lore: ArrayList<String>,translate: Boolean = true): ItemBuilder {
        this.editMeta{
            it.lore = (it.lore ?: arrayListOf())
                .plus(if (translate) lore.map{text -> ChatColor.translateAlternateColorCodes('&',text)}.toList() else lore.toList())
        }
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        this.item.amount = amount
        return this
    }

    fun enchant(enchantment: Enchantment,level: Int): ItemBuilder {
        this.item.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun removeEnchant(enchantment: Enchantment): ItemBuilder {
        this.item.removeEnchantment(enchantment)
        return this
    }

    fun color(color: Color?): ItemBuilder {
        this.editMeta{

            if (it !is LeatherArmorMeta) {
                throw UnsupportedOperationException("Cannot set color of a non-leather armor item.")
            }

            it.color = color
        }

        return this
    }

    fun unbreakable(unbreakable: Boolean): ItemBuilder {
        this.editMeta{it.spigot().isUnbreakable = unbreakable}
        return this
    }

    fun owner(owner: String): ItemBuilder {
        this.editMeta{

            if (it !is SkullMeta) {
                throw UnsupportedOperationException("Cannot set owner of a non-skull item.")
            }

            it.owner = owner
        }
        return this
    }

    fun setFireworkColor(color: Color): ItemBuilder {
        this.editMeta{

            if (it !is FireworkEffectMeta) {
                throw UnsupportedOperationException("Cannot set color of a non-firework item.")
            }

            it.effect = FireworkEffect.builder().withColor(color).build()
        }
        return this
    }

    private fun editMeta(meta: (ItemMeta) -> Unit) {

        var itemMeta = this.item.itemMeta

        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(Material.getMaterial(this.item.typeId))
        }

        meta.invoke(itemMeta)

        this.item.itemMeta = itemMeta
    }

    fun build(): ItemStack {
        return this.item.clone()
    }

    companion object {

        fun of(material: Material): ItemBuilder {
            return ItemBuilder(material,1)
        }

        fun of(material: Material,amount: Int): ItemBuilder {
            return ItemBuilder(material,amount)
        }

        fun copyOf(builder: ItemBuilder): ItemBuilder {
            return ItemBuilder(builder.build())
        }

        fun copyOf(item: ItemStack): ItemBuilder {
            return ItemBuilder(item)
        }

    }

}