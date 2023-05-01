package net.frozenorb.foxtrot.brewer

import net.frozenorb.foxtrot.Foxtrot
import net.frozenorb.foxtrot.brewer.listener.FancyBrewerListener
import net.frozenorb.foxtrot.brewer.listener.FancyBrewerResourceListener
import net.frozenorb.foxtrot.brewer.service.FancyBrewerTickService
import org.bukkit.event.Listener

class FancyBrewerModule {

    fun init(core: Foxtrot) {
        core.server.scheduler.runTaskLater(Foxtrot.instance,{

            FancyBrewerRepository.findAll().forEach{FancyBrewerHandler.addBrewer(it)}
            core.server.scheduler.runTaskTimerAsynchronously(core,FancyBrewerTickService,20L,20L)
        },20L)

        for (listener in this.getListeners()) {
            core.server.pluginManager.registerEvents(listener,core)
        }
    }

    fun onSave(core: Foxtrot) {
        FancyBrewerRepository.deleteAll()
        FancyBrewerRepository.saveAll()
    }

    fun shutdown(core: Foxtrot) {
        FancyBrewerRepository.deleteAll()
        FancyBrewerRepository.saveAll()
    }

    fun getListeners(): List<Listener> {
        return listOf(
            FancyBrewerListener,
            FancyBrewerResourceListener
        )
    }

}