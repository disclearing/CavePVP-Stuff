package org.cavepvp.entity.ai

import net.minecraft.server.v1_7_R4.*
import net.minecraft.util.com.mojang.authlib.GameProfile
import org.apache.commons.lang.WordUtils
import org.bukkit.World
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftContainer
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.spigotmc.ActivationRange
import java.util.*

object ShardNmsAI : NmsAI {

    private val CONTAINER_COUNTER_FIELD = EntityPlayer::class.java.getDeclaredField("containerCounter")

    init {
        CONTAINER_COUNTER_FIELD.isAccessible = true
    }

    override fun getLatency(player: Player):Int {
        return (player as CraftPlayer).handle.ping
    }

    override fun getProtocolVersion(player: Player): Int {
        return (player as CraftPlayer).handle.playerConnection.networkManager.version
    }

    override fun getName(item: ItemStack): String {

        var toReturn = CraftItemStack.asNMSCopy(item).name

        if (toReturn.contains(".")) {
            toReturn = WordUtils.capitalize(item.type.toString().toLowerCase().replace("_"," "))
        }

        return toReturn
    }

    override fun getInventoryName(inventory: Inventory): String {
        return (inventory as CraftInventory).inventory.inventoryName
    }

    override fun loadOfflinePlayerData(uuid: UUID, username: String): Player {
        return EntityPlayer(
            MinecraftServer.getServer(),
            MinecraftServer.getServer().getWorldServer(0),
            GameProfile(uuid,username),
            PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
        ).bukkitEntity.also{it.loadData()}
    }

    override fun getWorldData(world: World,x: Int, y: Int, z: Int): Int {
        return (world as CraftWorld).handle.getData(x,y,z)
    }

    override fun setSkullTexture(item: ItemStack, texture: String): ItemStack {

        val copy = CraftItemStack.asNMSCopy(item)

        val tag = copy.tag ?: NBTTagCompound()

        val skullOwner = NBTTagCompound()

        skullOwner.setString("Id",UUID.randomUUID().toString())

        val compound = NBTTagCompound()

        compound.setString("Value", texture)

        val textures = NBTTagList()

        textures.add(compound)

        val properties = NBTTagCompound()

        properties.set("textures", textures)
        skullOwner.set("Properties", properties)
        tag.set("SkullOwner", skullOwner)

        copy.tag = tag
        return CraftItemStack.asBukkitCopy(copy)
    }

    override fun isActive(entity: Entity): Boolean {
        return ActivationRange.checkIfActive((entity as CraftEntity).handle)
    }

    override fun isMobSpawner(entity: Entity): Boolean {
        return ((entity as CraftEntity).handle as? EntityInsentient)?.fromMobSpawner == true
    }

    override fun setMobSpawner(entity: Entity,spawner: Boolean) {

        val insentient = (entity as CraftEntity).handle

        if (insentient !is EntityInsentient) {
            return
        }

        insentient.fromMobSpawner = spawner
    }

    override fun getWindowId(player: Player): Int {
        return (player as CraftPlayer).handle.activeContainer.windowId
    }

    override fun getContainerCounter(player: Player): Int {
        return CONTAINER_COUNTER_FIELD.getInt((player as CraftPlayer).handle)
    }

    override fun updateInventory(player: Player) {
        (player as CraftPlayer).handle.updateInventory(player.handle.activeContainer)
    }

    override fun updateInventoryWithoutEvent(player: Player,inventory: Inventory) {

        if (player !is CraftPlayer) {
            return
        }

        val handle = player.handle

        if (handle.playerConnection == null) {
            return
        }

        val container = CraftContainer(inventory,player,handle.nextContainerCounter())

        val size = container.bukkitView.topInventory.size
        val title = container.bukkitView.title

        handle.playerConnection.sendPacket(PacketPlayOutOpenWindow(container.windowId,inventory.type.ordinal,title,size,true))
        handle.activeContainer = container
        handle.activeContainer.addSlotListener(handle)
    }

}