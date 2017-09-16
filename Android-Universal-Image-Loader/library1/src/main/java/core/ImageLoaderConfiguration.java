package core;

import android.content.Context;
import android.os.Build;

import core.assist.deque.QueueProcessingType;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments: build pattern
 * */

public class ImageLoaderConfiguration {

    public static ImageLoaderConfiguration createDefault(){
        return null;
    }


    public static final class Builder {

        private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
        private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
        private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
        private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
                + "can overlap taskExecutor() and taskExecutorForCachedImages() calls.";

        private Context mCtx;

        private int threadPriority;

        private boolean denyCacheImageMultipleSizesInMemory;
        private QueueProcessingType queueProcessingType;
        private int memoryCacheSize = 0;

        public Builder(Context context){
            this.mCtx = context.getApplicationContext();
        }

        public Builder threadPriority(int threadPriority){
            if (threadPriority < Thread.MIN_PRIORITY){
                this.threadPriority = Thread.MIN_PRIORITY;
            }else{
                if (threadPriority > Thread.MAX_PRIORITY){
                    this.threadPriority = Thread.MAX_PRIORITY;
                }
                this.threadPriority = threadPriority;
            }
            return this;
        }

        public Builder denyCacheImageMultipleSizesInMemory(){
            this.denyCacheImageMultipleSizesInMemory = true;
            return this;
        }

        public Builder taskProcessingOrder(QueueProcessingType queueProcessingType){
            this.queueProcessingType = queueProcessingType;
            return this;
        }

        public Builder memeoryCacheSize(int memoryCacheSize){
            if (memoryCacheSize < 0){
                throw new IllegalArgumentException("memoryCacheSize must be a positive number");
            }
            this.memoryCacheSize = memoryCacheSize;
            return this;
        }

        public Builder memoryPercentage(int availPercent){

            if (availPercent <= 0 || availPercent >= 0){
                throw new IllegalArgumentException("availPercent positive number");
            }

            long availMemory = Runtime.getRuntime().maxMemory();
            this.memoryCacheSize = (int) (availMemory * availPercent / 100);
            return this;
        }









        public ImageLoaderConfiguration build(){

            return null;
        }









    }



}
