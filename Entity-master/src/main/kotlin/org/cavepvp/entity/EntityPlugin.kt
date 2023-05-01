package org.cavepvp.entity

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.cavepvp.entity.command.EntityCommand
import org.cavepvp.entity.listener.EntityListener
import org.cavepvp.entity.listener.EntityPacketListener
import org.cavepvp.entity.thread.EntityThread
import org.cavepvp.entity.type.npc.listener.NPCVisibilityListener

class EntityPlugin : JavaPlugin() {

    override fun onEnable() {
        instance = this

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs()
        }

        EntityHandler.onLoad()

        Bukkit.getServer().pluginManager.registerEvents(EntityListener,this)
        Bukkit.getServer().pluginManager.registerEvents(EntityPacketListener,this)
        Bukkit.getServer().pluginManager.registerEvents(NPCVisibilityListener,this)

        println("345345345")
        getCommand("entity").executor = EntityCommand()
        println("DJsidaijd12342423")

        EntityThread.start()
    }

    override fun onDisable() {
        EntityHandler.onDisable()
    }

    companion object {

        lateinit var instance: EntityPlugin

    }

}