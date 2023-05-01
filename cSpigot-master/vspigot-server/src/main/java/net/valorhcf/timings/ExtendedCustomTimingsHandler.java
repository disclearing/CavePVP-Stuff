package net.valorhcf.timings;

import net.minecraft.server.MinecraftServer;
import net.valorhcf.ThreadingManager;
import net.valorhcf.ThreadingManager.TaskQueueWorker;
import org.bukkit.Bukkit;
import org.spigotmc.CustomTimingsHandler;
import org.spigotmc.SpigotConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExtendedCustomTimingsHandler extends CustomTimingsHandler {

    private static File path = new File("LagSpikeLog");
    private static File file;
    private static TaskQueueWorker taskQueue;

    public ExtendedCustomTimingsHandler(String name)
    {
        this( name, null );
    }

    public ExtendedCustomTimingsHandler(String name, CustomTimingsHandler parent)
    {
        super(name, parent);
    }

    public static void tick() {
        if(Bukkit.getPluginManager().useTimings() && SpigotConfig.lagSpikeLoggerEnabled) {
            for(CustomTimingsHandler handler: HANDLERS) {
                if(handler.getCurrentTickTotal() > SpigotConfig.lagSpikeLoggerTickLimitNanos) {
                    dumpTimings(HANDLERS);
                    break;
                }
            }
        }
        CustomTimingsHandler.tick();
    }

    private static void dumpTimings(Queue<CustomTimingsHandler> handlers) {
        ArrayList<LogEntry> list = new ArrayList<LogEntry>(handlers.size());
        for(CustomTimingsHandler handler: handlers) {
            if(handler.getCurrentTickTotal() > 1000000L) { // greater than 1ms
                list.add(new LogEntry(handler));
            }
        }
        if(taskQueue == null) {
            taskQueue = ThreadingManager.createTaskQueue();
        }
        taskQueue.queueTask(new LogDump(list));
    }

    private static class LogEntry implements Comparable<LogEntry> {
        public String name;
        public long total;

        public LogEntry(CustomTimingsHandler handler) {
            this.name = handler.getName();
            this.total = handler.getCurrentTickTotal();
        }

        @Override
        public int compareTo(LogEntry other) {
            return (int) (other.total - this.total);
        }

        public String getFormatedTotal() {
            double a = (double) this.total / 1000000.0D;
            return String.format("%.2f", a);
        }
    }

    private static class LogDump implements Runnable {

        private List<LogEntry> list;
        private long serverTick;

        public LogDump(List<LogEntry> list) {
            this.list = list;
            this.serverTick = MinecraftServer.currentTick;
        }

        @Override
        public void run() {
            if(list.isEmpty()) {
                return;
            }
            Collections.sort(this.list);
            if(!path.exists()) {
                path.mkdirs();
            }
            SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd_HH-mm-ss");
            String formatedDate = df.format(new Date());
            if(file == null) {
                file = new File(path, "LagSpikeLog_" + formatedDate + ".txt");
            }
            BufferedWriter writer = null;
            try {
                writer =  new BufferedWriter(new FileWriter(file, true), 8 * 1024);
                writer.newLine();
                writer.write("Time stamp: ");
                writer.write(formatedDate);
                writer.write("   Server tick: ");
                writer.write(String.valueOf(this.serverTick));
                writer.newLine();
                writer.newLine();
                for(LogEntry e: list) {
                    writer.write(e.getFormatedTotal());
                    writer.write(" -- ");
                    writer.write(e.name);
                    writer.newLine();
                }
                writer.newLine();
                writer.write("============================================================");
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.out.println("Failed to dump the timings of a lag spike: " + e.toString());
                e.printStackTrace();
            } finally {
                if(writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {}
                }
            }
        }
    }
}
