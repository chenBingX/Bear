package artist;


import bear.MotionEvent;
import utils.ThreadPool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

//
//import com.sun.jna.platform.unix.X11;
//import sun.plugin.dom.css.Rect;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Iterator;
//
///**
// * Created by coorchice on 2017/10/13.
// * <p>
// * ArtView是重制的View，需要添加到ArtistLayout中才能显示。大多数使用方法和原生View保持一致。
// * <p>
// * 支持绝对布局和相对布局。
// * <p>
// * 可以单独处理触摸事件。
// * <p>
// * 现在已经对重绘操作进行了对齐，所以频繁的发起重绘不会带来性能影响。
// * <p>
// * ArtView由于不受系统管控，所以可以异步进行创建、更新UI等操作。但需要谨慎对待数据安全问题，特别是View相关的属性。
// * <p>
// * 支持圆角背景，并能单独指定圆角位置。{@link ArtView#corner}
// */
//
public class ArtView {

    public static final int MARGIN_TYPE_NULL = -1;

    /**
     * leftMargin、topMargin
     */
    public static final int MARGIN_TYPE_F = 1;
    /**
     * rightMargin、bottomMargin
     */
    public static final int MARGIN_TYPE_R = 2;

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;

    public static final int MODE_SHIFT = 30;
    public static final int MODE_MASK = 0x3 << MODE_SHIFT;
    public static final int WRAP_CONTENT = (1 << MODE_SHIFT);
    public static final int EXACTLY = (2 << MODE_SHIFT);
    public static final int MATCH_PARENT = (3 << MODE_SHIFT);


    public static final int ALIGN_NO = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_TOP = 2;
    public static final int ALIGN_RIGHT = 3;
    public static final int ALIGN_BOTTOM = 4;

    public static final String X = "x";
    public static final String Y = "y";
    public static final String TRANSLATION_X = X;
    public static final String TRANSLATION_Y = Y;
    public static final String ROTATE = "degrees";
    public static final String SCALE_X = "scaleX";
    public static final String SCALE_Y = "scaleY";

    public int visibility = VISIBLE;
    private String id = null;

    protected int width;
    protected int height;
    protected float x;
    protected float y;
    protected int left;
    protected int top;
    protected int right;
    protected int bottom;
    protected int marginLeft;
    protected int marginTop;
    protected int marginRight;
    protected int marginBottom;
    protected int padding;
    protected int paddingLeft;
    protected int paddingTop;
    protected int paddingRight;
    protected int paddingBottom;
    protected Anchor anchor;
    private ArrayList<Anchor> anchorList;

    private ArtParent parent;
    private int parentWidth;
    private int parentHeight;

    protected int layoutGravity = Gravity.NO_GRAVITY;
    protected int gravity;
    //    private X11.Drawable background;
    private int backgroundResId = -1;
    private int backgroundColor;
    protected Color background;
    //    private Path backgroundColorPath;
//    private RectF backgroundColorRectF;
//    private Path strokeWidthPath;
//    private RectF strokeLineRectF;
    private float strokeWidth;
    private int strokeColor;
    private float corner;
    private float leftTopCorner[] = new float[2];
    private float rightTopCorner[] = new float[2];
    private float leftBottomCorner[] = new float[2];
    private float rightBottomCorner[] = new float[2];
    private float corners[] = new float[8];
    private boolean leftTopCornerEnable;
    private boolean rightTopCornerEnable;
    private boolean leftBottomCornerEnable;
    private boolean rightBottomCornerEnable;

//    protected AttributeSet attrs;
//    protected Context context;

    private String anchorView = "no_id";
    private int marginAnchorLeft = -1;
    private int marginAnchorTop = -1;
    private int marginAnchorRight = -1;
    private int marginAnchorBottom = -1;
    public boolean centerAnchorHorizontal;
    public boolean centerAnchorVertical;
    public int widthMode = WRAP_CONTENT;
    public int heightMode = WRAP_CONTENT;
    private Context context;

    private int hAlign = ALIGN_NO, vAlign = ALIGN_NO;
    private ArtistLayout root;
    public String code;
    private boolean isSelected = false;

    private Rect rect;
    private OnTouchListener onTouchListener;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private boolean isClick;
    private Runnable tapRunnable;
    private Runnable longClickRunnable;
    private boolean clickable = true;
    private boolean longClickable = true;


//    public ArtView(Context context, AttributeSet set) {
//        this.context = context;
//        init(set);
//    }

