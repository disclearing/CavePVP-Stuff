package cc.fyre.modsuite.mod.item.menu

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.ModVisibility
import cc.fyre.modsuite.mod.item.menu.button.OnlineStaffButton
import cc.fyre.neutron.Neutron
import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.pagination.PaginatedMenu
import org.bukkit.entity.Player

class OnlineStaffMenu : PaginatedMenu() {

    companion object {
        private const val TITLE = "Online Staff"
    }

    override fun getPrePaginatedTitle(p0: Player?): String {
        return TITLE
    }

    override fun getAllPagesButtons(p0: Player): MutableMap<Int, Button> {
        return ModHandler.getAllOnlineStaffMembers()
            .filter{it.isOnline}.filter{ ModHandler.getModModeById(p0.uniqueId)!!.visibility != ModVisibility.LOWER_STAFF }
            .sortedByDescending{Neutron.getInstance().profileHandler.fromUuid(it.uniqueId)?.activeRank?.weight?.get() ?: 0}
            .withIndex()
            .associate{it.index to OnlineStaffButton(it.value)}
            .toMutableMap()
    }

}