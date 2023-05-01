package net.frozenorb.foxtrot.brewer.service

import net.frozenorb.foxtrot.brewer.FancyBrewerHandler

object FancyBrewerTickService : Runnable {

    override fun run() {
        FancyBrewerHandler.getAllBrewers().forEach{it.tick()}
    }

}