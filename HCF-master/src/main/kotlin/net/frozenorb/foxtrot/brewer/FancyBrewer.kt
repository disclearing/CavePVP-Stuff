package net.frozenorb.foxtrot.brewer

import net.frozenorb.foxtrot.Foxtrot
import net.frozenorb.foxtrot.brewer.button.type.*
import net.frozenorb.foxtrot.brewer.inventory.FancyBrewerInventory
import net.frozenorb.foxtrot.map.MapHandler
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.BrewingStand
import org.bukkit.block.Hopper
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

@JsonClass(generateAdapter = true)
data class FancyBrewer(
    @Json(name = "_id")
    val id: UUID,
    val owner: UUID,
    val location: Location
) {

    var state = FancyBrewerState.IDLE
    var locked = false

    var lastTick = 0
    var lastHopperTick = HOPPER_TICK
    var lastResource: FancyBrewerResource? = null
    var lastResourceType: Material? = null

    var bottles = arrayOfNulls<ItemStack?>(3)
    var resources = arrayOfNulls<ItemStack?>(FancyBrewerResource.getAllResources().size)

    //TODO CANNOT USE POTION AS KEY, DIFF STORAGE
    var brewed = mutableMapOf<Int,AtomicInteger>()

    var currentAmount: Int? = null
    var currentlyBrewing: Potion? = null

    @Transient var initialized = false

    @Transient var hopper: Hopper? = null

    @Transient lateinit var stand: BrewingStand
    @Transient lateinit var inventory: FancyBrewerInventory

    fun init() {

        if (this.location.block.state !is BrewingStand) {
            return
        }

        this.stand = this.location.block.state as BrewingStand
        this.hopper = (this.location.block.getRelative(BlockFace.DOWN)?.state as? Hopper)
        
        this.inventory = FancyBrewerInventory(this)

        for ((index,button) in BUTTONS) {

            val rendered = button.render(this)

            if (rendered == null) {
                this.inventory.setItem(index,BLANK_BUTTON)
                continue
            }

            this.inventory.setItem(index,rendered)
        }

        for ((index,item) in this.bottles.withIndex()) {
            this.inventory.setItem(GLASS_SLOTS[index],item)
        }

        this.inventory.fill()
        this.initialized = true
    }

    fun openInventory(player: Player) {
        player.openInventory(this.inventory)
    }

    private fun refreshBottles() {

        val bottles = GLASS_SLOTS.map{this.inventory.getItem(it)}.toTypedArray()

        if (bottles.sumOf{it?.amount ?: 0} != this.bottles.sumOf{it?.amount ?: 0}) {
            this.flagForSave()
            this.refreshButtons()
        }

        this.bottles = bottles
    }

    internal fun refreshButtons() {

        for ((index,button) in BUTTONS) {

            val rendered = button.render(this)

            if (rendered == null) {

                if (this.inventory.getItem(index) != null) {
                    this.inventory.setItem(index, BLANK_BUTTON)
                }

                continue
            }

            this.inventory.setItem(index,rendered)
        }

    }

    fun isEmpty():Boolean {
        return this.bottles.all{it == null} && this.resources.all{it == null}
    }

    fun setBottles(index: Int,item: ItemStack?) {
        this.bottles[index] = item
    }

    fun setResource(resource: FancyBrewerResource,item: ItemStack?) {
        this.resources[resource.getIndex()] = item
    }

    fun containsResource(resource: FancyBrewerResource):Boolean {
        return this.inventory.getItem(resource.getSlot()) != null
    }

    fun getResourceByType(resource: FancyBrewerResource):ItemStack? {
        return this.inventory.getItem(resource.getSlot())
    }

    fun getResourceBySlot(slot: Int):ItemStack? {

        val resource = FancyBrewerResource.getResourceBySlot(slot)
            ?: throw IllegalArgumentException("No resource with slot $slot found!")

        return this.inventory.getItem(resource.getSlot())
    }

    fun getAllBottles():Int {
        return this.bottles.sumOf{it?.amount ?: 0}
    }

    fun getAllResources():Array<ItemStack?> {
        return FancyBrewerResource.getAllResources()
            .map{this.inventory.getItem(it.getSlot())}
            .plus(GLASS_SLOTS.map{this.inventory.getItem(it)})
            .filterNotNull()
            .toTypedArray()
    }

    fun getAllDrops():Array<ItemStack?> {

        val drops = FancyBrewerResource.getAllResources()
            .map{this.inventory.getItem(it.getSlot())}
            .toMutableList()

        drops.addAll(GLASS_SLOTS.map{this.inventory.getItem(it)})

        for ((key,value) in this.brewed) {

            val potion = Potion.fromDamage(key)

            for (i in 0 until value.get()) {
                drops.add(potion.toItemStack(1))
            }

        }

        return drops
            .filterNotNull()
            .toTypedArray()
    }

    fun getEstimatedTime():Int {

        val bottles = this.bottles.sumOf{it?.amount ?: 0}

        if (bottles < BOTTLES_PER_RESULT) {
            return -1
        }

        var lowest = bottles / BOTTLES_PER_RESULT
        var rotations = 0

        for (resource in FancyBrewerResource.getAllResources()) {

            val item = this.inventory.getItem(resource.getSlot())
                ?: continue

            if (item.amount < lowest) {
                lowest = item.amount
            }

            rotations++
        }


        if (rotations < REQUIRED_ROTATIONS) {
            return -1
        }

        return (lowest) * (rotations * BREW_INTERVAL)
    }

    fun tick() {

        if (!this.locked && this.hopper != null && this.brewed.isNotEmpty()) {

            if (--this.lastHopperTick == 0) {

                Bukkit.getServer().scheduler.runTask(Foxtrot.instance) {

                    val first = this.brewed.entries.first()
                    val slot = this.hopper!!.inventory.contents.withIndex().firstOrNull{it.value == null}

                    if (slot != null) {
                        this.hopper!!.inventory.addItem(Potion.fromDamage(first.key).toItemStack(1))

                        if (first.value.decrementAndGet() == 0) {
                            this.brewed.remove(first.key)
                        }

                        if (this.lastTick > 0) {
                            this.refreshButtons()
                        }

                        this.flagForSave()
                    }

                }

                this.lastHopperTick = HOPPER_TICK
            }

        }

        if (this.state == FancyBrewerState.IDLE) {
            return
        }

        if (this.lastTick > 0) {
            this.lastTick--
            return
        }

        if (this.lastResource != null) {

            if (this.currentlyBrewing == null || this.lastResourceType == null) {
                this.stop()
                return
            }

            this.currentlyBrewing = this.combine(this.currentlyBrewing!!,this.lastResourceType!!)
        }

        val bottles = this.bottles.sumOf{it?.amount ?: 0}
        val resource = if (this.lastResource == null) {
            FancyBrewerResource.WART
        } else {

            var next = this.lastResource!!.next()
            var attempts = 0

            while (this.inventory.getItem(next.getSlot()) == null) {

                if (++attempts == 3) {
                    this.stop()
                    break
                }

                next = next.next()
            }

            if (next == FancyBrewerResource.WART) {

                var transferred = 0

                if (this.brewed.isEmpty() && !this.locked && this.hopper != null) {

                    for (item in this.hopper!!.inventory.contents) {

                        if (item != null) {
                            continue
                        }

                        transferred++
                    }

                }

                if (transferred > 0 && this.currentlyBrewing != null) {

                    for (i in 0 until transferred) {
                        this.hopper!!.inventory!!.addItem(this.currentlyBrewing!!.toItemStack(1))
                    }

                }

                this.currentAmount = this.currentAmount!! - transferred

                if (this.currentlyBrewing != null && this.currentAmount!! > 0) {
                    this.brewed.getOrPut(this.currentlyBrewing!!.toDamageValue().toInt()) { AtomicInteger() }.addAndGet(this.currentAmount!!)
                }

                object : BukkitRunnable() {

                    private var tick = BOTTLES_PER_RESULT

                    override fun run() {

                        if (--this.tick == 0) {
                            this.cancel()
                        }

                        this@FancyBrewer.stand.inventory.setItem(this.tick,null)
                    }

                }.runTaskTimer(Foxtrot.instance,3L,3L)

                if (this.lastResource == next) {
                    this.stop()
                    return
                }
            }

            if (bottles < BOTTLES_PER_RESULT) {
                this.stop()
                return
            }

            next
        }

        val item = this.inventory.getItem(resource.getSlot())

        if (item != null) {

            if (item.amount == 1) {
                this.inventory.setItem(resource.getSlot(),null)
            } else {
                this.inventory.setItem(resource.getSlot(),item.apply{this.amount = this.amount - 1})
            }

        }

        if (resource == FancyBrewerResource.WART) {

            if (bottles <= 0) {
                this.stop()
                return
            }

            val amount = min(bottles,BOTTLES_PER_RESULT)

            this.currentAmount = amount
            this.currentlyBrewing = Potion(PotionType.WATER)

            val displayItem = ItemStack(Material.GLASS_BOTTLE)

            object : BukkitRunnable() {

                private var tick = BOTTLES_PER_RESULT

                override fun run() {

                    if (--this.tick == 0) {
                        this.cancel()
                    }

                    this@FancyBrewer.stand.inventory.setItem(this.tick,displayItem)
                }

            }.runTaskTimer(Foxtrot.instance,10L,2L)

            this.inventory.removeItem(ItemStack(Material.GLASS_BOTTLE).apply{this.amount = BOTTLES_PER_RESULT})
            this.refreshBottles()
        }

        this.lastTick = BREW_INTERVAL
        this.lastResource = resource
        this.lastResourceType = item.type
        this.refreshButtons()
        this.flagForSave()
    }

    private fun stop() {
        this.state = FancyBrewerState.IDLE
        this.lastTick = BREW_INTERVAL
        this.lastResource = null
        this.lastResourceType = null
        this.currentlyBrewing = null
        this.refreshButtons()
        this.flagForSave()
    }

    private fun combine(potion: Potion,item: Material):Potion {

        if (item == Material.NETHER_STALK) { 
            return Potion.fromItemStack(ItemStack(Material.POTION).apply{this.durability = 16})
        }

        if (item == Material.SULPHUR) {
            return potion.splash()
        }

        if (item == Material.REDSTONE) {

            if (potion.type == PotionType.POISON || potion.type == PotionType.SLOWNESS) {
                return potion
            }

            return potion.extend()
        }

        val type = potion.type

        if (item == Material.GLOWSTONE_DUST) {

            if (type != null && Foxtrot.instance.serverHandler.getPotionLimit(type) < 2) {
                return potion
            }

            if (potion.type == null) {
                return potion
            }

            return potion.apply{this.level = 2}
        }

        //TODO better way to handle these
        if (item == Material.FERMENTED_SPIDER_EYE) {

            if (potion.type == PotionType.NIGHT_VISION) {
                return potion.apply{this.type = PotionType.INVISIBILITY}
            } else if (potion.type == PotionType.SPEED) {
                return potion.apply{this.type = PotionType.SLOWNESS}
            }

        }


        val result = FancyBrewerResource.RESULT_TABLE[item] ?: return potion
        val resultType = PotionType.getByEffect(result)

        if (resultType == null || Foxtrot.instance.serverHandler.getPotionLimit(resultType) == 0) {
            return potion
        }

        return potion.apply{
            this.type = resultType
            this.level = 1
        }
    }

    fun isAllowedToInsert(slot: Int,item: ItemStack):Boolean {

        if (item.type == Material.GLASS_BOTTLE) {
            return GLASS_SLOTS.contains(slot)
        }

        val resource = FancyBrewerResource.getResourceByType(item.type)
            ?: return false

        return resource.getSlot() == slot
    }

    fun addBottles(amount: Int) {

        var remaining = amount

        for (slot in GLASS_SLOTS) {

            if (this.bottles[slot - 12] == null) {
                this.inventory.setItem(slot,ItemStack(Material.GLASS_BOTTLE).apply{this.amount = remaining})
                break
            }

            val bottles = this.bottles[slot - 12]!!

            if (bottles.amount == Material.GLASS_BOTTLE.maxStackSize) {
                continue
            }

            if ((bottles.amount + remaining) > Material.GLASS_BOTTLE.maxStackSize) {
                remaining -= Material.GLASS_BOTTLE.maxStackSize - bottles.amount

                this.inventory.setItem(slot,bottles.apply{this.amount = Material.GLASS_BOTTLE.maxStackSize})

                if (remaining <= 0) {
                    break
                }

                continue
            }

            this.inventory.setItem(slot,bottles.apply{this.amount = this.amount + remaining})
            break
        }

    }

    @JvmName("setHopper1")
    fun setHopper(hopper: Hopper?) {
        this.hopper = hopper
        this.lastHopperTick = HOPPER_TICK
        this.refreshButtons()
    }

    fun flagForSave() {
        FancyBrewerHandler.addUpdate(this)
    }

    companion object {

        val BUTTONS = hashMapOf(
            ///4 to BrewerInfoButton,
            16 to BrewerStartButton,
            25 to BrewerStorageButton,
            31 to BrewerValueButton,
            34 to BrewerTransferButton,
        )

        val GLASS_SLOTS = intArrayOf(12,13,14)
        val MAX_BOTTLES = 64 * GLASS_SLOTS.size

        const val HOPPER_TICK = 3
        const val BREW_INTERVAL = 5
        const val BOTTLES_PER_RESULT = 3
        const val REQUIRED_ROTATIONS = 3

        val BLANK_BUTTON = ItemStack(Material.STAINED_GLASS_PANE).apply{
            
            val itemMeta = this.itemMeta
            
            itemMeta.displayName = " "
            
            this.itemMeta = itemMeta
            this.durability = 15
        }
    }

}