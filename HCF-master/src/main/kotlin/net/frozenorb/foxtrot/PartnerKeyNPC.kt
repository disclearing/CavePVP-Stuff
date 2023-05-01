package net.frozenorb.foxtrot

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.moshi.JsonClass
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.bukkit.Bukkit
import org.bukkit.Location
import org.cavepvp.entity.type.npc.NPC
import java.io.IOException
import java.util.*

@JsonClass(generateAdapter = true)
class PartnerKeyNPC(location: Location) : NPC(NAME,location) {

    @Transient var index = 0

    override fun init() {
        super.init()


        val skin = skinCache[index]

        this.setSkin("Hey", skin.first, skin.second)

        Bukkit.getServer().scheduler.runTaskTimerAsynchronously(Foxtrot.instance, {
            if (index == skinCache.size - 1) {
                index = 0
            } else {
                index++
            }
            val next = skinCache[index]
            this.setSkin("Hey", next.first, next.second)
        }, 15 * 20L, 15 * 20L)
    }

    companion object {

        const val NAME = "PartnerKey"

        val client = OkHttpClient()
        val skinCache = arrayListOf<Pair<String,String>>()

        fun loadSkins() {

            for (value in PartnerType.values()) {

                val uuid = value.uuid

                var skin: Pair<String,String>? = null

                try {
                    skin = this.getSkin(uuid)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (skin == null) {
                    Bukkit.getLogger().info("Failed to load skin for " + value.crateName + " (Mojang API).")
                    continue
                }

                skinCache.add(skin)
            }

        }


        @Throws(IOException::class)
        fun getSkin(uuid: UUID):Pair<String,String>? {
            val request: Request = Request.Builder().url("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false").build()
            val execute: Response = this.client.newCall(request).execute()
            return run {
                val json: JsonObject =
                    JsonParser().parse(execute.body!!.string()).getAsJsonObject()
                if (!json.has("properties")) {
                    null
                } else {
                    val properties = json.getAsJsonArray("properties")[0].asJsonObject
                    Pair(properties["value"].asString, properties["signature"].asString)
                }
            }
        }
    }


}