package artist;

import bear.MotionEvent;
import utils.Log;
import utils.LogUtils;
import utils.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArtistLayout extends JPanel implements ArtParent {

    public static final String TAG_ARTIST = "ArtistLayout";
    public static final String TAG_ASYNC_ARTIST = "ArtistAsyncLayout";

    private List<ArtView> childList = new ArrayList();

    private Context context;
    private int width = ArtView.MATCH_PARENT;
    private int height = ArtView.MATCH_PARENT;
    private Color background;
    public PhoneLayout screen;
    public static ArtistLayout root;
    private int margin = -9999;
    private int marginBottom;
    private int marginRight;
    private int marginLeft;
    private int marginTop;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private List<ArtView> consumingList = new ArrayList<>();
    private Point touchPoint = new Point();
    private boolean hasOnClickListener = false;

    public ArtistLayout(Context context) {
        this.context = context;
//        init(set);
        root = this;
    }


//    private void init(AttributeSet set) {
//        initAttrs(set);
//    }
//
//    private void initAttrs(AttributeSet set) {
//        TypeArray ta = TypeArray.asTypeArray(getContext(), set);
//        int count = ta.getIndexCount();
//        for (int i = 0; i < count; i++) {
//            String name = ta.getIndex(i);
//            if (name.equals(R.styleable.android_layout_width)) {
//                width = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.android_layout_height)) {
//                height = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.android_background)) {
//                background = ta.getColor(i, JBColor.WHITE);
//            } else if (name.equals(R.styleable.android_layout_margin)) {
//                margin = (int) ta.getDimension(i, -9999);
//            } else if (name.equals(R.styleable.android_layout_marginBottom)) {
//                marginBottom = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.android_layout_marginRight)) {
//                marginRight = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.android_layout_marginLeft)) {
//                marginLeft = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.android_layout_marginTop)) {
//                marginTop = (int) ta.getDimension(i, 0);
//            }
//        }
//        if (margin != -9999) {
//            marginLeft = margin;
//            marginRight = margin;
//            marginBottom = margin;
//            marginTop = margin;
//        }
//        setSize(width, height);
//        setBounds(marginLeft, marginTop, getWidth(), getHeight());
//        setBackground(background);
//    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // 去除抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        try {
            draw(g2d);
            drawChildren(g2d);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void draw(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawChildren(Graphics2D g2d) {
        measureChildren(g2d);
        layoutChildren(g2d);

        for (int i = 0; i < childList.size(); i++) {
            ArtView child = childList.get(i);
            child.draw(g2d);
        }
    }

    private void measureChildren(Graphics2D g2d) {
        for (int i = 0; i < childList.size(); i++) {
            ArtView child = childList.get(i);
            child.measure(getWidth(), getHeight(), g2d);
        }
    }

    private void layoutChildren(Graphics2D g2d) {
        for (ArtView child : childList) {
            child.layout(0 + getPaddingLeft() - getPaddingRight(), 0 + getPaddingTop() - getPaddingBottom(),
                    getWidth(), getHeight(), g2d);
        }
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public void update() {
        postInvalidate();
    }

    /**
     * 同一个View只能添加到一个ArtistLayout中。否则抛出异常。见{@link ArtView#attach(ArtParent)}。
     *
     * @param view
     */
    public final void addView(ArtView view) {
        view.attach(this);
        childList.add(view);
        postInvalidate();
    }

    /**
     * 同一个View只能添加到一个ArtistLayout中。否则抛出异常。见{@link ArtView#attach(ArtParent)}。
     *
     * @param views
     */
    public final void addViews(List<ArtView> views) {
        childList.addAll(views);
        for (int i = 0; i < childList.size(); i++) {
            childList.get(i).attach(this);
        }
        postInvalidate();
    }

    public final void removeView(ArtView view) {
        view.detach();
        childList.remove(view);
        postInvalidate();
    }

    public final void clearViews() {
        for (int i = 0; i < childList.size(); i++) {
            childList.get(i).detach();
        }
        childList.clear();
        postInvalidate();
    }

    public void postInvalidate() {
        invalidate();
//        paintComponent(getGraphics());
        context.invalidate();
    }

    public PhoneLayout getRoot() {
        return screen;
    }

    public void setRoot(PhoneLayout root) {
        this.screen = root;
        setSize(width, height);
    }

    @Override
    public void setSize(int width, int height) {
        if (screen != null) {
            if (width == ArtView.MATCH_PARENT) {
                width = screen.getWidth();
            } else if (width == ArtView.WRAP_CONTENT) {

            }
            if (height == ArtView.MATCH_PARENT) {
                height = screen.getHeight();
            } else if (height == ArtView.WRAP_CONTENT) {

            }

        }
        super.setSize(width - marginLeft - marginRight, height - marginBottom - marginTop);
    }

    public ArtView findViewById(String id) {
        for (ArtView view : childList) {
            if (!TextUtils.isEmpty(view.getId()) && view.getId().equals(id)) {
                return view;
            } else {
                ArtView findView = view.findViewById(id);
                if (findView != null) {
                    return findView;
                }
            }

        }

        return null;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public Context getContext() {
        return context;
    }

    public List<ArtView> getChildList() {
        return childList;
    }

    public boolean dispatchTouchEvent(MotionEvent event){
        return onTouchEvent(event);
    }


    public final boolean onTouchEvent(MotionEvent event) {
        boolean hasConsume = false;
        int action = event.getAction();
        int actionMasked = action;
        if (actionMasked == MotionEvent.ACTION_DOWN && !hasOnClickListener) {
            for (int i = childList.size() - 1; i >= 0; i--) {
                ArtView child = childList.get(i);
                if (child.getVisibility() == ArtView.VISIBLE &&
                        child.getRect().contains((int)event.getX(), (int)event.getY())) {
                    hasConsume = child.onTouch(event);
                    if (hasConsume) {
                        consumingList.add(child);
                        break;
                    }
                }
            }
        } else {
            Iterator<ArtView> iterator = consumingList.iterator();
            List<ArtView> goneChild = new ArrayList<>();
            while (iterator.hasNext()) {
                hasConsume = true;
                ArtView child = iterator.next();
                if (child.getVisibility() == ArtView.VISIBLE) {
                    child.onTouch(event);
                } else {
                    int realAction = event.getAction();
                    if (realAction != MotionEvent.ACTION_UP && realAction != MotionEvent.ACTION_CANCEL) {
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        child.onTouch(event);
                        event.setAction(realAction);
                        goneChild.add(child);
                    }
                }
            }
            consumingList.removeAll(goneChild);
            goneChild.clear();
            if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
                consumingList.clear();
            }
        }
        if (LogUtils.DEBUG) {
            touchPoint.move((int)event.getX(), (int)event.getY());
            invalidate();
        }
        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            event.setLocation(-1, -1);
        }
        return hasConsume;
    }
}
