package artist;

import utils.Utils;

import java.awt.*;

public class ArtImage extends ArtView {


    /**
     * 缩放模式：以最小边为基准，按比例缩放
     */
    public static final int CENTER_INSIDE = 1;
    /**
     * 缩放模式：缩放到填充满整个View
     */
    public static final int FIT_XY = 2;
    /**
     * 缩放模式：缩放居中，比例保持不变
     */
    //public static final int CENTER_INSIDE = 3;

    private Image src;
    private float srcX, srcY;
    private int srcW, srcH;

    public int scaleType = CENTER_INSIDE;

    public ArtImage(Context context) {
        super(context);
//        init(set);
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
//            if (name.equals(R.styleable.src)) {
//                src = ta.getDrawable(i);
//            } else if (name.equals(R.styleable.scaleType)) {
//                String v = ta.getString(i);
//                if (v.equals("center")) {
//                    scaleType = CENTER_INSIDE;
//                } else if (v.equals("fit_xy")) {
//                    scaleType = FIT_XY;
//                }
//            }
//        }
//    }

    @Override
    public void onMeasure(Graphics g) {
        super.onMeasure(g);
        ArtistLayout parent = getRoot();
        if (src != null && parent != null) {
            srcW = src.getWidth(parent);
            srcH = src.getHeight(parent);
            float r = (float) srcW / (float) srcH;
            int tSrcW = srcW, tSrcH = srcH;
            if (getWidthMode() != WRAP_CONTENT) {
                if (srcW > getWidth() - paddingLeft - paddingRight) {
                    tSrcW = getWidth() - paddingLeft - paddingRight;
                }
            }
            if (getHeightMode() != WRAP_CONTENT) {
                if (srcH > getHeight() - paddingTop - paddingBottom) {
                    tSrcH = getHeight() - paddingTop - paddingBottom;
                }
            }
            if (tSrcW != srcW || tSrcH != srcH) {
                if (tSrcW < tSrcH) {
                    srcW = tSrcW;
                    srcH = (int) ((float) srcW / r);
                } else {
                    srcH = tSrcH;
                    srcW = (int) ((float) srcH * r);
                }
            }
            if (getWidthMode() == WRAP_CONTENT) {
                width = computeWapContentWidth();
            }
            if (getHeightMode() == WRAP_CONTENT) {
                height = computeWapContentHeight();
            }

        }
    }

    @Override
    public void onLayout(Graphics2D g2d) {
        super.onLayout(g2d);
        layoutImage(getWidth(), getHeight());
        updateGravity();
    }

    /**
     * 根据所给大小和缩放模式缩放Drawable大小
     *
     * @param vWidth
     * @param vHeight
     */
    private void layoutImage(int vWidth, int vHeight) {
        if (getRoot() == null || src == null || vWidth <= 0 || vHeight <= 0) {
            return;
        }
        int srcWidth = src.getWidth(getRoot());
        int srcHeight = src.getHeight(getRoot());
        switch (scaleType) {
            case CENTER_INSIDE:
                if (vWidth < vHeight) {
                    int height = (int) ((float) vWidth / ((float) srcWidth / (float) srcHeight));
                    srcX = (int) (x + getWidth() / 2 - vWidth / 2);
                    srcY = (int) (y + getHeight() / 2 - height / 2);
                    src = Utils.scaleImage(src, vWidth, height);
                } else {
                    int width = (int) ((float) vHeight * ((float) srcWidth / (float) srcHeight));
                    srcX = (int) (x + getWidth() / 2 - width / 2);
                    srcY = (int) (y + getHeight() / 2 - vHeight / 2);
                    src = Utils.scaleImage(src, width, vHeight);
                }
                break;
            case FIT_XY:
                srcX = x;
                srcY = y;
                src = Utils.scaleImage(src, vWidth, vHeight);
                break;
            default:
        }
        srcW = src.getWidth(getRoot());
        srcH = src.getHeight(getRoot());
    }

    @Override
    public void onDraw(Graphics g) {
        super.onDraw(g);
        Graphics2D g2d = (Graphics2D) g;
        ArtistLayout parent = getRoot();
        if (src != null && parent != null) {
            g2d.drawImage(src, (int) srcX, (int) srcY, srcW, srcH, parent);
        }
    }

    @Override
    public void updateGravity() {
        if (src == null && getParent() == null) {
            return;
        }
        float realTextX = this.x;
        float realTextY = this.y;
        final int majorGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        if (getHeightMode() == WRAP_CONTENT) {
            srcY = realTextY + paddingTop;
        } else {
            switch (majorGravity) {
                case Gravity.BOTTOM:
                    realTextY = realTextY + getHeight() - srcH + paddingTop - paddingBottom;
                    break;
                case Gravity.CENTER_VERTICAL:
                    realTextY = realTextY + getHeight() / 2 - srcH / 2 + paddingTop - paddingBottom;
                    break;
                case Gravity.TOP:
                    realTextY = realTextY + paddingTop - paddingBottom;
                    break;
                default:
                    break;
            }
            srcY = realTextY;
        }

        if (getWidthMode() == WRAP_CONTENT) {
            srcX = realTextX + paddingLeft;
        } else {
            switch (minorGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    realTextX = realTextX + getWidth() / 2 - srcW / 2 + paddingLeft - paddingRight;
                    break;
                case Gravity.RIGHT:
                    realTextX = realTextX + getWidth() - srcW + paddingLeft - paddingRight;
                    break;
                case Gravity.LEFT:
                    realTextX = realTextX + paddingLeft - paddingRight;
                    break;
                default:
                    break;
            }
            srcX = realTextX;
        }
    }


    @Override
    public int computeWapContentWidth() {
        return srcW + paddingLeft + paddingRight;
    }

    @Override
    public int computeWapContentHeight() {
        return srcH + paddingTop + paddingBottom;
    }
}
