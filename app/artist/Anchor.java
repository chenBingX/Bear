package artist;

import static artist.ArtView.*;

public class Anchor {

    public static final int MARGIN_TYPE_NULL = -1;

    /**
     * leftMargin、topMargin
     */
    public static final int MARGIN_TYPE_F = 1;
    /**
     * rightMargin、bottomMargin
     */
    public static final int MARGIN_TYPE_R = 2;

    public static final int MARGIN_TYPE_CENTER = 2;

    public float mX;
    public float mY;
    public float tX;
    public float tY;
    public float offsetX;
    public float offsetY;
    public boolean followVisible;

    public ArtView m;
    public ArtView t;
    public float tOldWidth;
    public float tOldHeight;
    public float mOldWidth;
    public float mOldHeight;

    public int xMarginType = -1;
    public int yMarginType = -1;
    public boolean attach = false;

    public boolean centerAnchorHorizontal = false;
    public boolean centerAnchorVertical = false;
    public int hAlign = ALIGN_NO, vAlign = ALIGN_NO;


    public Anchor(ArtView m, ArtView t, float mX, float mY, float tX, float tY, float offsetX,
                  float offsetY, boolean followVisible) {
        this.m = m;
        this.t = t;
        this.mX = mX;
        this.mY = mY;
        this.tX = tX;
        this.tY = tY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.followVisible = followVisible;
        mOldWidth = m.getWidth();
        mOldHeight = m.getHeight();
        tOldWidth = t.getWidth();
        tOldHeight = t.getHeight();
    }

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }

    public float gettX() {
        return tX;
    }

    public void settX(float tX) {
        this.tX = tX;
    }

    public float gettY() {
        return tY;
    }

    public void settY(float tY) {
        this.tY = tY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public ArtView getM() {
        return m;
    }

    public ArtView getT() {
        return t;
    }

    public void notifyChanged() {
        if (m != null && t != null) {
            changed();
            m.notifyAllAnchor();
        }
    }

    public void changed() {
        float offsetX = this.offsetX;
        float offsetY = this.offsetY;
        if (offsetX != MARGIN_TYPE_NULL && t.visibility == GONE){
            offsetX = 0;
        }
        if (offsetY != MARGIN_TYPE_NULL && t.visibility == GONE){
            offsetY = 0;
        }
        if (offsetX != MARGIN_TYPE_NULL) {
            if (xMarginType == MARGIN_TYPE_R) {
                m.x = t.x - offsetX - m.getWidth();
            } else {
                m.x = t.x + t.getWidth() + offsetX;
            }
        }
        if (m != null && t != null) {
            if (offsetX != MARGIN_TYPE_NULL) {
                if (xMarginType == MARGIN_TYPE_R) {
                    m.x = t.x - offsetX - m.getWidth();
                } else  {
                    m.x = t.x + t.getWidth() + offsetX;
                }
            }
            if (centerAnchorHorizontal) {
                m.x = t.x + t.getWidth() / 2 - m.getWidth() / 2;
            }
            if (hAlign == ALIGN_LEFT) {
                m.x = t.x;
            } else if (hAlign == ALIGN_RIGHT) {
                m.x = t.x + t.getWidth() - m.getWidth();
            }

            if (offsetY != MARGIN_TYPE_NULL) {
                if (yMarginType == MARGIN_TYPE_R) {
                    m.y = t.y - offsetY - m.getWidth();
                } else  {
                    m.y = t.y + t.getHeight() + offsetY;
                }
            }
            if (centerAnchorVertical) {
                m.y = t.y + t.getHeight() / 2 - m.getHeight() / 2;
            }
            if (vAlign == ALIGN_TOP) {
                m.y = t.y;
            } else if (vAlign == ALIGN_BOTTOM) {
                m.y = t.y + t.getHeight() - m.getHeight();
            }

            m.left = (int) m.x;
            m.top = (int) m.y;
            m.right = m.left + m.getWidth();
            m.bottom = m.top + m.getHeight();

        }
    }

    public void setAttach(boolean attach) {
        this.attach = attach;
    }

    public boolean isAttach() {
        return attach;
    }
}