    public ArtView(Context context) {
        this.context = context;
        rect = new Rect();
//        init(set);
    }

//    private void init(AttributeSet set) {
//        initAttrs(set);
//    }

//    private void initAttrs(AttributeSet set) {
//        TypeArray ta = TypeArray.asTypeArray(getContext(), set);
//        int count = ta.getIndexCount();
//        for (int i = 0; i < count; i++) {
//            String name = ta.getIndex(i);
//            if (name.equals(R.styleable.id)) {
//                id = ta.getString(i);
//                if (!TextUtils.isEmpty(id) && id.contains("@+id/")) {
//                    id = id.substring(id.indexOf("/") + 1);
//                }
//            } else if (name.equals(R.styleable.layout_width)) {
//                width = (int) ta.getDimension(i, 0);
//                widthMode = width;
//                if (width != WRAP_CONTENT && width != MATCH_PARENT) {
//                    widthMode = EXACTLY;
//                }
//            } else if (name.equals(R.styleable.layout_height)) {
//                height = (int) ta.getDimension(i, 0);
//                heightMode = height;
//                if (height != WRAP_CONTENT && height != MATCH_PARENT) {
//                    heightMode = EXACTLY;
//                }
//            } else if (name.equals(R.styleable.background)) {
//                background = ta.getColor(i, null);
//            } else if (name.equals(R.styleable.x)) {
//                x = ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.y)) {
//                y = ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.marginLeft)) {
//                setMarginLeft((int) ta.getDimension(i, 0));
//            } else if (name.equals(R.styleable.marginTop)) {
//                setMarginTop((int) ta.getDimension(i, 0));
//            } else if (name.equals(R.styleable.marginRight)) {
//                setMarginRight((int) ta.getDimension(i, 0));
//            } else if (name.equals(R.styleable.marginBottom)) {
//                setMarginBottom((int) ta.getDimension(i, 0));
//            } else if (name.equals(R.styleable.layoutGravity)) {
//                layoutGravity = Gravity.gravity(ta.getString(i));
//            } else if (name.equals(R.styleable.gravity)) {
//                gravity = Gravity.gravity(ta.getString(i));
//            } else if (name.equals(R.styleable.paddingTop)) {
//                paddingTop = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.paddingLeft)) {
//                paddingLeft = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.paddingRight)) {
//                paddingRight = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.paddingBottom)) {
//                paddingBottom = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.visibility)) {
//                String v = ta.getString(i);
//                if (v.equals("visible")) {
//                    visibility = VISIBLE;
//                } else if (v.equals("gone")) {
//                    visibility = GONE;
//                } else if (v.equals("invisible")) {
//                    visibility = INVISIBLE;
//                }
//            } else if (name.equals(R.styleable.marginAnchorBottom)) {
//                marginAnchorBottom = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.marginAnchorRight)) {
//                marginAnchorRight = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.marginAnchorTop)) {
//                marginAnchorTop = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.marginAnchorLeft)) {
//                marginAnchorLeft = (int) ta.getDimension(i, 0);
//            } else if (name.equals(R.styleable.anchor)) {
//                anchorView = ta.getString(i);
//                if (!TextUtils.isEmpty(anchorView) && anchorView.contains("@+id/")) {
//                    anchorView = anchorView.substring(anchorView.indexOf("/") + 1);
//                }
//            } else if (name.equals(R.styleable.centerAnchorHorizontal)) {
//                centerAnchorHorizontal = ta.getBoolean(i, false);
//            } else if (name.equals(R.styleable.centerAnchorVertical)) {
//                centerAnchorVertical = ta.getBoolean(i, false);
//            } else if (name.equals(R.styleable.alignAnchorLeft)) {
//                boolean v = ta.getBoolean(i, false);
//                if (v) {
//                    hAlign = ALIGN_LEFT;
//                }
//            } else if (name.equals(R.styleable.alignAnchorRight)) {
//                boolean v = ta.getBoolean(i, false);
//                if (v) {
//                    hAlign = ALIGN_RIGHT;
//                }
//            } else if (name.equals(R.styleable.alignAnchorTop)) {
//                boolean v = ta.getBoolean(i, false);
//                if (v) {
//                    vAlign = ALIGN_TOP;
//                }
//            } else if (name.equals(R.styleable.alignAnchorBottom)) {
//                boolean v = ta.getBoolean(i, false);
//                if (v) {
//                    vAlign = ALIGN_BOTTOM;
//                }
//            }
//        }
//
//    }

    public void attach(ArtParent parent) {
        this.parent = parent;
        if (parent != null) {
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
        }
    }

    public void detach() {
        parent = null;
        parentWidth = 0;
        parentHeight = 0;
    }

