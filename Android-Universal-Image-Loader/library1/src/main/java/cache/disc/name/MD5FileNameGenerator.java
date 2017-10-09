package cache.disc.name;

import java.io.FilenameFilter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangdan on 2017/9/18.
 *
 * comments:
 */

public class MD5FileNameGenerator implements FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(String imageUrl) {
        byte[] md5 = getMD5(imageUrl.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    private byte[] getMD5(byte[] bytes) {

        byte[] hash = null;
        try{
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(bytes);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }


}
