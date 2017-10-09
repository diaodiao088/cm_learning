package core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cache.disc.DiskCache;
import cache.disc.UnlimitedDiskCache;
import cache.disc.name.FileNameGenerator;
import cache.disc.name.MD5FileNameGenerator;
import cache.disc.name.ext.LruDiskCache;
import cache.memory.MemoryCache;
import cache.memory.impl.LimitedLruCacheImpl;
import core.assist.deque.LIFOLinkedBlockingDeque;
import core.assist.deque.LinkedBlockingDeque;
import core.assist.deque.QueueProcessingType;
import utils.StroageUtils;

/**
 * Created by zhangdan on 2017/9/19.
 *
 * comments:
 */

public class DefaultConfigurationFactory {

    public static Executor createExecutor(int threadPoolSize , int threadPriority , QueueProcessingType queneType){
        boolean lifo = queneType == QueueProcessingType.LIFO;
        BlockingDeque<Runnable> taskQuene = lifo ? new LIFOLinkedBlockingDeque<Runnable>() : new LinkedBlockingDeque<Runnable>();
        return new ThreadPoolExecutor(threadPoolSize , threadPoolSize , 0L , TimeUnit.MILLISECONDS ,
                taskQuene , createThreadFactory(threadPriority , "uil-pool-"));
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String s) {
        return new DefaultThreadFactory(s , threadPriority);
    }

    public static Executor createTaskDistributor(){
        return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY , "uil-pool-d-"));
    }

    public static DiskCache createDiskCache(Context context , FileNameGenerator diskCacheFileNameGenerator ,
                                            long diskCacheSize , int diskCacheFileCount){
        File reserveCacheDir = createReserveDiskCacheDir(context);
        if (diskCacheSize > 0 || diskCacheFileCount > 0) {
            File individualCacheDir = StroageUtils.getIndividualCacheDirectory(context);
            try {
                return new LruDiskCache(individualCacheDir, reserveCacheDir, diskCacheFileNameGenerator, diskCacheSize,
                        diskCacheFileCount);
            } catch (IOException e) {
                // continue and create unlimited cache
            }
        }

        File cacheDir = StroageUtils.getCacheDirectory(context);
        return new UnlimitedDiskCache(cacheDir, reserveCacheDir, diskCacheFileNameGenerator);
    }

    private static File createReserveDiskCacheDir(Context context){
        File appCacheDir = StroageUtils.getCacheDirectory(context ,false);
        File invalidDir = new File(appCacheDir , "uil-images");
        if (invalidDir.exists() || invalidDir.mkdir()){
            appCacheDir = invalidDir;
        }
        return appCacheDir;
    }

    public static MemoryCache createMemoryCache(Context context , int memoryCacheSize){

        if (memoryCacheSize == 0){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int memoryclass = am.getMemoryClass();
            if (hasHoneyComb() && isLargeHeap(context)){
                memoryclass = getLargeMemoryClass(am);
            }
            memoryCacheSize = 1024 * 1024 * memoryclass / 8;
        }
        return new LimitedLruCacheImpl(memoryCacheSize);
    }

    public static boolean hasHoneyComb(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isLargeHeap(Context context){
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }

    public static FileNameGenerator createFileNameGenerator(){
        return new MD5FileNameGenerator();
    }

    public static class DefaultThreadFactory implements  ThreadFactory{

        private static final AtomicInteger pollNumber = new AtomicInteger(1);
        private final ThreadGroup threadGroup;
        private final AtomicInteger threadNum = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        public DefaultThreadFactory( String namePrefix , int threadPriority){
            this.threadGroup = Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix + pollNumber.getAndIncrement() + "-thread-";;
            this.threadPriority = threadPriority;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(threadGroup , r , namePrefix + threadNum.getAndIncrement() , 0);
            if (thread.isDaemon()){
                thread.setDaemon(false);
            }
            thread.setPriority(threadPriority);
            return thread;
        }
    }

}
