package wildflower.geometry;

import org.joml.Vector2f;

import static wildflower.geometry.Geometry.clamp;

public class Circle implements Shape {
    private Vector2f center;
    private float radius;

    public Circle(Vector2f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector2f getCenter() {
        return this.center;
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    public boolean isOverlapping(Shape other) {
        return other.isOverlappingCircle(this);
    }

    @Override
    public boolean isOverlappingCircle(Circle circle) {
        float centerDistance = this.center.distance(circle.getCenter());
        float sumOfRadii = this.radius + circle.getRadius();
        return centerDistance < sumOfRadii;
    }

    @Override
    public boolean isOverlappingAxisAlignedBox(AxisAlignedBox axisAlignedBox) {
        return axisAlignedBox.containsPoint(center) ||
                clamp(new Vector2f(center),
                        axisAlignedBox.getUpperLeft(),
                        axisAlignedBox.getLowerRight()
                ).distance(center) < radius;
    }
}
