package org.cavepvp.entity.thread

import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.EntityPlugin
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.ai.ShardEntityAI
import org.cavepvp.entity.ai.ShardPacketAI
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.type.npc.NPCAnimationType
import org.cavepvp.entity.util.NMSUtil

object EntityThread : Thread("Shard - Entity Thread") {

    private var tick = 0

    // TODO We could possible cache the entity's surrounding chunks and see if the players are in those chunks?
    override fun run() {

        while (true) {

            var tickViewers = false

            if (++this.tick > 20) {
                this.tick = 0
                tickViewers = true
            }

            EntityHandler.getAllEntities().forEach{entity ->

                if (entity.visibility == EntityVisibility.HIDDEN) {
                    return@forEach
                }

                if (tickViewers) {

                    try {
                        ShardEntityAI.handleEntityViewerTick(entity)
                    } catch (ex: ConcurrentModificationException) {
                        EntityPlugin.instance.logger.info("vSpigot entity tracker would have crashed the thread but it has been prevented.")
                    }

                }

                val viewers = entity.getAllViewers()

                if (entity.initialized) {
                    entity.tick = this.tick
                    entity.onTick()

                    if (entity is NPC && entity.faces) {

                        val outsideOfFacing = entity.lookingTowards.toHashSet()

                        (entity.location.world as CraftWorld).handle.playerMap.forEachNearby(entity.location.x,entity.location.y,entity.location.z,NPC.FACE_DISTANCE,true) {

                            if (outsideOfFacing.contains(it.uniqueID)) {
                                outsideOfFacing.remove(it.uniqueID)
                            }

                            entity.lookTowardsPlayer(it.bukkitEntity,false)
                        }

                        if (outsideOfFacing.isNotEmpty()) {

                            val packets = arrayListOf(
                                ShardPacketAI.createEntityHeadRotation(entity.id,entity.location.yaw),
                                ShardPacketAI.createEntityLook(entity.id,entity.location.yaw,entity.location.pitch,true)
                            )

                            if (entity.swing) {
                                packets.add(ShardPacketAI.createEntityAnimation(entity.id, NPCAnimationType.SWING.id))
                            }

                            outsideOfFacing.forEach{
                                entity.lookingTowards.remove(it)

                                val player = Bukkit.getServer().getPlayer(it) ?: return@forEach

                                packets.forEach{packet -> NMSUtil.sendPacket(player,packet)}
                            }
                        }
                    }

                }

                entity.animations.forEach{
                    // We tick the animation and set the values there,
                    // then we loop through all the players and tick it individually for performance,
                    // this reduces the amount of viewer loops
                    it.onTick(entity)
                    viewers.forEach{viewer -> it.onTick(entity,viewer)}
                }
            }

            sleep(50)
        }

    }

}