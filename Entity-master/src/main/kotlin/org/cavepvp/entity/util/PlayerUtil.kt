package org.cavepvp.entity.util

import net.minecraft.util.com.mojang.authlib.GameProfile
import net.minecraft.util.com.mojang.authlib.properties.Property
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.Prompt
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.cavepvp.entity.EntityPlugin
import org.cavepvp.entity.ai.ShardPacketAI

object PlayerUtil {

    @JvmStatic
    fun isLegacy(player: Player):Boolean {
        return (player as CraftPlayer).handle.playerConnection.networkManager.version <= 5
    }

    @JvmStatic
    fun sendPacket(player: Player,packet: Any) {
        ShardPacketAI.sendPacket(player,packet)
    }

    @JvmStatic
    fun createCloneProfile(player: Player):GameProfile {
        return createCloneProfile(NMSUtil.getGameProfile(player))
    }

    @JvmStatic
    fun startPrompt(player: Player,prompt: Prompt,timeout: Int = 30,plugin: Plugin = EntityPlugin.instance) {
        player.beginConversation(ConversationFactory(plugin).withFirstPrompt(prompt).withModality(false).withLocalEcho(false).withTimeout(timeout).buildConversation(player))
    }

    @JvmStatic
    fun createCloneProfile(profile: GameProfile):GameProfile {

        val toReturn = GameProfile(profile.id,profile.name)
        var textures = profile.properties["textures"].firstOrNull()

        if (textures != null && textures.value != null && textures.hasSignature()) {
            textures = Property(textures.name,textures.value,textures.signature)
            toReturn.properties.put("textures",textures)
        }

        return toReturn
    }

//    fun sendActionBar(player: Player,text: String) {
//
//        if (isLegacy(player)) {
//
//            if (!LunarClientAPI.getInstance().isRunningLunarClient(player)) {
//                return
//            }
//
//            LunarClientAPI.getInstance().sendPacket(player,LCPacketTitle(
//                TitleType.SUBTITLE.name,
//                text,
//                3000L,
//                0L,
//                3000L
//            ))
//            return
//        }
//
//        sendPacket(player,ShardPacketAI.createChat(text,2,false))
//    }

}