package wildflower;

public class Ball {
    private float px;
    private float py;

    public Ball(float px, float py) {
        this.px = px;
        this.py = py;
    }

    public void setPosition(float newPx, float newPy) {
        this.px = newPx;
        this.py = newPy;
    }
}
