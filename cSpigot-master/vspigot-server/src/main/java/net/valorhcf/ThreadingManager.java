package net.valorhcf;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.valorhcf.command.WorldStatsCommand.WorldStatsTask;
import net.valorhcf.pathsearch.PathSearchThrottlerThread;
import net.valorhcf.pathsearch.jobs.PathSearchJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.SpigotConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadingManager {

    private final Logger log = LogManager.getLogger();
    private static ThreadingManager instance;
    private PathSearchThrottlerThread pathSearchThrottler;
    private ScheduledExecutorService timerService = Executors.newScheduledThreadPool(1, new NamePriorityThreadFactory(Thread.NORM_PRIORITY + 2, "mSpigot_TimerService"));
    private TickCounter tickCounter = new TickCounter();
    private NamePriorityThreadFactory cachedThreadPoolFactory;
    private ExecutorService cachedThreadPool;

    private ScheduledFuture<Object> tickTimerTask;
    private TickTimer tickTimerObject;
    private static int timerDelay = 45;

    private TaskQueueWorker nbtFiles;
    private TaskQueueWorker headConversions;

    public ThreadingManager() {
        instance = this;
        this.pathSearchThrottler = new PathSearchThrottlerThread(2);
        this.timerService.scheduleAtFixedRate(this.tickCounter, 1, 1000, TimeUnit.MILLISECONDS);
        this.tickTimerObject = new TickTimer();
        this.cachedThreadPoolFactory = new NamePriorityThreadFactory(Thread.currentThread().getPriority() - 1, "mSpigot_Async-Executor").setLogThreads(true).setDaemon(true);
        this.cachedThreadPool = Executors.newCachedThreadPool(this.cachedThreadPoolFactory);
        this.nbtFiles = this.createTaskQueueWorker();
        this.headConversions = this.createTaskQueueWorker();
    }

    public void shutdown() {
        this.pathSearchThrottler.shutdown();
        this.timerService.shutdown();
        this.cachedThreadPool.shutdown();
        while((this.nbtFiles.isActive()) && !this.cachedThreadPool.isTerminated()) {
            try {
                this.cachedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
                log.warn("mSpigot is still waiting for NBT files to be written to disk. " + this.nbtFiles.getTaskCount() + " to go...");
            } catch(InterruptedException e) {}
        }
        if(!this.cachedThreadPool.isTerminated()) {
            this.cachedThreadPool.shutdownNow();
            try {
                this.cachedThreadPool.awaitTermination(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(SpigotConfig.logRemainingAsyncThreadsDuringShutdown && this.cachedThreadPoolFactory.getActiveCount() > 0) {
                log.warn("mSpigot is still waiting for " + this.cachedThreadPoolFactory.getActiveCount() + " async threads to finish.");
                Queue<WeakReference<Thread>> queue = this.cachedThreadPoolFactory.getThreadList();
                Iterator<WeakReference<Thread>> iter = null;
                if(queue != null) {
                    System.out.println("== List of async threads that did not terminate on their own == ");
                    iter = queue.iterator();
                }
                while(iter != null && iter.hasNext()) {
                    WeakReference<Thread> ref = iter.next();
                    Thread t = ref.get();
                    if(t == null) {
                        iter.remove();
                    } else if (t.isAlive()) {
                        StackTraceElement[] e = t.getStackTrace();
                        System.out.println(t.getName() + " - " + t.getState().toString());
                        for(StackTraceElement et: e) {
                            System.out.println(et.toString());
                        }
                        System.out.println("========================== ");
                    }
                }
            }
        }
    }

    public static void saveNBTPlayerDataStatic(PlayerDataSaveJob savejob) {
        instance.nbtFiles.queueTask(savejob);
    }

    public static void saveNBTFileStatic(NBTTagCompound compound, File file) {
        instance.saveNBTFile(compound, file);
    }

    public void saveNBTFile(NBTTagCompound compound, File file) {
        this.nbtFiles.queueTask(new NBTFileSaver(compound, file));
    }

    private class NBTFileSaver implements Runnable {

        private NBTTagCompound compound;
        private File file;

        public NBTFileSaver(NBTTagCompound compound, File file) {
            this.compound = compound;
            this.file = file;
        }

        public void run() {
            FileOutputStream fileoutputstream = null;
            try {
                fileoutputstream = new FileOutputStream(this.file);
                NBTCompressedStreamTools.a(this.compound, (OutputStream) fileoutputstream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(fileoutputstream != null) {
                    try {
                        fileoutputstream.close();
                    } catch (IOException e) {}
                }
            }
            this.compound = null;
            this.file = null;
        }
    }

    public static boolean queuePathSearch(PathSearchJob pathSearchJob) {
        return instance.pathSearchThrottler.queuePathSearch(pathSearchJob);
    }

    public class TickCounter implements Runnable {

        private ArrayDeque<Integer> ticksPerSecond;
        private AtomicInteger ticksCounter;

        public TickCounter() {
            this.ticksPerSecond = new ArrayDeque<Integer>();
            this.ticksCounter = new AtomicInteger(0);
        }

        @Override
        public void run() {
            int lastCount = this.ticksCounter.getAndSet(0);
            synchronized(this.ticksPerSecond) {
                this.ticksPerSecond.addLast(lastCount);
                if(this.ticksPerSecond.size() > 30) {
                    this.ticksPerSecond.removeFirst();
                }
            }
        }

        public void increaseTickCounter() {
            this.ticksCounter.incrementAndGet();
        }

        public Integer[] getTicksPerSecond() {
            synchronized(this.ticksPerSecond) {
                return this.ticksPerSecond.toArray(new Integer[0]);
            }
        }
    }

    public static TickCounter getTickCounter() {
        return instance.tickCounter;
    }

    public static void startTickTimerTask() {
        instance.tickTimerTask = instance.timerService.schedule(instance.tickTimerObject, timerDelay, TimeUnit.MILLISECONDS);
    }

    public static void cancelTimerTask(float tickTime) {
        if(checkTickTime(tickTime) && instance.tickTimerTask.cancel(false)) {
            instance.tickTimerObject.tickFinishedEarly();
        }
    }

    private static boolean checkTickTime(float tickTime) {
        if(tickTime > 45.0F) {
            if(timerDelay > 40) {
                timerDelay--;
            }
        } else {
            if(timerDelay < 45) {
                timerDelay++;
            }
            return tickTime < 40.0F;
        }
        return false;
    }

    private class TickTimer implements Callable<Object> {
        public Object call() {
            this.tickIsGoingToFinishLate();
            return null;
        }

        public void tickIsGoingToFinishLate() {
        }

        public void tickFinishedEarly() {
        }
    }

    public static void addWorldStatsTask(WorldStatsTask task) {
        instance.timerService.schedule(task, 2, TimeUnit.SECONDS);
    }

    public static void execute(Runnable runnable) {
        instance.cachedThreadPool.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return instance.cachedThreadPool.submit(runnable);
    }

    public static Future<?> submit(Callable<?> callable) {
        return instance.cachedThreadPool.submit(callable);
    }

    public static void queueHeadConversion(Runnable runnable) {
        instance.headConversions.queueTask(runnable);
    }

    public static TaskQueueWorker createTaskQueue() {
        return instance.createTaskQueueWorker();
    }

    public TaskQueueWorker createTaskQueueWorker() {
        return new TaskQueueWorker(this.cachedThreadPool);
    }

    public class TaskQueueWorker implements Runnable {

        private ConcurrentLinkedDeque<Runnable> taskQueue = new ConcurrentLinkedDeque<Runnable>();
        private ExecutorService service;
        private volatile boolean isActive = false;

        public TaskQueueWorker(ExecutorService service) {
            this.service = service;
        }

        @Override
        public void run() {
            Runnable task = null;
            while(this.isActive = ((task = this.taskQueue.pollFirst()) != null)) {
                try {
                    task.run();
                } catch (Exception e) {
                    log.error("Thread " + Thread.currentThread().getName() + " encountered an exception: " + e.getMessage(), e);
                }
            }
        }

        public void queueTask(Runnable runnable) {
            this.taskQueue.addLast(runnable);
            if(!this.isActive) {
                this.isActive = true;
                this.service.execute(this);
            }
        }

        public boolean isActive() {
            if(!this.isActive && !this.taskQueue.isEmpty()) {
                this.isActive = true;
                this.service.execute(this);
            }
            return this.isActive;
        }

        public int getTaskCount() {
            int count = this.taskQueue.size();
            if(this.isActive) {
                count++;
            }
            return count;
        }
    }

    public static Executor getCommonThreadPool() {
        return instance.cachedThreadPool;
    }
}
