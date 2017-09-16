package core.assist;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments: image load fail reason
 */

public class FailReason {

    private final FailType failType;

    private final Throwable cause;

    public FailReason(FailType failType , Throwable cause){
        this.failType = failType;
        this.cause = cause;
    }

    public FailType getFailType() {
        return failType;
    }

    public Throwable getCause() {
        return cause;
    }

    public static enum  FailType{

        IO_ERROR ,

        DECODING_ERROR ,

        NETWORK_ERROR,

        OUT_OF_MEMEORY,

        UNKNOWN
    }
}
