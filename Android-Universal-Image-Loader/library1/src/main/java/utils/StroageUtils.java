package utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangdan on 2017/9/21.
 *
 * comments:
 */

public class StroageUtils {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";

    private StroageUtils(){

    }

    public static File getCacheDirectory(Context context){
        return getCacheDirectory(context , true);
    }

    public static File getCacheDirectory(Context context , boolean prefExternal){

        File appCacheDir = null;
        String externalStorgaeState;

        try{
            externalStorgaeState = Environment.getExternalStorageState();
        }catch (NullPointerException e){
            externalStorgaeState = "";
        }catch (IncompatibleClassChangeError e){
            externalStorgaeState = "";
        }

        if (prefExternal && Environment.MEDIA_MOUNTED.equals(externalStorgaeState)
                && isHasPermission(context)){
            appCacheDir = getExternalCacheDir(context);
        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        if (appCacheDir == null){
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    public static File getIndividualCacheDirectory(Context context) {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
    }

    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    private static File getExternalCacheDir(Context context){

        File dataDir = new File(new File(Environment.getExternalStorageDirectory(),"Android") , "data");
        File appCacheDir = new File(new File(dataDir , context.getPackageName()) , "cache");
        if (!appCacheDir.exists()){
            if (!appCacheDir.mkdirs()){
                return null;
            }

            try{
                new File(appCacheDir , ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return appCacheDir;
    }



    private static boolean isHasPermission(Context context) {
        int permission = ContextCompat.checkSelfPermission(context ,EXTERNAL_STORAGE_PERMISSION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


}
