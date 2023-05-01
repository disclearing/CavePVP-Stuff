package cc.fyre.modsuite.mod.visibility

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.ModVisibility
import cc.fyre.proton.visibility.action.VisibilityAction
import cc.fyre.proton.visibility.provider.VisibilityProvider
import org.bukkit.entity.Player

object ModVisibilityAdapter : VisibilityProvider {

    override fun getAction(player: Player,viewer: Player): VisibilityAction {


        val modMode = ModHandler.getModModeById(viewer.uniqueId)
        val targetModMode = ModHandler.getModModeById(player.uniqueId) ?: return VisibilityAction.NEUTRAL

        if (modMode == null && targetModMode.visibility != ModVisibility.VISIBLE) {
            return VisibilityAction.HIDE
        }

        return when (targetModMode.visibility) {
            ModVisibility.VISIBLE -> VisibilityAction.NEUTRAL
            ModVisibility.INVISIBLE -> if (modMode?.visibility != ModVisibility.VISIBLE) VisibilityAction.NEUTRAL else VisibilityAction.HIDE
            ModVisibility.LOWER_STAFF -> {

                if (modMode?.visibility == ModVisibility.VISIBLE) {
                    return VisibilityAction.HIDE
                } else {

                    if (player.isOp && !viewer.isOp) {
                        return VisibilityAction.HIDE
                    }

                    return VisibilityAction.NEUTRAL
                }

            }
        }
    }

}