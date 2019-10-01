package artist;

import utils.TextUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author coorchice
 * @date 2018/02/28
 */

public class ArtGroup extends ArtView implements ArtParent {

    private List<ArtView> childList = new CopyOnWriteArrayList();
    private List<ArtView> consumingList = new CopyOnWriteArrayList<>();
    private boolean hasOnClickListener = false;


    public ArtGroup(Context context) {
        super(context);
//        readAttrs(attrs);
        init();
    }

//    private void readAttrs(AttributeSet attrs) {
//        if (attrs != null) {
//        }
//    }

    private void init() {

    }

    @Override
    public boolean isAttached() {
        return getParent() != null;
    }

    @Override
    public void update() {
        //ArtParent parent = getParent();
        //if (parent != null) {
        //    state = REQUEST_COMPUTING;
        //    parent.update();
        //}
        invalidate();
    }

    /**
     * 同一个View只能添加到一个ArtistLayout中。否则抛出异常。见{@link ArtView#attach(ArtParent)} ()}。
     *
     * @param view
     */
    @Override
    public final void addView(ArtView view) {
        view.attach(this);
        childList.add(view);
        update();
    }

    @Override
    public Color getBackground() {
        return background;
    }

    /**
     * 同一个View只能添加到一个ArtistLayout中。否则抛出异常。见{@link ArtView#attach(ArtParent)} ()}。
     *
     * @param views
     */
    public final void addViews(List<ArtView> views) {
        childList.addAll(views);
        for (int i = 0; i < childList.size(); i++) {
            childList.get(i).attach(this);
        }
        update();
    }

    public final void removeView(ArtView view) {
        view.detach();
        childList.remove(view);
        update();
    }

    public final void clearViews() {
        for (int i = 0; i < childList.size(); i++) {
            childList.get(i).detach();
        }
        childList.clear();
        update();
    }

    @Override
    public void onMeasure(Graphics g) {
        super.onMeasure(g);
        /**
         * 如果尺寸包含WRAP_CONTENT，需要进行特殊的计算
         * 否则就跳过这些操作，节省算力
         */
        boolean hasWrapContentMode = false;

        /**
         * WRAP_CONTENT需要进行特殊的计算，首先要假设其为最大可获得尺寸
         * 子View将基于这个尺寸进行一次测量和虚拟布局计算
         * 在虚拟布局完成后，就可以知道子View布局所需空间大小
         * 从而设置该模式下的Group尺寸
         */
        if (getWidthMode() == WRAP_CONTENT) {
            hasWrapContentMode = true;
            int tempWidth = 0;
            /**
             * 先获取Parent的可用空间，为0则直接假设为屏幕空间
             */
            if (getParent() != null && getParent().getWidth() > 0) {
                tempWidth = getParent().getWidth();
            } else {
                tempWidth = getContext().getScreenWidth();
            }
            /**
             * 设置尺寸时需要保留尺寸模式
             */
            width = tempWidth;
        }

        if (getHeightMode() == WRAP_CONTENT) {
            hasWrapContentMode = true;

            int tempHeight = 0;
            if (getParent() != null && getParent().getHeight() > 0) {
                tempHeight = getParent().getHeight();
            } else {
                tempHeight = getContext().getScreenHeight();
            }
            height = tempHeight;
        }

        /**
         * 在layout之后，子View的x、y会根据Group的左边进行变换，结果缓存在了子View中
         * 下一次的测量需要先重置到原点，然后再测量确定相对与原点坐标系的布局结果
         * 这样，当layout的时候只需转换一下坐标即可。
         */
        for (ArtView child : childList) {
            child.x = 0;
            child.y = 0;
        }

        /**
         * 计算所有子View铺开需要的空间
         */
        int left = 0, top = 0, right = 0, bottom = 0;
        for (ArtView child : childList) {
            child.measure(getWidth(), getHeight(), (Graphics2D) g);

            if (getWidthMode() == WRAP_CONTENT || getHeightMode() == WRAP_CONTENT) {
                /**
                 * 测量之后，需要通知挂到该锚点上的View刷新坐标
                 */
                if (child.anchor != null) {
                    child.anchor.notifyChanged();
                } else {
                    child.notifyAllAnchor();
                }
                float childX = child.x;
                float childY = child.y;
                left = (int) Math.min(left, childX);
                top = (int) Math.min(top, childY);
                right = (int) Math.max(right, childX + child.getWidth());
                bottom = (int) Math.max(bottom, childY + child.getHeight());
            }
        }

        // 需要加上Padding值
        if (getWidthMode() == ArtView.WRAP_CONTENT) {
            width = right - left + paddingLeft + paddingRight;
        }
        if (getHeightMode() == ArtView.WRAP_CONTENT) {
            height = bottom - top + paddingTop + paddingBottom;
        }

    }

