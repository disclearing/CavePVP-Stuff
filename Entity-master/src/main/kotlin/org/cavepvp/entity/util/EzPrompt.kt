package org.cavepvp.entity.util

import cc.fyre.proton.Proton
import cc.fyre.proton.menu.Menu
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player

open class EzPrompt<T>(protected val lambda: (T) -> Unit) : StringPrompt() {

    protected var text = "${ChatColor.GREEN}Please input a value."
    private var limit = -1
    private var regex: Regex? = null
    private var restoreMenu = true
    private var failureLambda: ((String) -> Unit)? = null

    fun withText(text: String): EzPrompt<T> {
        this.text =  text
        return this
    }

    fun withRegex(regex: Regex): EzPrompt<T> {
        this.regex = regex
        return this
    }

    fun withLimit(limit: Int): EzPrompt<T> {
        this.limit = limit
        return this
    }

    fun withRestoreMenu(value: Boolean): EzPrompt<T> {
        this.restoreMenu = value
        return this
    }

    fun onFailure(use: (String) -> Unit): EzPrompt<T> {
        this.failureLambda = use
        return this
    }

    override fun getPromptText(context: ConversationContext): String {
        return this.text
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {

        val player = context.forWhom as Player

        val menu = Menu.getCurrentlyOpenedMenus()[player.uniqueId]

        if (menu != null) {
            player.closeInventory()
        }

        if (this.limit != -1 && input.length > this.limit) {
            player.sendRawMessage("${ChatColor.RED}Input text is too long! (${input.length} > ${this.limit})")
            this.failureLambda?.invoke(input)
            return Prompt.END_OF_CONVERSATION
        }

        if (this.regex != null && !input.matches(this.regex!!)) {
            player.sendRawMessage("${ChatColor.RED}Input text does not match regex pattern ${this.regex!!.pattern}.")
            this.failureLambda?.invoke(input)
            return Prompt.END_OF_CONVERSATION
        }

        try {
            this.lambda.invoke(input as T)
        } catch (ex: Exception) {
            player.sendRawMessage("${ChatColor.RED}Failed to handle input: ${ChatColor.WHITE}${input}")
            this.failureLambda?.invoke(input)
        }

        if (menu != null && this.restoreMenu) {
            menu.openMenu(player)
        }

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        PlayerUtil.startPrompt(player,this)
    }

    companion object {

        val NAME_PROMPT = "${ChatColor.GREEN}Please input a name. ${ChatColor.GRAY}(Colors supported, limit of 48 characters)"
        val IDENTIFIER_PROMPT = "${ChatColor.GREEN}Please input a unique ID. ${ChatColor.GRAY}(Limit of 16 characters)"
        val IDENTIFIER_REGEX = "[a-zA-Z_\\-0-9]*".toRegex()

    }

}