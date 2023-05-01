package net.frozenorb.foxtrot.brewer.inventory

import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.FancyBrewerResource
import net.minecraft.server.v1_7_R4.EntityHuman
import net.minecraft.server.v1_7_R4.IInventory
import net.minecraft.server.v1_7_R4.ItemStack
import net.minecraft.server.v1_7_R4.Items
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryHolder

class IFancyBrewerInventory(
    private val brewer: FancyBrewer,
) : IInventory {

    private val items = arrayOfNulls<ItemStack>(FancyBrewerInventory.SIZE)
    private var maxStack = IInventory.MAX_STACK
    private val viewers = mutableListOf<HumanEntity>()

    private val type = InventoryType.CHEST
    private val owner = this.brewer.stand

    override fun getSize(): Int {
        return this.items.size
    }

    override fun getItem(i: Int): ItemStack? {
        return this.items[i]
    }

    override fun getOwner(): InventoryHolder {
        return this.owner
    }

    override fun splitStack(i: Int, j: Int): ItemStack? {
        val stack = this.getItem(i) ?: return null

        val result: ItemStack

        if (stack.count <= j) {
            this.setItem(i, null)
            result = stack
        } else {
            result = CraftItemStack.copyNMSStack(stack, j)
            stack.count -= j

            if (this.brewer.initialized) {

                if (stack.item == Items.GLASS_BOTTLE) {
                    this.brewer.setBottles(i - 12,CraftItemStack.asBukkitCopy(result))
                    this.brewer.flagForSave()
                    this.brewer.refreshButtons()
                } else {

                    val resource = FancyBrewerResource.getResourceBySlot(i)

                    if (resource != null) {
                        this.brewer.setResource(resource,CraftItemStack.asBukkitCopy(result))
                        this.brewer.flagForSave()
                        this.brewer.refreshButtons()
                    }

                }

            }
        }

        this.update()

        return result
    }

    override fun splitWithoutUpdate(i: Int): ItemStack? {
        val stack = this.getItem(i) ?: return null
        val result: ItemStack

        if (stack.count <= 1) {
            this.setItem(i, null)
            result = stack
        } else {
            result = CraftItemStack.copyNMSStack(stack, 1)
            stack.count -= 1

            if (this.brewer.initialized) {

                if (stack.item == Items.GLASS_BOTTLE) {
                    this.brewer.setBottles(i - 12,CraftItemStack.asBukkitCopy(result))
                    this.brewer.flagForSave()
                    this.brewer.refreshButtons()
                } else {

                    val resource = FancyBrewerResource.getResourceBySlot(i)

                    if (resource != null) {
                        this.brewer.setResource(resource,CraftItemStack.asBukkitCopy(result))
                        this.brewer.flagForSave()
                        this.brewer.refreshButtons()
                    }

                }

            }
        }

        return result
    }

    override fun setItem(i: Int, itemstack: ItemStack?) {

        if (itemstack == null) {

            if (this.brewer.initialized && this.items[i] != null) {

                if (this.items[i]!!.item == Items.GLASS_BOTTLE) {
                    this.brewer.setBottles(i - 12,null)
                    this.brewer.flagForSave()
                    this.brewer.refreshButtons()
                } else {

                    val resource = FancyBrewerResource.getResourceBySlot(i)

                    if (resource != null) {
                        this.brewer.setResource(resource,null)
                        this.brewer.flagForSave()
                        this.brewer.refreshButtons()
                    }


                }

            }

            this.items[i] = null
            return
        }

        this.items[i] = itemstack

        if (this.maxStackSize > 0 && itemstack.count > this.maxStackSize) {
            itemstack.count = this.maxStackSize
        }

        if (!this.brewer.initialized) {
            return
        }

        if (itemstack.item == Items.GLASS_BOTTLE) {
            this.brewer.setBottles(i - 12,CraftItemStack.asBukkitCopy(itemstack))
            this.brewer.flagForSave()
            this.brewer.refreshButtons()
            return
        }

        val resource = FancyBrewerResource.getResourceBySlot(i) ?: return

        this.brewer.setResource(resource,CraftItemStack.asBukkitCopy(itemstack))
        this.brewer.flagForSave()
        this.brewer.refreshButtons()
    }

    override fun getInventoryName(): String {
        return FancyBrewerInventory.TITLE
    }

    override fun getMaxStackSize(): Int {
        return this.maxStack
    }

    override fun setMaxStackSize(size: Int) {
        this.maxStack = size
    }

    override fun update() {}

    override fun a(entityhuman: EntityHuman?): Boolean {
        return true
    }

    override fun getContents(): Array<ItemStack?> {
        return this.items
    }

    override fun onOpen(who: CraftHumanEntity) {
        this.viewers.add(who)
    }

    override fun onClose(who: CraftHumanEntity) {
        this.viewers.remove(who)
    }

    override fun getViewers(): List<HumanEntity> {
        return this.viewers
    }

    fun getType(): InventoryType {
        return this.type
    }

    override fun closeContainer() {}

    override fun startOpen() {}

    override fun k_(): Boolean {
        return false
    }

    override fun b(i: Int, itemstack: ItemStack?): Boolean {
        return true
    }

}
