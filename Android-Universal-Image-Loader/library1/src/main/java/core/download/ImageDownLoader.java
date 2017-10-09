package core.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by zhangdan on 2017/9/28.
 *
 * comments:
 */
public interface ImageDownLoader {

    InputStream getStream(String imageUri , Object extra) throws IOException;

    enum Scheme{

        HTTP("http"),HTTPS("https"),FILE("File"),CONTENT("Content"),ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme){
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        public static Scheme ofUri(String imageUri){
            if (imageUri != null){
                for (Scheme scheme: values()) {
                    if (scheme.belongsTo(imageUri)){
                        return scheme;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri){
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        public String wrap(String path){
            return uriPrefix + path;
        }

        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }
}
