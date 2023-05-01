package cc.fyre.modsuite.freeze

import cc.fyre.modsuite.freeze.event.PlayerFreezeEvent
import cc.fyre.neutron.Neutron
import com.lunarclient.bukkitapi.LunarClientAPI
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTitle
import com.lunarclient.bukkitapi.title.TitleType
import net.minecraft.util.com.google.common.cache.CacheBuilder
import net.minecraft.util.com.google.common.cache.CacheLoader
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

object FreezeHandler {

    private val frozen = CacheBuilder.newBuilder()
        .expireAfterAccess(1L,TimeUnit.HOURS)
        .build(object : CacheLoader<Player, List<PotionEffect>>() {

            override fun load(player: Player): List<PotionEffect> {
                return player.activePotionEffects.toList()
            }

    })

    fun getAllFrozen():Map<Player,List<PotionEffect>> {
        return this.frozen.asMap()
    }

    fun get(player: Player):List<PotionEffect>? {
        return this.frozen.getUnchecked(player)
    }

    fun isFrozen(player: Player):Boolean {
        return this.frozen.asMap().containsKey(player)
    }

    fun setFrozen(player: Player,value: Boolean) {

        if (this.frozen.asMap().containsKey(player) == value) {
            throw IllegalStateException("${player.name} is already frozen.")
        }

        if (value) {
            this.frozen.apply(player)

            LunarClientAPI.getInstance().sendPacket(player,LCPacketTitle(TitleType.TITLE.name,"${ChatColor.GREEN}${ChatColor.BOLD}You have been frozen!",TITLE_DURATION,0L,TITLE_DURATION))

            player.velocity = VELOCITY
            player.activePotionEffects.forEach{player.removePotionEffect(it.type)}
            player.addPotionEffect(POTION_EFFECT)
            player.walkSpeed = 0.0F
        } else {
            player.walkSpeed = 0.2F
            player.removePotionEffect(PotionEffectType.JUMP)
            player.addPotionEffects(this.frozen.asMap().remove(player))

            this.frozen.invalidate(player)
        }

        PlayerFreezeEvent(player,value).call()
    }

    fun isAbleToFreeze(sender: Player,player: Player):Boolean {

        val profile = Neutron.getInstance().profileHandler.fromUuid(player.uniqueId)
        val senderProfile = Neutron.getInstance().profileHandler.fromUuid(sender.uniqueId)

        if (profile == null || senderProfile == null) {
            return true
        }

        return senderProfile.activeRank.weight.get() >= profile.activeRank.weight.get()
    }

    val VELOCITY = Vector(0,0,0)
    private val POTION_EFFECT = PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,128)

    const val TITLE_DURATION = 3000L
    const val FROZEN_MESSAGE = "§c§lYou cannot do this whilst frozen."
}