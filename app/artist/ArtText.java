package artist;

import utils.TextUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class ArtText extends ArtView {

    private String text = "";
    private Color textColor = Color.BLACK;
    private float textSize;
    /**
     * 单位文字的高
     */
    private float textHeight;
    /**
     * 单位文字的宽
     */
    private float textWidth;
    private float textX;
    private float textY;
    private float textPadding;
    private String typeface = Font.SERIF;
    private int typefaceStyle = Font.PLAIN;
    private FontMetrics fontMetrics;
    private int maxLines = -1;
    private String thumbnailText = "";
    private float innerTextSize = 0;


    public ArtText(Context context) {
        super(context);
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
//            if (name.equals(R.styleable.text)) {
//                text = ta.getString(i);
//            } else if (name.equals(R.styleable.textColor)) {
//                textColor = ta.getColor(i, JBColor.BLACK);
//            } else if (name.equals(R.styleable.textSize)) {
//                textSize = ta.getDimension(i, 12);
//            } else if (name.equals(R.styleable.textStyle)){
//                String style = ta.getString(i);
//                if (!TextUtils.isEmpty(style) && !style.contains(".ttf")){
//                    if (style.equals("bold")) {
//                        typefaceStyle = Font.BOLD;
//                    } else if (style.equals("serif")) {
//                        typeface = Font.SERIF;
//                    } else if (style.equals("italic")) {
//                        typefaceStyle = Font.ITALIC;
//                    } else if (style.equals("sans_serif")) {
//                        typeface = Font.SANS_SERIF;
//                    } else if (style.equals("monospace")) {
//                        typeface = Font.MONOSPACED;
//                    }
//                }
//            }
//        }
//    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        this.text = text;
        thumbnailText = null;
        invalidate();
    }

    public void setTextSize(float textSize) {
        setTextSize(2, textSize);
    }

    public void setTextSize(int unit, float textSize) {
        setRawTextSize(textSize);
    }

    private void setRawTextSize(float size) {
        if (size != innerTextSize) {
            textSize = size;
            innerTextSize = size;
//            textPaint.setTextSize(innerTextSize);
//            ellipsisWidth = textPaint.measureText("...");
//            // 得到系统默认字体属性
//            Paint.FontMetrics fm = textPaint.getFontMetrics();
//            fmTextHeight = fm.descent - fm.ascent;
//            fmFontHeight = fm.bottom - fm.top;
            invalidate();
        }
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setTextColor(String color) {
        setTextColor(Color.decode(color));
    }

    @Override
    public void onMeasure(Graphics g) {
        super.onMeasure(g);
        measureText((Graphics2D) g);
//        update(g);
//        checkTextXY();
    }

    @Override
    public void onLayout(Graphics2D g) {
        super.onLayout(g);
        checkTextXY();
    }

    private void measureText(Graphics2D g) {
        Font f = g.getFont();
        Font font = new Font(typeface, typefaceStyle, (int) textSize);
        g.setFont(font);
        fontMetrics = g.getFontMetrics(font);
        Rectangle2D stringBounds = fontMetrics.getStringBounds(text, g);
        textWidth = (float) stringBounds.getWidth();
        textHeight = (float) stringBounds.getHeight();
//        g.setFont(f);
        fontMetrics = g.getFontMetrics(font);
        checkTextXY();

    }

    @Override
    public void onDraw(Graphics g) {
        super.onDraw(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(textColor);
        Font font = new Font(typeface, typefaceStyle, (int) textSize);
        g2d.setFont(font);
        fontMetrics = g2d.getFontMetrics(font);
//        measureText((Graphics2D) g);
//        update(g);
//        checkTextXY();
//        Vector<String> textLinesVector = getTextLinesVector(g, text, height, width, maxLines);
//        String drawText = vectorToString(textLinesVector);
        String drawText = text;
        g2d.drawString(drawText, textX, textY);
    }

    private void checkTextXY() {
        updateGravity();
    }

    /**
     * 更新文字的gravity
     */
    @Override
    public void updateGravity() {
        float realTextX = this.x + textPadding * 0.4f;
        float realTextY = this.y + fontMetrics.getAscent() + textPadding * 0.5f;
        final int majorGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        if (getHeightMode() == WRAP_CONTENT) {
            textY = realTextY + paddingTop;
        } else {
            switch (majorGravity) {
                case Gravity.BOTTOM:
                    realTextY = realTextY + getHeight() - textHeight + paddingTop - paddingBottom;
                    break;
                case Gravity.CENTER_VERTICAL:
                    realTextY = realTextY + getHeight() / 2 - textHeight / 2 + paddingTop - paddingBottom;
                    break;
                case Gravity.TOP:
                    realTextY = realTextY + paddingTop - paddingBottom;
                    break;
                default:
                    break;
            }
            textY = realTextY;
        }

        if (getWidthMode() == WRAP_CONTENT) {
            textX = realTextX + paddingLeft;
        } else {
            switch (minorGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    realTextX = realTextX + getWidth() / 2 - textWidth / 2 + paddingLeft - paddingRight;
                    break;
                case Gravity.RIGHT:
                    realTextX = realTextX + getWidth() - textWidth + paddingLeft - paddingRight;
                    break;

                case Gravity.LEFT:
                    realTextX = realTextX + paddingLeft - paddingRight;
                    break;
                default:
                    break;
            }
            textX = realTextX;
        }
    }

    /**
     * 更新View的大小以及内容排布
     */
    private void update(Graphics g) {

        if (TextUtils.isEmpty(text)) {
            return;
        }
        // 计算文字的长度
//        textPaint.getTextBounds(text, 0, text.length(), textRect);
//        textWidth = textRect.width();
        measureText((Graphics2D) g);

        if (getWidthMode() == WRAP_CONTENT) {
            // view的宽度模式为WRAP_CONTENT，需要根据文字及其大小计算View的大小
            int parentWidth = getParentWidth();
            float width = 0;
            // 计算最大可用空间
            int availableWidth = parentWidth - paddingRight - paddingLeft;
            if (availableWidth > 0) {
                if (textWidth > availableWidth) {
                    width = parentWidth;
                    // 根据宽度调整文字
                    adjustText(parentWidth, g);
                    textWidth = availableWidth;
                } else {
                    width = textWidth + paddingRight + paddingLeft;
                }
            } else {
                textWidth = 0;
            }
            // 更新View的width
            this.width = (int) width;
//            computeWapContentWidth();
            // 更新right
            right = left + getWidth();
        } else {
            right = left + getWidth();
            if (textWidth > getWidth() - paddingRight - paddingLeft) {
                // 根据宽度调整文字
                adjustText(getWidth() - paddingRight - paddingLeft, g);
                textWidth = getWidth() - paddingRight - paddingLeft;
            }
        }

        if (getHeightMode() == WRAP_CONTENT) {
            int height = 0;
            int lines = 1;
            if (this.maxLines > 1) {
                lines = maxLines;
            } else {
                int availableWidth = getWidth() - paddingRight - paddingLeft;
                if (availableWidth > 0) {

                    if (!TextUtils.isEmpty(text)) {
                        Rect rect = new Rect();
                        measureText((Graphics2D) g);
                        float m = textWidth % availableWidth;
                        lines = (int) (textWidth / availableWidth);
                        if (m != 0) {
                            lines += 1;
                        }
                        if (lines == 0) {
                            lines = 1;
                        }
                    }
                }
            }
            measureText((Graphics2D) g);
            int totalTextHeight = (int)(textWidth * lines);
            height = totalTextHeight + paddingTop + paddingBottom;
            if (height > getParentHeight()) {
                height = getParentHeight();
            }
            this.height = height;
//            computeWapContentHeight();
            bottom = top + getHeight();
            textHeight = totalTextHeight;
        } else {
            bottom = top + getHeight();
            measureText((Graphics2D) g);
        }
    }

    private void adjustText(int maxLineWidth, Graphics g) {
        if (!TextUtils.isEmpty(text)) {
            measureText((Graphics2D) g);

            // 如果文字的总长度大于View所能达到的最大宽度
            if (maxLines != 1) {
                //                int lines = (int) (textWidth / (maxLineWidth - paddingRight - paddingLeft) + 0.5f);

            } else {
                // 如果文字超长，并且最多只能显示一行

                // 计算除去"..."剩余可用于显示文字的width
                Rect cW = new Rect();
                measureText((Graphics2D) g);
                int availableWidth = (int) (maxLineWidth - textWidth);

                int length = text.length();
                StringBuilder thumbnailTextSb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char ch = text.charAt(i);
                    String str = String.valueOf(ch);
                    Rectangle2D stringBounds = fontMetrics.getStringBounds(str, g);
                    // 如果剩余可用空间减去当前文字width仍然大于0，说明还有空间展示该文字
                    if ((availableWidth -= stringBounds.getWidth()) > 0) {
                        thumbnailTextSb.append(str);
                    }
                }
                // 生成略缩文字
                thumbnailText = thumbnailTextSb.toString() + "...";
            }
        }
    }

    /**
     * 将文字拆分成每一行放到Vector里
     */
    public Vector<String> getTextLinesVector(Graphics g, String content, float maxHeight, float maxWidth,
                                             int mMaxLinesNum) {
        Vector<String> mString = new Vector<>();
        int mRealLine = 0;// 字符串真实的行数
        char ch;
        int w = 0;
        int istart = 0;
        float mFontHeight = getFontHeight(g);
        //显示的最大行数
        if (mMaxLinesNum < 1) {
            // 如果没有限制最大行数，需要计算最大行数
            if (maxHeight <= 0) {
                // 高度不够也需要最少一行的空间
                mMaxLinesNum = 1;
            } else {
                // 确保至少有一行的空间
                // 如果有多出来的空间，就需要加一行
                float m = maxHeight % mFontHeight;
                mMaxLinesNum = (int)(maxHeight / mFontHeight);
                if (m != 0) {
                    mMaxLinesNum += 1;
                }
                if (mMaxLinesNum == 0) {
                    mMaxLinesNum = 1;
                }
            }
        }
        // 如果文字不为空，至少需要一行显示
        if (!TextUtils.isEmpty(content) && mMaxLinesNum == 0) {
            mMaxLinesNum = 1;
        }
        int count = content.length();
        for (int i = 0; i < count; i++) {
            ch = content.charAt(i);
            String str = String.valueOf(ch);
            Rectangle2D stringBounds = fontMetrics.getStringBounds(str, g);
            if (ch == '\n') {
                mRealLine++;// 真实的行数加一
                mString.addElement(content.substring(istart, i));
                istart = i + 1;
                w = 0;
            } else {

                w += (int)Math.ceil(stringBounds.getWidth());
                if (w > maxWidth) {
                    mRealLine++;// 真实的行数加一
                    mString.addElement(content.substring(istart, i));
                    istart = i;
                    i--;
                    w = 0;
                } else {
                    if (i == count - 1) {
                        mRealLine++;// 真实的行数加一
                        mString.addElement(content.substring(istart, count));
                    }
                }
            }
            //当真实行数大于显示的最大行数时跳出循环
            if (mMaxLinesNum != -1 && mRealLine == mMaxLinesNum) {
                break;
            }
        }
        return mString;
    }

    /**
     * 给每一行文字末尾加上 "\n" 分行
     *
     * @param strs
     * @return
     */
    private String vectorToString(Vector<String> strs) {
        if (strs.size() == 1) {
            return strs.get(0);
        }
        StringBuffer ss = new StringBuffer();
        for (String s : strs) {
            ss.append(s + "\n");
        }
        return ss.toString();
    }

    /**
     * 得到文字的高度
     */
    private float getFontHeight(Graphics g) {
        // 得到系统默认字体属性
        return g.getFontMetrics().getHeight();
    }


    @Override
    public int computeWapContentWidth() {
        int width = (int) (textWidth + paddingLeft + paddingRight);
        return width;
    }

    @Override
    public int computeWapContentHeight() {
        int height = (int) (textHeight + paddingTop + paddingBottom);
        return height;
    }
}
