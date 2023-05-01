package cc.fyre.modsuite.listener

import cc.fyre.core.staff.StaffHandler
import cc.fyre.core.staff.request.IPRequest
import cc.fyre.modsuite.ModSuite
import cc.fyre.shard.constants.ApiConstants
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import org.apache.logging.log4j.core.Core
import retrofit2.Response
import java.util.concurrent.TimeUnit

object VPNListener : Listener {

    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(1,TimeUnit.DAYS)
        .build(object : CacheLoader<String,Response<IPRequest>>() {

            override fun load(key: String):Response<IPRequest> {
                return StaffHandler.getService().findIpInfo(key).execute()
            }

        })

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerLogin(event: LoginEvent) {

        if (event.isCancelled) {
            return
        }

        val ip = event.connection.address.address.hostAddress

        // We have to check here, because if the result gets cached, the whitelist status won't change.
        if (StaffHandler.isVPNWhitelisted(ip)) {
            return
        }

        event.registerIntent(ModSuite.instance)

        BungeeCord.getInstance().scheduler.runAsync(ModSuite.instance) {

            val request = this.cache.getUnchecked(ip)

            when (request.code()) {
                ApiConstants.BAD_REQUEST -> ModSuite.instance.logger.info("Failed VPN request for ${event.connection.name}: Invalid IP")
                ApiConstants.TOO_MANY_REQUESTS -> ModSuite.instance.logger.info("Failed VPN request for ${event.connection.name}: Rate Limit")
                else -> ModSuite.instance.logger.info("Failed VPN request for ${event.connection.name}: N/A")
            }

            if (request.body() != null && (request.body()!!.vpn || request.body()!!.proxy) && !request.body()!!.whitelisted) {
                event.isCancelled = true
                event.setCancelReason(TextComponent("${ChatColor.RED}You cannot join with a VPN or Proxy."))
            }

            event.completeIntent(ModSuite.instance)
        }

    }

}