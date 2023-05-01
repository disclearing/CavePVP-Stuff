package org.cavepvp.entity

import org.cavepvp.entity.animation.EntityAnimationRegistry
import org.cavepvp.entity.animation.moshi.EntityAnimationJsonAdapter
import org.cavepvp.entity.animation.moshi.EntityAnimationTypeJsonAdapter
import org.cavepvp.entity.animation.type.RingsAnimation
import org.cavepvp.entity.listener.EntityListener
import org.cavepvp.entity.listener.EntityPacketListener
import org.cavepvp.entity.thread.EntityThread
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.hologram.adapter.HologramAdapter
import org.cavepvp.entity.type.hologram.line.HologramLine
import org.cavepvp.entity.type.hologram.line.type.HologramItemLine
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.type.npc.listener.NPCVisibilityListener
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.cavepvp.entity.animation.type.RainbowAnimation
import org.cavepvp.entity.moshi.MoshiUtil
import java.math.BigDecimal

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
object EntityHandler {

    private val entities = mutableMapOf<Int,Entity>()
    private val entitiesByName = mutableMapOf<String,Entity>()

    private val adapters = mutableListOf<HologramAdapter>()

    fun onLoad() {
        MoshiUtil.rebuild{
            it.add(EntityAnimationJsonAdapter)
            it.add(EntityAnimationTypeJsonAdapter)
            it.add(
                PolymorphicJsonAdapterFactory.of(Entity::class.java,"type")
                .withSubtype(NPC::class.java,"NPC")
                .withSubtype(Hologram::class.java,"HOLOGRAM")
            )
            it.add(
                PolymorphicJsonAdapterFactory.of(HologramLine::class.java,"type")
                .withSubtype(HologramTextLine::class.java,"TEXT")
                .withSubtype(HologramItemLine::class.java,"ITEM")
            )
        }

        for (color in ChatColor.values().filter{it.isColor}) {
            EntityAnimationRegistry.register(RingsAnimation(color))
        }

        EntityAnimationRegistry.register(RainbowAnimation)

        EntityRepository.onLoad()

        // Load them a second later, let other plugins register their own type first
        Bukkit.getServer().scheduler.runTaskLater(EntityPlugin.instance,{

            EntityRepository.findAll().forEach{entity ->

                if (entity is NPC) {
                    entity.hologram.parent = entity.id
                }

                this.register(entity)
            }

            EntityPlugin.instance.logger.info("[Entities] Loaded ${this.entities.size} ${if (this.entities.size == 1) "entity" else "entities"} to memory from ${EntityRepository.container.nameWithoutExtension} container.")
        },20L)

    }

    fun destroy(entity: Entity) {
        this.entities.remove(entity.id)
        this.entitiesByName.remove(entity.name.toLowerCase())

        if (!entity.file.exists() || !entity.persistent) {
            return
        }

        EntityRepository.deleteById(entity)
    }

    fun register(entity: Entity) {
        this.entities[entity.id] = entity
        this.entitiesByName[entity.name.toLowerCase()] = entity

        entity.init()
        entity.initialized = true

        if (!entity.persistent) {
            return
        }

        if (entity.file.exists()) {
            return
        }

        entity.file.createNewFile()

        EntityRepository.updateById(entity)
    }

    fun registerType(clazz: Class<out Entity>,label: String) {
        MoshiUtil.addToPolymorphic(Entity::class.java,clazz,label)
    }

    fun getAllEntities():List<Entity> {
        return this.entities.values.toList()
    }

    fun getEntityById(id: Int): Entity? {
        return this.entities[id]
    }

    fun getEntityByName(name: String): Entity? {
        return this.entitiesByName[name.toLowerCase()]
    }

    fun addAdapter(adapter: HologramAdapter) {
        this.adapters.add(adapter)
    }

    fun getAdapters():List<HologramAdapter> {
        return this.adapters
    }

    fun onDisable() {

        val written = this.entities.values.filter{it.persistent}.sumOf{

            if (!it.file.exists()) {
                return@sumOf BigDecimal.ZERO
            }

            try {
                EntityRepository.updateById(it)
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@sumOf BigDecimal.ZERO
            }

            return@sumOf BigDecimal.ONE
        }.toInt()

        EntityPlugin.instance.logger.info("[Entities] Wrote $written ${if (written == 1) "entity" else "entities"} to disk in ${EntityRepository.container.nameWithoutExtension} container.")
    }

}