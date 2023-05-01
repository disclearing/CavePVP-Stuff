package cc.fyre.modsuite

import cc.fyre.core.BungeeCore
import cc.fyre.modsuite.command.FirewallBlacklistCommand
import cc.fyre.modsuite.staff.StaffModule
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.plugin.Plugin

class ModSuite : Plugin() {

    override fun onEnable() {
        instance = this

        BungeeCore.instance.registerModule(StaffModule)
        //BungeeCord.getInstance().getPluginManager().registerListener(this,VPNListener)
        BungeeCord.getInstance().getPluginManager().registerCommand(this,FirewallBlacklistCommand)
    }

    override fun onDisable() {}

    companion object {

        lateinit var instance: ModSuite

    }

}