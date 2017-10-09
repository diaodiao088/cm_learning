package core.download;

import android.content.Context;
import android.net.Uri;
import android.renderscript.ScriptGroup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import core.assist.ContentLenghtInputStream;
import utils.IoUtils;

/**
 * Created by zhangdan on 2017/9/28.
 * comments:
 */

public class BaseImageDownloader implements ImageDownLoader {

    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    protected static final int MAX_REDIRECT_COUNT = 5;

    private final Context mContext;
    private final int connectTimeOut;
    private final int readTimeOut;

    public BaseImageDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public BaseImageDownloader(Context context, int connectTimeOut, int readTimeOut) {
        this.mContext = context;
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (Scheme.ofUri(imageUri)) {
            case HTTP:
            case HTTPS:
                return getStreamFromNetwork(imageUri, extra);
            case FILE:
                return getStreamFromFile(imageUri, extra);
            case DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case UNKNOWN:
                return getStreamFromOthers(imageUri , extra);
            default:
                return getStreamFromOthers(imageUri ,extra);
        }
    }

    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {

        HttpURLConnection conn = createConnection(imageUri, extra);
        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"), extra);
        }
        InputStream inputStream;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            IoUtils.readAndCloseSiliently(conn.getErrorStream());
            throw e;
        }

        if (!shouldBeProcessed(conn)) {
            IoUtils.closeSiliently(inputStream);
        }

        return new ContentLenghtInputStream(new BufferedInputStream(inputStream), conn.getContentLength());
    }

    private boolean shouldBeProcessed(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() == 200;
    }


    private HttpURLConnection createConnection(String imageUri, Object extra) throws IOException {

        String url = Uri.encode(imageUri, ALLOWED_URI_CHARS);
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setConnectTimeout(connectTimeOut);
        httpURLConnection.setReadTimeout(readTimeOut);

        return httpURLConnection;
    }

    private InputStream getStreamFromFile(String filePath, Object extra) throws IOException {

        File imageFile = new File(filePath);

        if (!imageFile.exists()) {
            return null;
        }

        FileInputStream inputStream = new FileInputStream(imageFile);

        return new ContentLenghtInputStream(new BufferedInputStream(inputStream), (int) imageFile.length());
    }

    private InputStream getStreamFromDrawable(String drawablePath, Object extra) {
        String drawableString = Scheme.DRAWABLE.crop(drawablePath);
        int drawableID = Integer.parseInt(drawableString);
        return mContext.getResources().openRawResource(drawableID);
    }

    private InputStream getStreamFromAssets(String assetsPath, Object extra) throws IOException {
        assetsPath = Scheme.ASSETS.crop(assetsPath);
        return mContext.getResources().getAssets().open(assetsPath);
    }

    protected InputStream getStreamFromOthers(String path , Object extra) throws IOException{
        throw new UnsupportedEncodingException("");
    }


}
