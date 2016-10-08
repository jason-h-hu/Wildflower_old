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

    public float getPositionX() {
        return px;
    }

    public float getPositionY() {
        return py;
    }

    public void update() {
        this.px-= 0.001;
        this.py-= 0.001;
    }
}
