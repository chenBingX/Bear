package artist;

import bear.MotionEvent;

import javax.swing.*;
import java.awt.*;

public class PhoneLayout extends JComponent {


    private Context context;

    public PhoneLayout(Context context) {
        this.context = context;
    }

    @Override
    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (getComponentCount() > 0 && getComponent(0) instanceof ArtistLayout){
            ((ArtistLayout) getComponent(0)).paintComponent(g);
        }
    }


    public Context getContext() {
        return context;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int componentCount = getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            Component component = getComponent(i);
            if (component instanceof ArtistLayout){
                if (component.contains((int)event.x, (int)event.y) && ((ArtistLayout) component).dispatchTouchEvent(event)){
                    break;
                }
            }
        }
        return true;
    }
}
