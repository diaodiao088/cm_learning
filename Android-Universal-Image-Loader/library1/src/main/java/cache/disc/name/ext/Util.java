package cache.disc.name.ext;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Created by zhangdan on 2017/9/26.
 *
 * comments:
 */

final class Util {

    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");

    private Util() {
    }

    static String readFully(Reader reader) throws IOException{
        try{
            StringWriter stringWriter = new StringWriter();
            char[] buffer = new char[1024];
            int count = 0;
            while((count = reader.read(buffer , 0 , buffer.length)) != -1){
                stringWriter.write(buffer , 0 ,count);
            }
            return stringWriter.toString();
        }finally {
            reader.close();
        }
    }

    static void deleteContents(File dir) throws  IOException{
        File[] fileList = dir.listFiles();

        if (fileList == null){
            throw new IOException();
        }

        for (int i = 0; i < fileList.length; i++) {
            File item = fileList[i];
            if (item != null && item.isFile()){
                if (!item.delete()){
                    throw new IOException();
                }
            }else{
                deleteContents(item);
            }
        }
    }

    static void closeQuietly(Closeable closeable) throws IOException{
        if (closeable != null){
            try{
                closeable.close();
            }catch (RuntimeException reThrown){
                throw reThrown;
            }catch (Exception ingnored){

            }
        }
    }
}
