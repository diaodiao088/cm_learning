package core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by zhangdan on 2017/9/28.
 *
 * comments:
 */

public interface ImageDecoder {

    Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException;

}
