package wildflower.geometry;

import org.joml.Vector2f;

import static wildflower.geometry.Geometry.rangesOverlap;
import static wildflower.geometry.Geometry.within;

public class AxisAlignedBox implements Shape {
    private Vector2f upperLeft;
    private Vector2f lowerRight;

    public AxisAlignedBox(Vector2f upperLeft, Vector2f lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public Vector2f getUpperLeft() {
        return upperLeft;
    }

    public Vector2f getLowerRight() {
        return lowerRight;
    }

    public boolean containsPoint(Vector2f point) {
        return within(point.x, upperLeft.x, lowerRight.x)
                && within(point.y, upperLeft.y, lowerRight.y);
    }

    @Override
    public boolean isOverlapping(Shape other) {
        return other.isOverlappingAxisAlignedBox(this);
    }

    @Override
    public boolean isOverlappingAxisAlignedBox(AxisAlignedBox axisAlignedBox) {
        return rangesOverlap(axisAlignedBox.getUpperLeft().x, axisAlignedBox.getLowerRight().x, upperLeft.x, lowerRight.x)
            && rangesOverlap(axisAlignedBox.getUpperLeft().y, axisAlignedBox.getLowerRight().y, upperLeft.y, lowerRight.y);
    }

    @Override
    public boolean isOverlappingCircle(Circle circle) {
        return circle.isOverlappingAxisAlignedBox(this);
    }
}
