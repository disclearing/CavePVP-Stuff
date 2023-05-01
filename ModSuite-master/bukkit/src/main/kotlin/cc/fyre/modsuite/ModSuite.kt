package cc.fyre.modsuite

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.command.*
import cc.fyre.modsuite.mod.editor.ModLayoutListener
import cc.fyre.modsuite.mod.item.inventory.ModSuiteInventoryListener
import cc.fyre.modsuite.mod.listener.*
import cc.fyre.modsuite.mod.packet.PidginNiggerPacket
import cc.fyre.modsuite.mod.scheduler.ModModeRefreshScheduler
import cc.fyre.modsuite.mod.visibility.ModVisibilityAdapter
import cc.fyre.modsuite.util.ConfigUtil
import cc.fyre.proton.Proton
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class ModSuite : JavaPlugin() {

    private var worldEditPlugin: Plugin? = null

    lateinit var config: ModSuiteConfig

    override fun onEnable() {
        instance = this

        this.config = ConfigUtil.register("config",this.name,this.dataFolder,ModSuiteConfig())
        this.worldEditPlugin = this.server.pluginManager.getPlugin("WorldEdit") ?: null

        Proton.getInstance().pidginHandler.registerPacket(PidginNiggerPacket::class.java)
        Proton.getInstance().visibilityHandler.registerHandler("MODSUITE_VISIBILITY",ModVisibilityAdapter)

        if (!this.config.modModeDisabled) {
            Proton.getInstance().commandHandler.registerClass(ModCommand::class.java)
            Proton.getInstance().commandHandler.registerClass(EditModModeCommand::class.java)
        }

        Proton.getInstance().commandHandler.registerClass(VanishCommand::class.java)
        Proton.getInstance().commandHandler.registerClass(InvseeCommand::class.java)
        Proton.getInstance().commandHandler.registerClass(BungeeTPCommand::class.java)
        
        if (!this.config.modModeDisabled) {
            Bukkit.getServer().scheduler.runTaskTimerAsynchronously(ModSuite.instance, ModModeRefreshScheduler,20L,20L)
        }

        if (!this.config.modModeDisabled) {
            this.server.pluginManager.registerEvents(ModListener,this)
        }

        this.server.pluginManager.registerEvents(VisibilityListener,this)
        this.server.pluginManager.registerEvents(ModInventoryListener,this)
        this.server.pluginManager.registerEvents(ModSuiteInventoryListener,this)
        this.server.pluginManager.registerEvents(ModLayoutListener,this)
        this.server.pluginManager.registerEvents(ModTeleportListener,this)

    }

    override fun onDisable() {

        var count = 0

        ModHandler.getAllModModes().filter{it.enabled}.forEach{

            val player = Bukkit.getServer().getPlayer(it.player) ?: return@forEach

            it.setModMode(false,player)
            count++
        }

        ModSuite.instance.logger.info("Disabled mod mode for $count staff members.")
    }
    
    fun isWorldEditEnabled():Boolean {
        return this.worldEditPlugin?.isEnabled == true
    }

    companion object {

        lateinit var instance: ModSuite

    }

}