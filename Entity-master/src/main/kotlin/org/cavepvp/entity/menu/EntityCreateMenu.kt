package org.cavepvp.entity.menu

import org.cavepvp.entity.menu.button.EntityCreateHologramButton
import cc.fyre.proton.menu.Menu
import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.buttons.BackButton
import org.bukkit.entity.Player
import org.cavepvp.entity.menu.button.EntityCreateNPCButton

object EntityCreateMenu : Menu() {

    override fun isPlaceholder(): Boolean {
        return true;
    }

    override fun size(player: Player?): Int {
        return 3*9;
    }

    override fun getTitle(player: Player): String {
        return "Create Entity"
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        return mutableMapOf(
            11 to EntityCreateNPCButton,
            13 to BackButton(EntityMenu()),
            15 to EntityCreateHologramButton
        )
    }
}