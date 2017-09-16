package core;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments:
 */

public class ImageLoader {

    public static final String TAG = ImageLoader.class.getSimpleName();

    private volatile  static ImageLoader sInstance;

    public static ImageLoader getInstance(){

        if (sInstance == null){
            synchronized (ImageLoader.class){
                if (sInstance == null){
                    sInstance = new ImageLoader();
                }
            }
        }
        return sInstance;
    }

    private ImageLoader(){
    }


    public synchronized  void init(){

    }








}
