package net.valorhcf.pathsearch;

import net.valorhcf.NamePriorityThreadFactory;
import net.valorhcf.pathsearch.jobs.PathSearchJob;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class PathSearchThrottlerThread extends ThreadPoolExecutor {

    private int queueLimit;
    private LinkedHashMap<PathSearchJob, PathSearchJob> filter;
    private HashSet<Integer> activeSearchHashes;
    private static PathSearchThrottlerThread instance;

    public PathSearchThrottlerThread(int poolSize) {
        super(poolSize, poolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new NamePriorityThreadFactory(Thread.MIN_PRIORITY, "mSpigot_PathFinder"));
        instance = this;
        adjustPoolSize(poolSize);
        this.filter = new LinkedHashMap<PathSearchJob, PathSearchJob>();
        this.activeSearchHashes = new HashSet<Integer>();
    }

    public boolean queuePathSearch(PathSearchJob newJob) {
        boolean jobHasBeenQueued = false;
        if(newJob != null) {
            synchronized(this.filter) {
                if(this.filter.containsKey(newJob) || this.filter.size() < 1000) {
                    jobHasBeenQueued = true;
                    PathSearchJob previousJob = this.filter.put(newJob, newJob);
                    if(previousJob != null) {
                        previousJob.cancel();
                    }
                }
            }
            if(!jobHasBeenQueued) {
                newJob.cancel();
            }
        }
        PathSearchJob jobToExecute = null;
        synchronized(this.filter) {
            Iterator<Entry<PathSearchJob, PathSearchJob>> iter = this.filter.entrySet().iterator();
            while(iter.hasNext() && this.getQueue().size() < this.queueLimit) {
                jobToExecute = iter.next().getValue();
                if(!this.activeSearchHashes.contains(jobToExecute.getSearchHash())) {
                    iter.remove();
                    if(jobToExecute != null) {
                        this.activeSearchHashes.add(jobToExecute.getSearchHash());
                        this.submit(jobToExecute);
                    }
                    if(newJob != null) {
                        break;
                    }
                }
            }
        }
        return jobHasBeenQueued;
    }

    @Override
    public void shutdown() {
        this.getQueue().clear();
        super.shutdown();
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        if(runnable instanceof FutureTask) {
            FutureTask<PathSearchJob> task = (FutureTask<PathSearchJob>) runnable;
            PathSearchJob job = null;
            try {
                job = task.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
            if(job != null) {
                synchronized(this.filter) {
                    this.activeSearchHashes.remove(job.getSearchHash());
                }
            }
        }
        this.queuePathSearch(null);
    }

    public static void adjustPoolSize(int size) {
        if(instance != null) {
            if(size > instance.getMaximumPoolSize()) {
                instance.setMaximumPoolSize(size);
                instance.setCorePoolSize(size);
            } else if(size < instance.getMaximumPoolSize()) {
                instance.setCorePoolSize(size);
                instance.setMaximumPoolSize(size);
            }
            instance.queueLimit = size * 8;
        }
    }
}
