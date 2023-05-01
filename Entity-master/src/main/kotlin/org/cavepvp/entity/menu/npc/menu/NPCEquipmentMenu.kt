package org.cavepvp.entity.menu.npc.menu

import cc.fyre.proton.menu.Menu
import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.buttons.BackButton
import cc.fyre.proton.menu.pagination.PaginatedMenu
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.material.*
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import org.cavepvp.entity.menu.npc.NPCMenu
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import org.cavepvp.entity.util.ItemPart
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NPCEquipmentMenu(private val npc: NPC) : Menu() {

    override fun isPlaceholder(): Boolean {
        return true;
    }

    override fun size(player: Player?): Int {
        return 3*9;
    }

    override fun getTitle(player: Player): String {
        return "Equipment"
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {

        val buttons = mutableMapOf<Int,Button>()

        buttons[10] = EquipmentButton(this.npc, ItemPart.HAND)
        buttons[11] = EquipmentButton(this.npc,ItemPart.HELMET)
        buttons[12] = EquipmentButton(this.npc,ItemPart.CHESTPLATE)
        buttons[13] = EquipmentButton(this.npc,ItemPart.LEGGINGS)
        buttons[14] = EquipmentButton(this.npc,ItemPart.BOOTS)

        buttons[16] = BackButton(NPCMenu(this.npc))

        return buttons
    }

    inner class EquipmentButton(private val npc: NPC,private val part: ItemPart) : Button() {
        override fun getName(p0: Player?): String {
            return ""
        }

        override fun getDescription(p0: Player?): MutableList<String> {
            return Collections.emptyList()
        }

        override fun getMaterial(p0: Player?): Material {
            return Material.AIR
        }

        private var item = this.npc.getEquipment(this.part)

        override fun getButtonItem(player: Player): ItemStack {

            if (this.item == null) {
                return ItemBuilder.of(Material.EMPTY_MAP)
                    .name(this.part.name)
                    .build()
            }

            return ItemBuilder.copyOf(this.item!!.clone())
                .build()
        }

        override fun clicked(player: Player?, slot: Int, clickType: ClickType) {

            if (clickType.isRightClick) {
                npc.setEquipment(this.part,null)
                return
            }

            object : PaginatedMenu() {

                override fun getPrePaginatedTitle(player: Player): String {
                    return this@EquipmentButton.part.name
                }

                override fun getMaxItemsPerPage(player: Player): Int {
                    return (5*9)
                }

                override fun getGlobalButtons(player: Player): MutableMap<Int, Button> {
                    return mutableMapOf(
                        4 to BackButton(NPCEquipmentMenu(npc))
                    )
                }

                override fun getAllPagesButtons(player: Player): HashMap<Int,Button> {
                    return (cached[part] ?: arrayListOf()).withIndex().associate{it.index to object : Button() {
                        override fun getName(p0: Player?): String {
                            return ""
                        }

                        override fun getDescription(p0: Player?): MutableList<String> {
                            return Collections.emptyList()
                        }

                        override fun getMaterial(p0: Player?): Material {
                            return Material.AIR
                        }

                        override fun getButtonItem(player: Player): ItemStack {
                            return it.value
                        }

                        override fun clicked(player: Player?, slot: Int, clickType: ClickType?) {
                            npc.setEquipment(part,it.value)
                            NPCEquipmentMenu(npc).openMenu(player)
                        }

                    } }.toMap(HashMap())
                }

            }.openMenu(player)

        }

    }

    companion object {

        private val cached = hashMapOf<ItemPart,ArrayList<ItemStack>>()
        private val materialBlacklist = arrayListOf(
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA,
            Material.DOUBLE_STEP,
            Material.FIRE,
            Material.BURNING_FURNACE,
            Material.SIGN_POST,
            Material.WALL_SIGN,
            Material.WOODEN_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.GLOWING_REDSTONE_ORE,
            Material.REDSTONE_TORCH_OFF,
            Material.SUGAR_CANE_BLOCK,
            Material.PORTAL,
            Material.JACK_O_LANTERN,
            Material.CAKE_BLOCK,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.NETHER_WARTS,
            Material.BREWING_STAND,
            Material.CAULDRON,
            Material.ENDER_PORTAL,
            Material.REDSTONE_LAMP_ON,
            Material.WOOD_DOUBLE_STEP,
            Material.COCOA,
            Material.TRIPWIRE,
            Material.FLOWER_POT,
            Material.CARROT,
            Material.POTATO,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.PISTON_MOVING_PIECE,
            Material.SKULL
        )

        init {
            ItemPart.values().forEach{

                val list = arrayListOf<ItemStack>()

                if (it != ItemPart.HAND) {

                    for (name in arrayOf("DIAMOND","GOLD","IRON","CHAINMAIL","LEATHER")) {

                        val item = try {
                            Material.valueOf("${name}_${it.name}")
                        } catch (ex: Exception) {
                            continue
                        }

                        val items = arrayListOf<ItemStack>()

                        items.add(ItemStack(item))

                        if (item.name.startsWith("LEATHER")) {

                            for (color in DyeColor.values()) {
                                items.add(ItemBuilder.of(item).color(color.color).build())
                            }

                        }

                        list.addAll(items)
                    }
                } else {
                    Material.values().filter{material ->

                        if (material.maxStackSize < 1) {
                            return@filter false
                        }

                        if (this.materialBlacklist.contains(material)) {
                            return@filter false
                        }

                        if (material.data == Bed::class.java
                            || material.data == PistonExtensionMaterial::class.java
                            || material.data == LongGrass::class.java
                            || material.data == RedstoneWire::class.java
                            || material.data == Crops::class.java
                        ) {
                            return@filter false
                        }

                        if (ItemPart.values().any{part -> material.name.endsWith(part.name)}) {
                            return@filter false
                        }

                        return@filter true
                    }.forEach{material -> list.add(ItemStack(material))}


                    for (color in DyeColor.values()) {
                        list.add(ItemBuilder.of(Material.FIREWORK_CHARGE).setFireworkColor(color.color).build())
                    }

                    for (type in PotionType.values().filter{ type -> type.maxLevel > 0}) {
                        list.add(Potion(type,1,false).toItemStack(1))
                    }

                    for (type in PotionType.values().filter{type -> type.maxLevel > 0}) {
                        list.add(Potion(type,1,true).toItemStack(1))
                    }

                }

                this.cached[it] = list
            }
        }
    }
}