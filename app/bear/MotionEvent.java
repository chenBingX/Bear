package bear;

public class MotionEvent {

    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_CANCEL = 3;

    public int action;
    public float x;
    public float y;



    public static MotionEvent build(int action, float x, float y){
        MotionEvent motionEvent = new MotionEvent();
        motionEvent.action = action;
        motionEvent.x = x;
        motionEvent.y = y;
        return motionEvent;
    }

    public int getAction() {
        return action;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
