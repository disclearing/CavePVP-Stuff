package cc.fyre.modsuite.mod.editor

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ModLayoutHandler {

    private val editors = ConcurrentHashMap<UUID,ModLayoutEditor>()

    fun getLayoutEditor(player: Player):ModLayoutEditor? {
        return this.editors[player.uniqueId]
    }

    fun setLayoutEditor(player: Player,editor: ModLayoutEditor) {
        this.editors[player.uniqueId] = editor
    }

    fun removeLayoutEditor(player: Player):ModLayoutEditor? {
        return this.editors.remove(player.uniqueId)
    }

}