    @Override
    public void layout(int l, int t, int r, int b, Graphics2D g2d) {
        super.layout(l, t, r, b, g2d);
        for (int i = 0; i < childList.size(); i++) {
            ArtView child = childList.get(i);
            child.checkParentSize();
            child.layout((int) x + paddingLeft - paddingRight, (int) y + paddingTop - paddingBottom,
                    (int) (x + getWidth()), (int) (y + bottom), g2d);
            // 测量之后获得了预布局的坐标，进行坐标转换，使子View被正确的绘制到Group中
            int offsetX = (int) x;
            int offsetY = (int) y;
            ArtParent parent = getParent();
            while (parent != null && parent instanceof ArtGroup) {
                /**
                 * 如果有爷View，由于此时还在进行虚拟布局，需要把爷View的坐标补偿进来
                 * 否则虚拟布局完成后再转换坐标，就会丢失爷View的坐标
                 */
                ArtGroup gParent = (ArtGroup) parent;
                offsetX += gParent.getX();
                offsetY += gParent.getY();

                parent = gParent.getParent();

            }
            child.setXWithoutInvalidate(child.x + offsetX + paddingLeft);
            child.setYWithoutInvalidate(child.y + offsetY + paddingTop);
            child.onLayout(g2d);
        }
    }

    @Override
    public void onDraw(Graphics g) {
        super.onDraw(g);
        // 这段错误太多了，抓住分析
        try {
            // 将绘制范围锁定
            BufferedImage bi = new BufferedImage(getContext().getScreenWidth(), getContext().getScreenHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D canvas = (Graphics2D) bi.getGraphics();
            // 去除抗锯齿
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (int i = 0; i < childList.size(); i++) {
                ArtView child = childList.get(i);
                child.draw(canvas);
            }
            bi.flush();
            if (getWidth() > 0 && getHeight() > 0) {
                BufferedImage subimage = bi.getSubimage((int) x, (int) y, getWidth(), getHeight());
                Graphics2D subCanvas = (Graphics2D) subimage.getGraphics();
                subCanvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                subCanvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.drawImage(subimage, (int) x, (int) y, getContext().getPhoneLayout());
                canvas.dispose();
                subimage.flush();
                subCanvas.dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public ArtView findViewById(String id) {
        if (!TextUtils.isEmpty(id) && id.equals(getId())) {
            return null;
        }

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
        return super.findViewById(id);
    }

    @Override
    public ArtView checkPointInRect(int x, int y) {
        for (int i = childList.size() - 1; i >= 0; i--) {
            ArtView artView = childList.get(i).checkPointInRect(x, y);
            if (artView != null) {
                return artView;
            }
        }
        return super.checkPointInRect(x, y);
    }

    @Override
    public ArtView findViewByCode(String code) {
        for (ArtView child : childList) {
            ArtView artView = child.findViewByCode(code);
            if (artView != null) {
                return artView;
            }
        }
        return super.findViewByCode(code);
    }

    @Override
    public void clearFlags() {
        for (ArtView child : childList) {
            child.clearFlags();
        }
        super.clearFlags();
    }
}


