package net.valorhcf.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import net.valorhcf.ThreadingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.SpigotTimings;
import org.bukkit.plugin.SimplePluginManager;
import org.spigotmc.CustomTimingsHandler;

import java.text.DecimalFormat;
import java.util.*;

public class WorldStatsCommand extends Command {

    public static WorldStatsTask task = null;

    public WorldStatsCommand(String name) {
        super(name);
        this.usageMessage = "/worldstats";
        this.description = "Displays technically important details about the active worlds";
        this.setPermission("valor.command.worldstats");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if(!this.testPermission(sender)) { return true; }

        SimplePluginManager pm = (SimplePluginManager)Bukkit.getPluginManager();
        boolean isTimingsAlreadyOn = pm.useTimings();

        if(task == null) {
            task = new WorldStatsTask(isTimingsAlreadyOn);
            ThreadingManager.addWorldStatsTask(task);
            if(!isTimingsAlreadyOn) {
                pm.useTimings(true);
                CustomTimingsHandler.reload();
            }
        }
        task.addSender(sender);
        return true;
    }

    public class WorldStatsTask implements Runnable {

        private List<CommandSender> senderList = new ArrayList<CommandSender>();
        private WorldNameComparator comparator = new WorldNameComparator();
        private DecimalFormat formater = new DecimalFormat("###0.0");

        private boolean wasTimingAlreadyOn;

        public WorldStatsTask(boolean wasTimingAlreadyOn) {
            this.wasTimingAlreadyOn = wasTimingAlreadyOn;
        }

        private void broadcastMessage(String msg) {
            for(CommandSender sender: this.senderList) {
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.stripColor(msg));
                } else {
                    sender.sendMessage(msg);
                }
            }
        }

        @Override
        public void run() {
            WorldStatsCommand.task = null;
            SimplePluginManager pm = (SimplePluginManager) Bukkit.getPluginManager();
            pm.useTimings(this.wasTimingAlreadyOn);

            StringBuilder sb = new StringBuilder();
            sb.append("[mSpigot]");
            sb.append(ChatColor.GOLD);
            sb.append(" WorldStats:");
            sb.append("\n");
            sb.append(ChatColor.GRAY);
            sb.append("Name  ");
            sb.append(ChatColor.YELLOW);
            sb.append("Chunks  ");
            sb.append(ChatColor.GREEN);
            sb.append("Entites  ");
            sb.append(ChatColor.BLUE);
            sb.append("TileEntities  ");
            sb.append(ChatColor.DARK_PURPLE);
            sb.append("Players  ");
            sb.append(ChatColor.RED);
            sb.append("TickTime");
            sb.append("\n");

            List<WorldServer> worlds = new LinkedList<WorldServer>();
            worlds.addAll(MinecraftServer.getServer().worlds);
            Collections.sort(worlds, this.comparator);
            InfoHolder overall = new InfoHolder("TOTAL");

            for(WorldServer ws: worlds) {
                InfoHolder worldDetails = this.readWorldDetails(ws);
                overall.add(worldDetails);
                sb.append(ChatColor.GRAY);
                sb.append(worldDetails.name);
                sb.append("  ");
                sb.append(ChatColor.YELLOW);
                sb.append(worldDetails.chunks);
                sb.append(ChatColor.RED);
                sb.append("(");
                sb.append(formatNanoToMilliseconds(ws.timings.doTick.getRecentAverage()));
                sb.append(")  ");
                sb.append(ChatColor.GREEN);
                sb.append(worldDetails.entities);
                sb.append(ChatColor.RED);
                sb.append("(");
                sb.append(formatNanoToMilliseconds(ws.timings.entityTick.getRecentAverage()));
                sb.append(" | ");
                sb.append(ChatColor.DARK_PURPLE);
                sb.append(formatNanoToMilliseconds(ws.timings.entityPlayerTickNormal.getRecentAverage()));
                sb.append(ChatColor.RED);
                sb.append(" | ");
                sb.append(ChatColor.DARK_PURPLE);
                sb.append(formatNanoToMilliseconds(ws.timings.entityPlayerTickOnMove.getRecentAverage()));
                sb.append(ChatColor.RED);
                sb.append(")  ");
                sb.append(ChatColor.BLUE);
                //sb.append(worldDetails.tileEntities);
                sb.append("--");
                sb.append(ChatColor.RED);
                sb.append("(");
                sb.append(formatNanoToMilliseconds(ws.timings.tileEntityTick.getRecentAverage()));
                sb.append(")  ");
                sb.append(ChatColor.DARK_PURPLE);
                sb.append(worldDetails.players);
                sb.append(ChatColor.RED);
                sb.append("(");
                sb.append(formatNanoToMilliseconds(ws.timings.tracker.getRecentAverage()));
                sb.append(")  ");
                sb.append("\n");
            }

            sb.append(ChatColor.DARK_AQUA);
            sb.append(ChatColor.BOLD);
            sb.append(overall.name);
            sb.append(" ");
            sb.append(ChatColor.RESET);
            sb.append(ChatColor.YELLOW);
            sb.append(overall.chunks);
            sb.append("  ");
            sb.append(ChatColor.GREEN);
            sb.append(overall.entities);
            sb.append("  ");
            sb.append(ChatColor.BLUE);
            //sb.append(overall.tileEntities);
            sb.append("--");
            sb.append("  ");
            sb.append(ChatColor.DARK_PURPLE);
            sb.append(overall.players);
            sb.append(ChatColor.RED);
            sb.append("  (");
            sb.append(formatNanoToMilliseconds(SpigotTimings.serverTickTimer.getRecentAverage()));
            sb.append(")");
            this.broadcastMessage(sb.toString());
        }

        public void addSender(CommandSender sender) {
            if(!this.senderList.contains(sender)) {
                this.senderList.add(sender);
            }
        }

        private String formatNanoToMilliseconds(long nano) {
            long milliPlusOne = nano / 100000L;
            double milli = (double) milliPlusOne / 10.0F;
            return this.formater.format(milli);
        }

        private class InfoHolder {
            public String name;
            public int chunks = 0;
            public int entities = 0;
            public int tileEntities = 0;
            public int players = 0;

            public InfoHolder(String name) {
                this.name = name;
            }

            public void add(InfoHolder ih) {
                this.chunks += ih.chunks;
                this.entities += ih.entities;
                this.tileEntities += ih.tileEntities;
                this.players += ih.players;
            }
        }

        private class WorldNameComparator implements Comparator<WorldServer> {
            @Override
            public int compare(WorldServer arg0, WorldServer arg1) {
                String n1 = arg0.getWorld().getName();
                String n2 = arg1.getWorld().getName();
                return n1.compareToIgnoreCase(n2);
            }
        }

        private InfoHolder readWorldDetails(WorldServer ws) {
            InfoHolder ih = new InfoHolder(ws.getWorld().getName());
            ih.chunks = ws.chunkProviderServer.chunks.size();
            ih.entities = ws.entityList.size();
            //ih.tileEntities = ws.tileEntityList.size();
            ih.players = ws.players.size();
            return ih;
        }
    }
}