    public void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        if (background == null) {
            background = parent.getBackground();
        }
        g2d.setColor(background);
        g2d.fillRect((int) x, (int) y, getWidth(), getHeight());

        onDraw(g2d);

        if (isSelected) {
            Color temp = g2d.getColor();
//            g2d.setColor(SELECTED_STROKE_COLOR);
            Stroke tempStroke = g2d.getStroke();
            BasicStroke basicStroke = new BasicStroke(2f);
            g2d.setStroke(basicStroke);
            g2d.drawRect((int) x, (int) y, getWidth(), getHeight());
            g2d.setColor(temp);
            g2d.setStroke(tempStroke);
        }
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void clearFlags() {
        isSelected = false;
    }

    public void makeWidth(int width) {
        if (getSizeMode(width) == MATCH_PARENT) {
            this.width = ((parentWidth - marginLeft - marginRight) & ~MODE_MASK) | (MATCH_PARENT & MODE_MASK);
        } else if (getSizeMode(width) == WRAP_CONTENT) {
            this.width = (width & ~MODE_MASK) | (WRAP_CONTENT & MODE_MASK);
        } else {
            this.width = (width & ~MODE_MASK) | (EXACTLY & MODE_MASK);
        }
    }

    public void makeHeight(int height) {
        if (getSizeMode(height) == MATCH_PARENT) {
            this.height = ((parentHeight - marginTop - marginBottom) & ~MODE_MASK) | (MATCH_PARENT & MODE_MASK);
        } else if (getSizeMode(height) == WRAP_CONTENT) {
            this.height = (height & ~MODE_MASK) | (WRAP_CONTENT & MODE_MASK);
        } else {
            this.height = (height & ~MODE_MASK) | (EXACTLY & MODE_MASK);
        }
    }

    public int getWidth() {
        if (visibility != GONE) {
            return (width & ~MODE_MASK);
        } else {
            return 0;
        }
    }

    public void setWidth(int width) {
        if (getSizeMode(width) == 0) {
            width = (width & ~MODE_MASK) | (EXACTLY & MODE_MASK);
        }
        makeWidth(width);
        right = left + getWidth();
        invalidate();
    }


    public int getHeight() {
        if (visibility != GONE) {
            return (height & ~MODE_MASK);
        } else {
            return 0;
        }
    }

    public void setHeight(int height) {
        if (getSizeMode(height) == 0) {
            height = (height & ~MODE_MASK) | (EXACTLY & MODE_MASK);
        }
        makeHeight(height);
        bottom = top + getHeight();
        invalidate();
    }


    public int getSizeMode(int size) {
        return (size & MODE_MASK);
    }

    public int computeWapContentWidth() {
        return 0;
    }

    public int computeWapContentHeight() {
        return 0;
    }

    public int getWidthMode() {
        return getSizeMode(this.width);
    }

    public int getHeightMode() {
        return getSizeMode(this.height);
    }

    public void setLayoutGravity(int layoutGravity) {
        this.layoutGravity = layoutGravity;
        //computeXYWithLayoutGravity();
        invalidate();
    }

