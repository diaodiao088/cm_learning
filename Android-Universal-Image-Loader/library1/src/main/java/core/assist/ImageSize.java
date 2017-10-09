package core.assist;

/**
 * Created by zhangdan on 2017/9/28.
 *
 * comments:
 */

public class ImageSize {

    private final int width;
    private final int height;

    public ImageSize(int width , int height){
        this.width = width;
        this.height = height;
    }

    public ImageSize(int width, int height, int rotation) {
        if (rotation % 180 == 0) {
            this.width = width;
            this.height = height;
        } else {
            this.width = height;
            this.height = width;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageSize scaleDown(float scale){
        return new ImageSize((int)(width / scale) , (int)(height / scale));
    }

    public ImageSize scale(float scale){
        return new ImageSize((int)(width * scale) , (int)(height * scale));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
