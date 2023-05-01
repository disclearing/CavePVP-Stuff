package org.cavepvp.entity.menu.hologram

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.Menu
import cc.fyre.proton.menu.buttons.BackButton
import org.bukkit.entity.Player
import org.cavepvp.entity.menu.EntityMenu
import org.cavepvp.entity.menu.button.EntityMoveButton
import org.cavepvp.entity.menu.hologram.type.HologramLinesButton
import org.cavepvp.entity.type.hologram.Hologram

class HologramMenu(private val hologram: Hologram) : Menu() {

    override fun size(player: Player?): Int {
        return 3*9
    }

    override fun isPlaceholder(): Boolean {
        return true
    }

    override fun getTitle(player: Player): String {
        return this.hologram.name
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        return mutableMapOf(
            10 to HologramLinesButton(this,this.hologram),
            11 to EntityMoveButton(this.hologram),
            16 to BackButton(EntityMenu())
        )
    }

}