    //    private void drawStrokeLine(Canvas canvas) {
//        if (strokeWidth > 0) {
//            if (strokeWidthPath == null) {
//                strokeWidthPath = new Path();
//            } else {
//                strokeWidthPath.reset();
//            }
//            if (strokeLineRectF == null) {
//                strokeLineRectF = new RectF();
//            } else {
//                strokeLineRectF.setEmpty();
//            }
//            strokeLineRectF.set(x + strokeWidth / 2, y + strokeWidth / 2, x + getWidth() - strokeWidth / 2,
//                    y + getHeight() - strokeWidth / 2);
//            getCorners(corner);
//            strokeWidthPath.addRoundRect(strokeLineRectF, corners, Path.Direction.CW);
//            Paint.Style style = paint.getStyle();
//            int color = paint.getColor();
//            float strokeWidth = paint.getStrokeWidth();
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setColor(strokeColor);
//            paint.setStrokeWidth(this.strokeWidth);
//            canvas.drawPath(strokeWidthPath, paint);
//            paint.setStyle(style);
//            paint.setColor(color);
//            paint.setStrokeWidth(strokeWidth);
//        }
//    }
//
//    private void drawBackgroundColor(Canvas canvas) {
//        if (backgroundColor == 0) {
//            return;
//        }
//
//        if (backgroundColorPath == null) {
//            backgroundColorPath = new Path();
//        } else {
//            backgroundColorPath.reset();
//        }
//
//        if (backgroundColorRectF == null) {
//            backgroundColorRectF = new RectF();
//        } else {
//            backgroundColorRectF.setEmpty();
//        }
//
//        backgroundColorRectF.set(x + strokeWidth, y + strokeWidth, x + getWidth() - strokeWidth, y + getHeight() - strokeWidth);
//        getCorners(corner - strokeWidth / 2);
//        backgroundColorPath.addRoundRect(backgroundColorRectF, corners, Path.Direction.CW);
//
//        Paint.Style style = paint.getStyle();
//        int color = paint.getColor();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(backgroundColor);
//        canvas.drawPath(backgroundColorPath, paint);
//        paint.setStyle(style);
//        paint.setColor(color);
//    }
//
//    private float[] getCorners(float corner) {
//        leftTopCorner[0] = 0;
//        leftTopCorner[1] = 0;
//        rightTopCorner[0] = 0;
//        rightTopCorner[1] = 0;
//        leftBottomCorner[0] = 0;
//        leftBottomCorner[1] = 0;
//        rightBottomCorner[0] = 0;
//        rightBottomCorner[1] = 0;
//        if (this.leftTopCornerEnable || this.rightTopCornerEnable || this.leftBottomCornerEnable
//                || this.rightBottomCornerEnable) {
//            if (this.leftTopCornerEnable) {
//                leftTopCorner[0] = corner;
//                leftTopCorner[1] = corner;
//            }
//            if (this.rightTopCornerEnable) {
//                rightTopCorner[0] = corner;
//                rightTopCorner[1] = corner;
//            }
//            if (this.leftBottomCornerEnable) {
//                leftBottomCorner[0] = corner;
//                leftBottomCorner[1] = corner;
//            }
//            if (this.rightBottomCornerEnable) {
//                rightBottomCorner[0] = corner;
//                rightBottomCorner[1] = corner;
//            }
//        } else {
//            leftTopCorner[0] = corner;
//            leftTopCorner[1] = corner;
//            rightTopCorner[0] = corner;
//            rightTopCorner[1] = corner;
//            leftBottomCorner[0] = corner;
//            leftBottomCorner[1] = corner;
//            rightBottomCorner[0] = corner;
//            rightBottomCorner[1] = corner;
//        }
//        corners[0] = leftTopCorner[0];
//        corners[1] = leftTopCorner[1];
//        corners[2] = rightTopCorner[0];
//        corners[3] = rightTopCorner[1];
//        corners[4] = rightBottomCorner[0];
//        corners[5] = rightBottomCorner[1];
//        corners[6] = leftBottomCorner[0];
//        corners[7] = leftBottomCorner[1];
//        return corners;
//    }
//
//    private void drawBackground(Canvas canvas) {
//        if (resources != null && backgroundResId != -1) {
//            this.background = resources.getDrawable(backgroundResId);
//        }
//        if (background != null) {
//            background.setBounds(getRect());
//            background.draw(canvas);
//        }
//    }
//
    public void onDraw(Graphics g) {
    }

    //    public Drawable getBackground() {
//        return background;
//    }
//
//    public void setBackground(Drawable background) {
//        this.background = background;
//        backgroundResId = -1;
//        invalidate();
//    }
//
//    public void setBackground(Bitmap background) {
//        this.background = new BitmapDrawable(background);
//        backgroundResId = -1;
//        invalidate();
//    }
//
//    public void setBackground(@DrawableRes int background) {
//        if (this.resources == null) {
//            this.backgroundResId = background;
//        } else {
//            this.background = resources.getDrawable(background);
//        }
//        invalidate();
//    }
//
//    public void setBackgroundColor(int color) {
//        this.backgroundColor = color;
//        invalidate();
//    }
//
//    public void setBackgroundColor(String color) {
//        setBackgroundColor(Color.parseColor(color));
//    }
//
//    public int getBackgroundColor() {
//        return backgroundColor;
//    }
//
    public int getVisibility() {
        return visibility;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (isAttach() && layoutGravity != Gravity.NO_GRAVITY) {
            layoutGravity &= ~Gravity.LEFT;
            layoutGravity &= ~Gravity.RIGHT;
            layoutGravity &= ~Gravity.CENTER_HORIZONTAL;
            layoutGravity &= ~Gravity.CENTER;
        }
        this.x = x;
        this.left = (int) x;
        this.right = this.left + getWidth();
        notifyAllAnchor();
//        invalidate();
    }

