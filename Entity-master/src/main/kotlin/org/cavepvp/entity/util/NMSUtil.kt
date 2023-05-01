package org.cavepvp.entity.util

import net.minecraft.util.com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.World

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack



/**
 * @project carnage
 *
 * @date 26/05/2020
 * @author xanderume@gmail.com
 */

object NMSUtil {

    private val version = Bukkit.getServer()::class.java.name.split(".")[3]

    @JvmStatic
    fun getHandle(world: World):Any {
        return world::class.java.getMethod("getHandle").invoke(world)
    }

    @JvmStatic
    fun getHandle(entity: Entity):Any {
        return entity::class.java.getMethod("getHandle").invoke(entity)
    }

    @JvmStatic
    fun getVersion():String {
        return version
    }

    @JvmStatic
    fun getNMSClass(name: String):Class<*> {
        return Class.forName("net.minecraft.server.$version.$name")
    }

    @JvmStatic
    fun getBukkitClass(name: String):Class<*> {
        return Class.forName("org.bukkit.craftbukkit.$version.$name")
    }

    @JvmStatic
    fun getAsNMSCopy(itemStack: ItemStack):Any? {
        return getBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy",ItemStack::class.java).invoke(itemStack)
    }

    @JvmStatic
    fun sendPacket(player: Player,packet: Any) {
        sendPacket(player,arrayOf(packet))
    }

    @JvmStatic
    fun sendPacket(player: Player,packets: Array<Any>) {

        val handle = getHandle(player)
        val connection = handle::class.java.getField("playerConnection").get(handle)

        packets.forEach{connection::class.java.getMethod("sendPacket", getNMSClass("Packet")).invoke(connection,it)}
    }

    @JvmStatic
    fun updateInventory(player: Player,container: Any) {
        updateInventory(getHandle(player),container)
    }

    @JvmStatic
    fun updateInventory(handle: Any,container: Any) {
        getNMSClass("EntityPlayer").getMethod("updateInventory", getNMSClass("Container")).invoke(handle,container)
    }

    @JvmStatic
    fun getGameProfile(player: Player):GameProfile {
        return getNMSClass("EntityHuman").getMethod("getProfile").invoke(getHandle(player)) as GameProfile
    }

    @JvmStatic
    fun setGameProfile(player: Player,profile: GameProfile) {

        val field = getNMSClass("EntityHuman").getDeclaredField("bH")

        field.isAccessible = true
        field.set(getHandle(player),profile)
        field.isAccessible = false
    }

    @JvmStatic
    fun floorByMathHelper(value: Double):Int {
        return MATH_HELPER_FLOOR_METHOD.invoke(null,value) as Int
    }

    @JvmStatic
    fun createDataWatcher():Any {
        return DATA_WATCHER_CONSTRUCTOR.newInstance(null)
    }

    private val DATA_WATCHER_CLASS: Class<*> = getNMSClass("DataWatcher")
    private val DATA_WATCHER_CONSTRUCTOR = DATA_WATCHER_CLASS.getConstructor(getNMSClass("Entity"))

    private val MATH_HELPER_FLOOR_METHOD = getNMSClass("MathHelper").getMethod("floor",Double::class.java)
}