package cc.fyre.modsuite.mod.scheduler

import cc.fyre.modsuite.mod.ModHandler
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

object ModModeRefreshScheduler : Runnable {

    override fun run() {

        ModHandler.setOnlineStaff(ModHandler.getAllOnlineStaffMembers().map{it.uniqueId}.toMutableList())

        val items = ModHandler.getAllModModeItems().filter{it.isRefresh()}

        for (modMode in ModHandler.getAllModModes().filter{it.enabled}) {

            val player = Bukkit.getServer().getPlayer(modMode.player) ?: continue

            modMode.sendActionBar(player)

            val layout = modMode.getLayout(player)

            for (modModeItem in items) {

                if (!layout.isItemEnabled(modModeItem.getKey())) {
                    continue
                }

                val slot = modModeItem.getSlot(player)
                val current = player.inventory.getItem(slot) ?: continue
                val itemStack = modModeItem.getItemStack(player)

                if (!this.isDifferent(current,itemStack)) {
                    continue
                }

                player.inventory.setItem(slot,itemStack)
            }

        }

    }

    private fun isDifferent(current: ItemStack,newValue: ItemStack):Boolean {

        if (current.amount != newValue.amount) {
            return true
        }

        return current.isSimilar(newValue)
    }

}