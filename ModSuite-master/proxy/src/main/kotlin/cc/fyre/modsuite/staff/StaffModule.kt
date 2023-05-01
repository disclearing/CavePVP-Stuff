package cc.fyre.modsuite.staff

import cc.fyre.core.BungeeCore
import cc.fyre.core.BungeeModule
import cc.fyre.core.CoreHandler
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Listener

object StaffModule : BungeeModule {

    override fun init(core: BungeeCore) {}

    override fun getCommands(): List<Command> {
        return listOf(

        )
    }

    override fun getHandler(): CoreHandler<*>? {
        return null
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            StaffListener
        )
    }

}