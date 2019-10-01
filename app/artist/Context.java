package artist;


import java.awt.image.ImageObserver;

public class Context {

    private PhoneLayout phoneLayout;
    private Runnable func;
    public float density = 1f;
    public float scaledDensity = 1f;

    public Context(){

    }

    public Context(Runnable invalidateFunc) {
        this.func = invalidateFunc;
    }

    public void setDensity(float density) {
        this.density = density > 0 ? density : 1f;
    }

    public void setScaledDensity(float scaledDensity) {
        this.scaledDensity = scaledDensity > 0 ? scaledDensity : 1f;
    }

    public ImageObserver getPhoneLayout() {
        return phoneLayout;
    }

    public void setPhoneLayout(PhoneLayout phoneLayout) {
        this.phoneLayout = phoneLayout;
    }

    public int getScreenWidth() {
        return phoneLayout.getWidth();
    }

    public int getScreenHeight() {
        return phoneLayout.getHeight();
    }

    public void invalidate() {
        func.run();
    }

    public float dp(float dp){
        return dp * density + 0.5F;
    }

    public float sp(float sp){
        return sp * scaledDensity + 0.5F;
    }

}