    public void setXWithoutInvalidate(float x) {
        this.x = x;
        this.left = (int) x;
        this.right = this.left + getWidth();
//        notifyAllAnchor();

    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (isAttach() && layoutGravity != Gravity.NO_GRAVITY) {
            layoutGravity &= ~Gravity.TOP;
            layoutGravity &= ~Gravity.BOTTOM;
            layoutGravity &= ~Gravity.CENTER_VERTICAL;
            layoutGravity &= ~Gravity.CENTER;
        }
        this.y = y;
        this.top = (int) y;
        this.bottom = this.top + getHeight();
        notifyAllAnchor();
//        invalidate();
    }

    public void setYWithoutInvalidate(float y) {
        this.y = y;
        this.top = (int) y;
        this.bottom = this.top + getHeight();
//        notifyAllAnchor();

    }

    public void setXY(float x, float y) {
        if (isAttach() && layoutGravity != Gravity.NO_GRAVITY) {
            layoutGravity &= ~Gravity.RIGHT;
            layoutGravity &= ~Gravity.LEFT;
            layoutGravity &= ~Gravity.CENTER_HORIZONTAL;
            layoutGravity &= ~Gravity.TOP;
            layoutGravity &= ~Gravity.BOTTOM;
            layoutGravity &= ~Gravity.CENTER_VERTICAL;
            layoutGravity &= ~Gravity.CENTER;
        }
        this.x = x;
        this.y = y;
        this.left = (int) x;
        this.top = (int) y;
        this.right = this.left + getWidth();
        this.bottom = this.top + getHeight();
        invalidate();
    }


    public int getParentWidth() {
        if (parent != null) {
            parentWidth = parent.getWidth();
        } else {
            parentWidth = 0;
        }
        return parentWidth;
    }

    public int getParentHeight() {
        if (parent != null) {
            parentHeight = parent.getHeight();
        } else {
            parentHeight = 0;
        }
        return parentHeight;
    }


    protected void computeXYWithLayoutGravity() {
        final int majorGravity = layoutGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = layoutGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
            case Gravity.BOTTOM:
                setYWithoutInvalidate(getParentHeight() - getHeight() + marginTop - marginBottom);
                break;

            case Gravity.CENTER_VERTICAL:
                setYWithoutInvalidate(getParentHeight() / 2 - getHeight() / 2 + marginTop - marginBottom);
                break;

            case Gravity.TOP:
                setYWithoutInvalidate(marginTop - marginBottom);
                break;
            default:
                break;
        }

