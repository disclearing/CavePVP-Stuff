package net.valorhcf;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamePriorityThreadFactory implements ThreadFactory {
    private int priority;
    private int idCounter = 0;
    private String name = "mSpigotThread";
    private boolean isDaemon = false;
    private Queue<WeakReference<Thread>> createdThreadList;

    public NamePriorityThreadFactory(int priority) {
        this.priority = Math.min(Math.max(priority, Thread.MIN_PRIORITY), Thread.MAX_PRIORITY);
    }

    public NamePriorityThreadFactory(int priority, String name) {
        this(priority);
        this.name = name;
    }

    public NamePriorityThreadFactory(String name) {
        this(Thread.NORM_PRIORITY);
        this.name = name;
    }

    public NamePriorityThreadFactory setDaemon(boolean daemon) {
        this.isDaemon = daemon;
        return this;
    }

    public NamePriorityThreadFactory setLogThreads(boolean log) {
        if(log) {
            if(this.createdThreadList == null) {
                this.createdThreadList = new ConcurrentLinkedQueue<WeakReference<Thread>>();
            }
        } else {
            this.createdThreadList = null;
        }
        return this;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setPriority(this.priority);
        thread.setName(this.name + "-" + String.valueOf(idCounter));
        thread.setDaemon(this.isDaemon);
        if(this.createdThreadList != null) {
            this.createdThreadList.add(new WeakReference<Thread>(thread));
        }
        idCounter++;
        return thread;
    }

    public int getActiveCount() {
        if(this.createdThreadList != null) {
            Iterator<WeakReference<Thread>> iter = this.createdThreadList.iterator();
            int count = 0;
            while(iter.hasNext()) {
                WeakReference<Thread> ref = iter.next();
                Thread t = ref.get();
                if(t == null) {
                    iter.remove();
                } else if(t.isAlive()) {
                    count++;
                }
            }
            return count;
        }
        return -1;
    }

    public Queue<WeakReference<Thread>> getThreadList() {
        return this.createdThreadList;
    }
}
