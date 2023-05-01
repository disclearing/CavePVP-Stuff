package org.cavepvp.entity.menu.npc

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.Menu
import cc.fyre.proton.menu.buttons.BackButton
import org.bukkit.entity.Player
import org.cavepvp.entity.menu.EntityMenu
import org.cavepvp.entity.menu.button.EntityMoveButton
import org.cavepvp.entity.menu.hologram.HologramButton
import org.cavepvp.entity.menu.npc.button.NPCAnimationButton
import org.cavepvp.entity.menu.npc.button.NPCCommandsButton
import org.cavepvp.entity.menu.npc.button.NPCEquipmentButton
import org.cavepvp.entity.menu.npc.button.NPCSkinButton
import org.cavepvp.entity.type.npc.NPC

class NPCMenu(private val npc: NPC) : Menu() {
    
    override fun isPlaceholder(): Boolean {
        return true;
    }

    override fun size(player: Player?): Int {
        return 3*9;
    }

    override fun getTitle(player: Player): String {
        return this.npc.name
    }

    override fun getButtons(player: Player): MutableMap<Int,Button> {
        return mapOf(
            10 to NPCSkinButton(this.npc),
            11 to NPCEquipmentButton(this.npc),
            12 to NPCAnimationButton(this.npc),
            13 to NPCCommandsButton(this,this.npc),
            14 to HologramButton(this.npc.hologram),
            15 to EntityMoveButton(this.npc),
            16 to BackButton(EntityMenu())
        ).plus(this.npc.getEditorButtons()).toMutableMap()
    }

}