        switch (minorGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                setXWithoutInvalidate(getParentWidth() / 2 - getWidth() / 2 + +marginLeft - marginRight);
                break;

            case Gravity.RIGHT:
                setXWithoutInvalidate(getParentWidth() - getWidth() + marginLeft - marginRight);
                break;
            case Gravity.LEFT:
                setXWithoutInvalidate(marginLeft - marginRight);
                break;
            default:
                break;
        }
    }

    public void updateGravity() {
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        setX(marginLeft);
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        setY(marginTop);

    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        setX(-marginRight);
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        setY(-marginBottom);
    }

    public void setBackgroundColor(Color color) {
        this.background = color;
        invalidate();
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        if (this.visibility == GONE) {

        }
        invalidate();
    }

    /**
     * 将View添加到一个父View中，见{@link ArtistLayout#addView(ArtView)}。
     * 注意不要传null，会抛 NullPointException 。
     *
     * @param parent ArtistLayout
     * @throws
     */
    public void addTo(ArtistLayout parent) {
        parent.addView(this);
    }

    /**
     * 检测该View是否被添加到了一个ArtistLayout中。
     *
     * @return
     */
    public boolean isAttach() {
        return parent != null;
    }

    //
    public ArtParent getParent() {
        return parent;
    }

    /**
     * 当View被添加到一个ArtistLayout的时候会被调用。
     */
    public void onAttach() {

    }

    /**
     * 当View被从一个ArtistLayout移除的时候会被调用。
     */
    public void onDetach() {

    }

    /**
     * 要求View发起一次重绘请求。
     */
    public void invalidate() {
        if (anchor != null && !anchor.isAttach()) {
            anchor.notifyChanged();
        } else {
            notifyAllAnchor();
        }
        if (parent != null) {
            parent.update();
        }
    }

    /**
     * 设置左边缘与target右边缘保持leftMargin的单位的距离。
     *
     * @param target
     * @param leftMargin
     */
    public void leftMarginTo(ArtView target, float leftMargin) {
        if (anchor != null) {
            anchor.t = target;
            anchor.offsetX = leftMargin;
            anchor.attach = true;
            anchor.followVisible = false;
            anchor.xMarginType = Anchor.MARGIN_TYPE_F;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, leftMargin, -1, true, false, Anchor.MARGIN_TYPE_F, Anchor.MARGIN_TYPE_NULL);
        }
    }

    /**
     * 设置上边缘与target底边缘保持topMargin的单位的距离。
     *
     * @param target
     * @param topMargin
     */
    public void topMarginTo(ArtView target, float topMargin) {
        if (anchor != null) {
            anchor.t = target;
            anchor.offsetY = topMargin;
            anchor.attach = true;
            anchor.followVisible = false;
            anchor.yMarginType = Anchor.MARGIN_TYPE_F;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, -1, topMargin, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_F);
        }
    }

    /**
     * 设置右边缘与target左边缘保持rightMargin的单位的距离。
     * <p>
     *
     * @param target
     * @param rightMargin
     */
    public void rightMarginTo(ArtView target, float rightMargin) {
        if (anchor != null) {
            anchor.t = target;
            anchor.offsetX = rightMargin;
            anchor.attach = true;
            anchor.followVisible = false;
            anchor.xMarginType = Anchor.MARGIN_TYPE_R;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, rightMargin, -1, true, false, Anchor.MARGIN_TYPE_R, Anchor.MARGIN_TYPE_NULL);
        }
    }

    /**
     * 设置下边缘与target上边缘保持bottomMargin的单位的距离。
     * <p>
     *
     * @param target
     * @param bottomMargin
     */
    public void bottomMarginTo(ArtView target, float bottomMargin) {
        if (anchor != null) {
            anchor.t = target;
            anchor.offsetY = bottomMargin;
            anchor.attach = true;
            anchor.followVisible = false;
            anchor.yMarginType = Anchor.MARGIN_TYPE_R;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, -1, bottomMargin, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_R);
        }
    }

    public void centerAnchorVertical(ArtView target, boolean centerAnchorVertical) {
        this.centerAnchorVertical = centerAnchorVertical;
        if (anchor != null) {
            anchor.centerAnchorVertical = centerAnchorVertical;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, -1, -1, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_NULL);
        }
    }

    public void centerAnchorHorizontal(ArtView target, boolean centerAnchorHorizontal) {
        this.centerAnchorHorizontal = centerAnchorHorizontal;
        if (anchor != null) {
            anchor.centerAnchorHorizontal = centerAnchorHorizontal;
            anchor.changed();
            invalidate();
        } else {
            anchor(target, -1, -1, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_NULL);
        }
    }

    public boolean isCenterAnchorVertical() {
        return centerAnchorVertical;
    }

    public boolean isCenterAnchorHorizontal() {
        return centerAnchorHorizontal;
    }


    /**
     * 设置水平方向的对齐
     *
     * @param target
     * @param hAlign
     */
    public void alignAnchorH(ArtView target, int hAlign) {
        this.hAlign = hAlign;
        if (anchor != null) {
            anchor.hAlign = hAlign;
        } else {
            anchor(target, -1, -1, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_NULL);
            anchor.hAlign = hAlign;
        }

        anchor.changed();
        invalidate();
    }

    /**
     * 设置垂直方向的对齐
     *
     * @param target
     * @param vAlign
     */
    public void alignAnchorV(ArtView target, int vAlign) {
        this.vAlign = vAlign;
        if (anchor != null) {
            anchor.vAlign = vAlign;
        } else {
            anchor(target, -1, -1, true, false, Anchor.MARGIN_TYPE_NULL, Anchor.MARGIN_TYPE_NULL);
            anchor.vAlign = vAlign;
        }

        anchor.changed();
        invalidate();
    }

    public void alignAnchorLeft(ArtView target) {
        alignAnchorH(target, ALIGN_LEFT);
    }

    public void alignAnchorRight(ArtView target) {
        alignAnchorH(target, ALIGN_RIGHT);
    }

    public void alignAnchorTop(ArtView target) {
        alignAnchorV(target, ALIGN_TOP);
    }

    public void alignAnchorBottom(ArtView target) {
        alignAnchorV(target, ALIGN_BOTTOM);
    }

    /**
     * 获得水平方向与Anchor view的对齐模式
     *
     * @return
     */
    public int getHAlign() {
        return hAlign;
    }

    /**
     * 获得垂直方向与Anchor view的对齐模式
     *
     * @return
     */
    public int getVAlign() {
        return vAlign;
    }

    /**
     * 自己左上角坐标相对于target左上角坐标偏移(offsetX, offsetY)。详情见
     * {@link ArtView#anchor(ArtView target, float offsetX, float offsetY, boolean attach, boolean followVisible)}
     * <p>
     * 当attach为true时，会跟随target的可见性。
     *
     * @param target  目标（宿主）View。
     * @param offsetX 在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于x点向右偏移offsetX个单位长度。
     * @param offsetY 在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于y点向右偏移offsetY个单位长度。
     * @param attach  是否建立依附关系。如果是true表示希望和目标锚点建立依附关系，那么当目标ArtView坐标发生改变时，
     *                本ArtView的位置也会跟着改变。如果是false表示不和目标锚点建立依附关系，也就不会随目标的坐标变化而变
     *                化，通常可以用来首次确定自己在总坐标系中的位置。
     */
    public void anchor(ArtView target, float offsetX, float offsetY, boolean attach) {
        anchor(target, 0, 0, 0, 0, offsetX, offsetY, attach, true);
    }

    /**
     * 自己左上角坐标相对于target左上角坐标偏移(offsetX, offsetY)。详情见
     * {@link ArtView#anchor(ArtView target, float mX, float mY, float tX, float tY, float offsetX, float offsetY, boolean attach, boolean followVisible)}
     *
     * @param target        目标（宿主）View。
     * @param offsetX       在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于x点向右偏移offsetX个单位长度。
     * @param offsetY       在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于y点向右偏移offsetY个单位长度。
     * @param attach        是否建立依附关系。如果是true表示希望和目标锚点建立依附关系，那么当目标ArtView坐标发生改变时，
     *                      本ArtView的位置也会跟着改变。如果是false表示不和目标锚点建立依附关系，也就不会随目标的坐标变化而变
     *                      化，通常可以用来首次确定自己在总坐标系中的位置。
     * @param followVisible 是否跟随target的可见性。只有attach为true时，该项才有效。
     */
    public void anchor(ArtView target, float offsetX, float offsetY, boolean attach, boolean followVisible) {
        anchor(target, 0, 0, 0, 0, offsetX, offsetY, attach, followVisible);
    }

    /**
     * 将自己的一个和目标target中的一个锚点建立联系，以此来确定自己位置。注意看看参数attch的解释哦！
     *
     * @param target        目标（宿主）View。
     * @param mX            自己坐标系（以自己的左上角为坐标原点的坐标系）中的x点。
     * @param mY            自己坐标系（以自己的左上角为坐标原点的坐标系）中的y点。
     * @param tX            目标坐标系（以目标的左上角为坐标原点的坐标系）中的x点。
     * @param tY            目标坐标系（以目标的左上角为坐标原点的坐标系）中的y点。
     * @param offsetX       在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于x点向右偏移offsetX个单位长度。
     * @param offsetY       在目标坐标系（以目标的左上角为坐标原点的坐标系）中，相对于y点向右偏移offsetY个单位长度。
     * @param attach        是否建立依附关系。如果是true表示希望和目标锚点建立依附关系，那么当目标ArtView坐标发生改变时，
     *                      本ArtView的位置也会跟着改变。如果是false表示不和目标锚点建立依附关系，也就不会随目标的坐标变化而变
     *                      化，通常可以用来首次确定自己在总坐标系中的位置。
     * @param followVisible 是否跟随target的可见性。只有attach为true时，该项才有效。
     */
    public final void anchor(ArtView target, float mX, float mY, float tX, float tY, float offsetX,
                             float offsetY, boolean attach, boolean followVisible) {
        if (attach) {
            detachAnchor();
            anchor = new Anchor(this, target, mX, mY, tX, tY, offsetX, offsetY, followVisible);
            anchor.setAttach(attach);
            target.addAnchor(anchor);
            anchor.changed();
            invalidate();
        } else {
            setXY(tX + offsetX - mX + target.x, tY + offsetY - mY + target.y);
        }
    }

    public void anchor(ArtView target, float offsetX, float offsetY, boolean attach, boolean followVisible,
                       int xMarginType, int yMarginType) {
        anchor(target, 0, 0, 0, 0, offsetX, offsetY, attach, followVisible, xMarginType, yMarginType);
    }


    public final void anchor(ArtView target, float mX, float mY, float tX, float tY, float offsetX,
                             float offsetY, boolean attach, boolean followVisible, int xMarginType, int yMarginType) {
        if (attach) {
            detachAnchor();
            anchor = new Anchor(this, target, mX, mY, tX, tY, offsetX, offsetY, followVisible);
            anchor.xMarginType = xMarginType;
            anchor.yMarginType = yMarginType;
            anchor.setAttach(attach);
            target.addAnchor(anchor);
            anchor.changed();
            invalidate();
        } else {
            setXY(tX + offsetX - mX + target.x, tY + offsetY - mY + target.y);
        }
    }

    /**
     * 接触Anchor所建立的联系。
     */
    public void detachAnchor() {
        if (anchor != null && anchor.getT() != null) {
            anchor.getT().removeAnchor(anchor);
            anchor = null;
        }
    }

    protected void addAnchor(Anchor anchor) {
        if (anchorList == null) {
            this.anchorList = new ArrayList<Anchor>();
        }
        this.anchorList.add(anchor);
    }

    protected void removeAnchor(Anchor anchor) {
        if (anchorList != null) {
            this.anchorList.remove(anchor);
        }
    }

    protected void clearAnchor() {
        if (anchorList != null) {
            this.anchorList.clear();
        }
    }

    public void notifyAllAnchor() {
        if (anchorList != null) {
            Iterator<Anchor> iterator = anchorList.iterator();
            while (iterator.hasNext()) {
                iterator.next().notifyChanged();
            }
        }
    }

    public String getAnchorView() {
        return anchorView;
    }

    public int getMarginAnchorLeft() {
        return marginAnchorLeft;
    }

    public int getMarginAnchorTop() {
        return marginAnchorTop;
    }

    public int getMarginAnchorRight() {
        return marginAnchorRight;
    }

    public int getMarginAnchorBottom() {
        return marginAnchorBottom;
    }

    public void measure(int pWidth, int pHeight, Graphics2D g2d) {
        parentWidth = pWidth;
        parentHeight = pHeight;
        onMeasure(g2d);
    }


    public void onMeasure(Graphics g) {

    }

    public void layout(int i, int i1, int i2, int i3, Graphics2D g2d) {
        computeXYWithLayoutGravity();
        onLayout(g2d);
        notifyAllAnchor();
    }

    public void onLayout(Graphics2D g2d) {

    }

    public ArtView findViewById(String id) {
        return null;
    }

    public void checkParentSize() {
        if (parent != null) {
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
        }
    }

    public Context getContext() {
        return context;
    }


    public ArtistLayout getRoot() {
        return root;
    }

    public void setRoot(ArtistLayout root) {
        this.root = root;
    }

    public ArtView checkPointInRect(int x, int y) {
        Rect rect = new Rect((int) this.x, (int) this.y, (int) this.x + getWidth(), (int) this.y + getHeight());
        if (rect.contains(x, y)) {
            return this;
        } else {
            return null;
        }
    }

    public ArtView findViewByCode(String code){
        if (code.equals(this.code)){
            return this;
        } else {
            return null;
        }
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        invalidate();

    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        this.paddingLeft = padding;
        this.paddingTop = padding;
        this.paddingRight = padding;
        this.paddingBottom = padding;
        invalidate();
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        invalidate();
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        invalidate();
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        invalidate();

    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        invalidate();

    }


    public Rect getRect() {
        left = (int) this.x;
        top = (int) y;
        right = (int) (this.x + getWidth());
        bottom = (int) (y + getHeight());
        rect.set(left, top, right, bottom);
        return rect;
    }

    public boolean onTouch(MotionEvent event) {
        if (onTouchListener != null) {
            return onTouchListener.onTouch(this, event);
        } else {
            return onTouchEvent(event);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (clickable || longClickable) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                isClick = true;
                startTrackClick();
                startTrackLongClick();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (onClickListener != null && isClick) {
                    onClickListener.onClick(ArtView.this);
                }
                isClick = false;
                cancelRunnable(tapRunnable);
                cancelRunnable(longClickRunnable);
            }
            return true;
        }
        return false;
    }

    private void startTrackClick() {
        if (tapRunnable == null) {
            tapRunnable = new Runnable() {
                @Override
                public void run() {
                    isClick = false;
                }
            };
        }
    }

    private void startTrackLongClick() {
        if (longClickRunnable == null) {
            longClickRunnable = new Runnable() {
                @Override
                public void run() {
                    if (onLongClickListener != null) {
                        isClick = false;
                        onLongClickListener.onLongClick(ArtView.this);
                    }
                }
            };
        }
        ThreadPool.postDelay(longClickRunnable, 500);
    }

    private void cancelRunnable(Runnable runnable) {
        ThreadPool.remove(runnable);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public static interface OnTouchListener {
        boolean onTouch(ArtView artView, MotionEvent event);
    }

    public static interface OnClickListener {
        void onClick(ArtView artView);
    }

    public static interface OnLongClickListener {
        void onLongClick(ArtView artView);
    }
}
