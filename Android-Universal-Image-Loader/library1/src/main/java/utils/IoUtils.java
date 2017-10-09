package utils;

import android.telephony.IccOpenLogicalChannelResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhangdan on 2017/9/19.
 *
 * comments:
 */

public final class IoUtils {

    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 500 * 1024;

    public static final int CONTINUE_LOADING_PERCENTAGE = 75;

    private IoUtils(){

    }

    public interface CopyListener{
        boolean onBytesCopied(int current ,int total);
    }

    public static void closeSiliently(Closeable closeable){
        if (closeable != null){
            try{
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyFile(OutputStream outputStream , InputStream inputStream , CopyListener listener , int bufferSize) throws IOException{

        int current = 0;
        int total = inputStream.available();
        if (total <= 0){
            total = DEFAULT_IMAGE_TOTAL_SIZE;
        }
        final byte[] bytes = new byte[bufferSize];
        int count = 0;
        if (shouldStopLoading(current , total , listener)){
            return false;
        }
        while((count = inputStream.read(bytes , 0 , bufferSize)) != -1){
            outputStream.write(bytes , 0 , count);
            current += count;
            if (shouldStopLoading(current , total , listener)){
                return false;
            }
        }
        outputStream.flush();
        return true;
    }


    public static boolean shouldStopLoading(int current , int total , CopyListener listener){
        if (listener != null) {
            boolean shouldContinue = listener.onBytesCopied(current ,total);
            if (!shouldContinue){
                if (100 * current / total < CONTINUE_LOADING_PERCENTAGE){
                    return true;
                }
            }
        }
        return false;
    }

    public static void readAndCloseSiliently(InputStream inputStream){

        byte[] buffer = new byte[32 * 1024];
        try{
            while(inputStream.read(buffer , 0 , buffer.length) != -1);
        }catch (IOException e){

        }finally {
            closeSiliently(inputStream);
        }
    }
}
