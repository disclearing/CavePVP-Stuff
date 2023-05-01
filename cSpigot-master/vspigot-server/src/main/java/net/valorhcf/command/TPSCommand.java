package net.valorhcf.command;

import net.valorhcf.ThreadingManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPSCommand extends Command {

    private int[] steps = new int[] { 20, 19, 18, 14, 9, 0};
    private String[] notes = new String[] { " > 20", " = 20", " = 19", ">= 15", ">= 10", ">=  1" };
    private ChatColor[] colors = new ChatColor[] {ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED };
    private StringBuilder[] builders;

    public TPSCommand(String name) {
        super(name);
        this.usageMessage = "/" + name;
        this.description = "Displays the servers tick rate of the last 30 seconds";
        this.setPermission( "valor.command.tpsgraph" );
        this.builders = new StringBuilder[this.steps.length];
        for(int i = 0; i < this.builders.length; i++) {
            this.builders[i] = new StringBuilder();
        }
    }

    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if(!testPermission(sender)) { return true; }
        Integer[] array = ThreadingManager.getTickCounter().getTicksPerSecond();
        boolean runByPlayer = (sender instanceof Player);
        if(array.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "TPS statistic: " + ChatColor.RESET + "No data available yet. Try again later");
            return true;
        }
        for(int i = 0; i < this.builders.length; i++) {
            this.builders[i].delete(0, this.builders[i].length());
        }

        int start = array.length - 30;
        for(int i = start; i < array.length; i++) {
            Integer k;
            if(i < 0) {
                k = 0;
            } else {
                k = array[i];
            }
            for(int j = 0; j < this.steps.length; j++) {
                if(k > this.steps[j]) {
                    if(runByPlayer) {
                        this.builders[j].append(this.colors[j]);
                        this.builders[j].append('\u2B1B');
                        this.builders[j].append(ChatColor.RESET);
                    } else {
                        this.builders[j].append("#");
                    }
                } else {
                    if(runByPlayer) {
                        this.builders[j].append(ChatColor.BLACK);
                        this.builders[j].append('\u2B1C');
                        this.builders[j].append(ChatColor.RESET);
                    } else {
                        this.builders[j].append("_");
                    }
                }
            }
        }
        ChatColor current = ChatColor.RED;
        Integer last = array[array.length - 1];
        for(int i = 0; i < this.steps.length; i++) {
            if(this.steps[i] < last) {
                current = this.colors[i];
                break;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "  TPS statistic (last 30 seconds)  -  Current TPS: " + current + last);
        for(int i = 0; i < this.builders.length; i++) {
            this.builders[i].append("  " + this.notes[i]);
            sender.sendMessage(this.builders[i].toString());
        }
        return true;
    }
}
