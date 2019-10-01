package artist;


import java.awt.*;

/**
 * @author coorchice
 * @date 2018/02/28
 */

public interface ArtParent {

    /**
     * 检测ArtistLayout是否出现在一个窗口中。
     *
     * @return
     */
    boolean isAttached();

    int getWidth();

    int getHeight();

    void update();

    void addView(ArtView view);

    Color getBackground();

}
