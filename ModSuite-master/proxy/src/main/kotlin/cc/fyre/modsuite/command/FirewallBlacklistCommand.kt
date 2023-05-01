package cc.fyre.modsuite.command

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

object FirewallBlacklistCommand : Command("firewall","modsuite.command.firewall.blacklist","fw"){

    override fun execute(sender: CommandSender,args: Array<out String>) {

        if (args.size < 2) {
            sender.sendMessage(TextComponent("${ChatColor.RED}Usage: /firewall blacklist <ip>"))
            return
        }

        if (args[0].lowercase() != "blacklist") {
            sender.sendMessage(TextComponent("${ChatColor.RED}Usage: /firewall blacklist <ip>"))
            return
        }

        Runtime.getRuntime().exec("ufw deny from ${args[1]} to any port 25565")
        sender.sendMessage(TextComponent("${ChatColor.GRAY}[${ChatColor.BLUE}${ChatColor.BOLD}Firewall${ChatColor.GRAY}] Restricted IP ${ChatColor.AQUA}${args[1]}${ChatColor.GRAY} from pinging."))
    }

}