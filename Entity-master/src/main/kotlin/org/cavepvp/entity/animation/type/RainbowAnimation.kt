package org.cavepvp.entity.animation.type

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityPlugin
import org.cavepvp.entity.ai.ShardPacketAI
import org.cavepvp.entity.animation.EntityAnimation
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import org.cavepvp.entity.util.ItemPart
import org.cavepvp.entity.util.NMSUtil
import java.awt.Color

object RainbowAnimation : EntityAnimation {

    private var hue = 0.0F
    private var color = DyeColor.WHITE.color
    private val contents = arrayOfNulls<ItemStack>(4)

    private var packets = arrayOfNulls<Any>(4)

    init {
        Bukkit.getServer().scheduler.runTaskTimerAsynchronously(EntityPlugin.instance,{
            increment()
        },1L,1L)
        this.update()
    }

    private fun increment() {

        if (this.hue >= 1.0F) {
            this.hue = 0.0F
        } else {
            this.hue += 0.005F
        }

        Color.getHSBColor(this.hue,1F,1F).also{
            this.color = org.bukkit.Color.fromRGB(it.red,it.green,it.blue)  // TODO nigger WTF?
        }

        this.update()
    }

    private fun update() {
        this.contents[0] = this.create(Material.LEATHER_BOOTS)
        this.contents[1] = this.create(Material.LEATHER_LEGGINGS)
        this.contents[2] = this.create(Material.LEATHER_CHESTPLATE)
        this.contents[3] = this.create(Material.LEATHER_HELMET)
    }

    override fun getDisplayItem(): ItemStack {
        return this.create(Material.LEATHER_HELMET)
    }

    override fun getDisplayName(): String {
        return "${ChatColor.RED}R${ChatColor.GOLD}a${ChatColor.YELLOW}i${ChatColor.DARK_GREEN}n${ChatColor.BLUE}b${ChatColor.DARK_PURPLE}o${ChatColor.LIGHT_PURPLE}w"
    }

    override fun getName(): String {
        return "rainbow"
    }

    override fun isSupported(entity: Entity): Boolean {
        return entity is NPC
    }

    override fun onDisable(entity: Entity) {

        if (entity !is NPC) {
            return
        }

        this.packets[0] = ShardPacketAI.createEquipment(entity.id,ItemPart.BOOTS.ordinal,entity.getEquipment(ItemPart.BOOTS))
        this.packets[1] = ShardPacketAI.createEquipment(entity.id,ItemPart.LEGGINGS.ordinal,entity.getEquipment(ItemPart.LEGGINGS))
        this.packets[2] = ShardPacketAI.createEquipment(entity.id,ItemPart.CHESTPLATE.ordinal,entity.getEquipment(ItemPart.CHESTPLATE))
        this.packets[3] = ShardPacketAI.createEquipment(entity.id,ItemPart.HELMET.ordinal,entity.getEquipment(ItemPart.HELMET))
    }

    override fun onTick(entity: Entity) {

        if (entity !is NPC) {
            return
        }

        this.packets[0] = ShardPacketAI.createEquipment(entity.id,ItemPart.BOOTS.ordinal,this.contents[0])
        this.packets[1] = ShardPacketAI.createEquipment(entity.id,ItemPart.LEGGINGS.ordinal,this.contents[1])
        this.packets[2] = ShardPacketAI.createEquipment(entity.id,ItemPart.CHESTPLATE.ordinal,this.contents[2])
        this.packets[3] = ShardPacketAI.createEquipment(entity.id,ItemPart.HELMET.ordinal,this.contents[3])
    }

    override fun onTick(entity: Entity,player: Player) {
        this.packets.forEach{NMSUtil.sendPacket(player,it!!)}
    }

    private fun create(material: Material):ItemStack {
        return ItemBuilder.of(material)
            .name("${this.getDisplayName()} ${ChatColor.GRAY}Outfit")
            .color(this.color)
            .build()
    }

}