package org.cavepvp.entity.ai

import net.minecraft.server.v1_7_R4.DataWatcher
import org.cavepvp.entity.util.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityPlugin
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.hologram.line.HologramLine
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.type.npc.NPCAnimationType
import org.cavepvp.entity.util.ItemPart
import org.cavepvp.entity.util.NMSUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ShardEntityAI : EntityAI {

    override fun sendNPCCreatePacket(player: Player,npc: NPC) {

        val watcher = NMSUtil.createDataWatcher() as DataWatcher

        watcher.a(0,0.toByte())
        watcher.a(1,20.0F)
        watcher.a(2,npc.name)
        watcher.a(3,0x01)
        watcher.a(4,0x00)
        watcher.a(5,0x01)
        watcher.a(6,20.0F)
        watcher.a(7,0)
        watcher.a(8,0.toByte())
        watcher.a(9,0.toByte())
        watcher.a(10,127.toByte())
        watcher.a(15,1.toByte())
        watcher.a(16,0x01)
        watcher.a(17,0F)

        PlayerUtil.sendPacket(player,ShardPacketAI.createPlayerInfoAdd(0, GameMode.CREATIVE,npc.name,npc.profile))
        PlayerUtil.sendPacket(player,ShardPacketAI.createNamedEntitySpawn(npc.id,npc.getLocation(),npc.profile,watcher,npc.getEquipment(ItemPart.HAND)))
        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityHeadRotation(npc.id,npc.getLocation().yaw))
        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityLook(npc.id,npc.getLocation().yaw,npc.getLocation().pitch,npc.location.block.type != Material.AIR))

        if (npc.swing) {
            Bukkit.getServer().scheduler.runTaskLater(EntityPlugin.instance,{
                PlayerUtil.sendPacket(player,ShardPacketAI.createEntityAnimation(npc.id,NPCAnimationType.SWING.id))
            },5L)
        }

        if (npc.tagVisibility == EntityVisibility.HIDDEN) {
            Bukkit.getServer().scheduler.runTaskLater(EntityPlugin.instance,{npc.ensureTagVisibility(player)},2L)
        }

        if (npc.tabVisibility == EntityVisibility.HIDDEN) {
            Bukkit.getServer().scheduler.runTaskLater(EntityPlugin.instance,{

                if (!player.isOnline) {
                    return@runTaskLater
                }

                PlayerUtil.sendPacket(player,ShardPacketAI.createPlayerInfoRemove(
                    npc.name,
                    npc.profile
                ))
            },20L)
        }

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityTeleport(npc.id,npc.getLocation()))
    }

    override fun sendNPCUpdatePacket(player: Player,npc: NPC) {}

    override fun sendNPCRefreshPacket(player: Player,npc: NPC) {
        this.sendNPCDestroyPacket(player,npc)
        this.sendNPCCreatePacket(player,npc)
    }

    override fun sendNPCDestroyPacket(player: Player,npc: NPC) {

        val legacy = PlayerUtil.isLegacy(player)

        val entityIds = mutableListOf<Int>()

        npc.hologram.lines.forEach{

            if (legacy) {
                entityIds.add(it.horseId)
            }

            entityIds.add(it.skullId)
        }

        entityIds.add(npc.id)

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityDestroy(entityIds.toIntArray()))

        if (npc.tabVisibility == EntityVisibility.HIDDEN) {
            return
        }

        PlayerUtil.sendPacket(player,ShardPacketAI.createPlayerInfoRemove(npc.name,npc.profile))
    }

    override fun sendNPCTagVisibility(player: Player,npc: NPC) {

        if (npc.tabVisibility == EntityVisibility.VISIBLE) {
            PlayerUtil.sendPacket(player,ShardPacketAI.createEntityDestroy(intArrayOf(npc.batId)))
            return
        }

        val watcher = NMSUtil.createDataWatcher() as DataWatcher

        watcher.a(0,(0 or 1 shl 5).toByte())
        watcher.a(1,300.toShort())
        watcher.a(12,-1700000)

        PlayerUtil.sendPacket(player,ShardPacketAI.createSpawnEntityLiving(
            npc.batId,
            EntityType.BAT.typeId.toInt(),
            npc.getLocation(),
            watcher,
            PlayerUtil.isLegacy(player)
        ))

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityAttach(0,npc.batId,npc.id))
    }

    override fun sendHologramCreatePacket(player: Player, hologram: Hologram) {
        hologram.lines.forEach{it.render(player)}
    }

    override fun sendHologramUpdatePacket(player: Player, hologram: Hologram) {
        hologram.lines.forEach{it.update(player)}
    }

    override fun sendHologramRefreshPacket(player: Player, hologram: Hologram) {
        hologram.sendDestroyPacket(player)
        hologram.sendCreatePacket(player)
    }

    override fun sendHologramDestroyPacket(player: Player, hologram: Hologram) {

        val legacy = PlayerUtil.isLegacy(player)
        val entityIds = mutableListOf<Int>()

        hologram.lines.forEach{

            if (legacy) {
                entityIds.add(it.horseId)
            }

            entityIds.add(it.skullId)
        }

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityDestroy(entityIds.toIntArray()))
    }

    override fun renderHologramTextLine(player: Player,text: String,line: HologramTextLine) {

        val watcher = NMSUtil.createDataWatcher() as DataWatcher

        if (!PlayerUtil.isLegacy(player)) {

            watcher.a(0,32.toByte()) // Invisible
            watcher.a(2,ChatColor.translateAlternateColorCodes('&',text))
            watcher.a(3,(if (line.blank) 0 else 1).toByte())

            PlayerUtil.sendPacket(player,ShardPacketAI.createSpawnEntityLiving(
                line.skullId,
                HologramLine.ARMOR_STAND_ID,
                line.location,
                watcher,
                false
            ))
            return
        }

        watcher.a(0,0.toByte())
        watcher.a(1,300.toShort())
        watcher.a(10,ChatColor.translateAlternateColorCodes('&',text))
        watcher.a(11,(if (line.blank) 0 else 1).toByte())
        watcher.a(12,-1700000)

        PlayerUtil.sendPacket(player,ShardPacketAI.createSpawnEntity(line.skullId,line.location,66))
        PlayerUtil.sendPacket(player,ShardPacketAI.createSpawnEntityLiving(
            line.horseId,
            EntityType.HORSE.typeId.toInt(),
            line.location,
            watcher,
            true
        ))
        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityAttach(
            0,
            line.horseId,
            line.skullId
        ))
    }

    override fun updateHologramTextLine(player: Player,text: String,line: HologramTextLine) {

        val legacy = PlayerUtil.isLegacy(player)

        val watcher = NMSUtil.createDataWatcher() as DataWatcher

        watcher.a(if (legacy) 10 else 2,ChatColor.translateAlternateColorCodes('&',text))
        watcher.a(if (legacy) 11 else 3,(if (line.blank) 0 else 1).toByte())

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityMetadata(if (legacy) line.horseId else line.skullId,watcher))
    }

    override fun destroyHologramTextLine(player: Player, line: HologramTextLine) {

        val legacy = PlayerUtil.isLegacy(player)

        PlayerUtil.sendPacket(player,ShardPacketAI.createEntityDestroy(
            if (legacy) intArrayOf(line.skullId,line.horseId) else intArrayOf(line.skullId)
        ))
    }

    override fun renderHologramItemLine(player: Player, item: org.bukkit.inventory.ItemStack, line: HologramTextLine) {

    }

    override fun handleEntityViewerTick(entity: Entity) {

        val players = ConcurrentHashMap.newKeySet<UUID>()

        (entity.location.world as CraftWorld).handle.playerMap.forEachNearby(entity.location.x,entity.location.y,entity.location.z,48.0,true) {

            if (!it.isAlive || !it.bukkitEntity.isOnline) {
                return@forEachNearby
            }

            if (entity.selfHandledViewing) {

                if (!entity.selfHandledViewers.contains(it.uniqueID)) {
                    return@forEachNearby
                }

                players.add(it.uniqueID)

                if (entity.viewers.contains(it.uniqueID)) {
                    return@forEachNearby
                }

                entity.viewers.add(it.uniqueID)
                entity.sendCreatePacket(it.bukkitEntity)
                return@forEachNearby
            }

            if (!entity.viewers.contains(it.uniqueID)) {
                entity.viewers.add(it.uniqueID)
                entity.sendCreatePacket(it.bukkitEntity)
            }

            players.add(it.uniqueID)
        }

        entity.viewers.filter{!players.contains(it)}.forEach{
            Bukkit.getServer().getPlayer(it)?.also{player ->
                entity.viewers.remove(player.uniqueId)
                entity.sendDestroyPacket(player)
            }
        }

    }

}
