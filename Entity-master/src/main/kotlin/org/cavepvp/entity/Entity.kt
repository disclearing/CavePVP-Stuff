package org.cavepvp.entity

import org.cavepvp.entity.animation.EntityAnimation
import com.squareup.moshi.JsonClass
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.util.EntityUtil
import org.cavepvp.entity.util.ItemBuilder
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
@JsonClass(generateAdapter = false)
abstract class Entity(var name: String,var location: Location,@Transient var parent: Int? = null) {

    var visibility = EntityVisibility.VISIBLE
    var animations = mutableListOf<EntityAnimation>()

    @Transient var id = EntityUtil.getNewEntityId()
    @Transient var file = File("${EntityRepository.container.absolutePath}/${this.getSerializedName()}.json")

    @Transient var tick = 0
    @Transient var persistent = true
    @Transient var initialized = false

    @Transient var selfHandledViewing = false
    @Transient var selfHandledViewers: ConcurrentHashMap.KeySetView<UUID,Boolean> = ConcurrentHashMap.newKeySet()

    @Transient val viewers: ConcurrentHashMap.KeySetView<UUID, Boolean> = ConcurrentHashMap.newKeySet()
    @Transient private val moveItem = ItemBuilder.of(MOVE_ITEM_TYPE)
        .name(MOVE_ITEM_NAME)
        .lore("$MOVE_ITEM_LORE_KEY${this.parent ?: this.id}")
        .data(this.getEntityType().typeId)
        .build()

    abstract fun init()

    @JvmName("getLocation1")
    fun getLocation():Location {
        return this.location
    }

    open fun updateLocation(location: Location) {
        this.location = location
    }

    fun getDistance(location: Location):Double {
        return this.location.distance(location)
    }

    fun getDistanceSquared(location: Location):Double {

        if (location.world != this.location.world) {
            return -1.0
        }

        return this.location.distanceSquared(location)
    }

    fun updateVisibility(visibility: EntityVisibility) {
        this.visibility = visibility
        this.visibility.action.test(this)
    }

    fun getMoveItem():ItemStack {
        return this.moveItem
    }

    private fun getSerializedName():String {
        return this.name
    }

    abstract fun getEntityType():EntityType

    abstract fun sendCreatePacket(player: Player)
    abstract fun sendUpdatePacket(player: Player)
    abstract fun sendRefreshPacket(player: Player)
    abstract fun sendDestroyPacket(player: Player)

    open fun sendToAll(lambda: (player: Player) -> Unit) {
        this.viewers.mapNotNull{Bukkit.getServer().getPlayer(it)}.filter{it.isOnline && !it.isDead}.forEach(lambda)
    }

    fun getAllViewers():List<Player> {
        return this.viewers.mapNotNull{Bukkit.getServer().getPlayer(it)}.filter{it.isOnline && !it.isDead}
    }

    open fun onTick() {}
    open fun onLeftClick(player: Player) {}
    open fun onRightClick(player: Player) {}

    companion object {

        const val DISTANCE = 1600.0

        val MOVE_ITEM_TYPE = Material.MONSTER_EGG
        val MOVE_ITEM_NAME = "${ChatColor.YELLOW}${ChatColor.BOLD}Move Entity"
        val MOVE_ITEM_LORE_KEY = "${ChatColor.DARK_GRAY}#"

        val BLOCK_HEIGHTS = hashMapOf(
            Material.STEP to 0.5,
            Material.WOOD_STEP to 0.5,
            Material.TRAP_DOOR to 0.2,
            Material.CARPET to 0.0625,
        )

    }

}