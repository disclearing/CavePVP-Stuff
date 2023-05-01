package cc.fyre.proton.command.command;

import lombok.Getter;
import org.bukkit.ChatColor;

public class CommandConfiguration {

    @Getter private String noPermissionMessage;

    @Getter private String playerOnlyCommandMessage;
    @Getter private String consoleOnlyCommandMessage;

    public CommandConfiguration(String noPermissionMessage) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes('&',noPermissionMessage);

        this.playerOnlyCommandMessage = ChatColor.RED + "This is a player-only command. It can only be used from in-game.";
        this.consoleOnlyCommandMessage = ChatColor.RED + "This is a console-only utility command. It cannot be used from in-game.";
    }

    public void setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes('&',noPermissionMessage);
    }

    public void setPlayerOnlyCommandMessage(String playerOnlyCommandMessage) {
        this.playerOnlyCommandMessage = ChatColor.translateAlternateColorCodes('&',playerOnlyCommandMessage);
    }

    public void setConsoleOnlyCommandMessage(String consoleOnlyCommandMessage) {
        this.consoleOnlyCommandMessage = ChatColor.translateAlternateColorCodes('&',consoleOnlyCommandMessage);
    }


}
