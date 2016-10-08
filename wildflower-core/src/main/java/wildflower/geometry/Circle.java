package wildflower.geometry;

import org.joml.Vector2f;

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
    public boolean isOverlappingCircle(Circle other) {
        float centerDistance = this.center.distance(other.getCenter());
        float sumOfRadii = this.radius + other.getRadius();
        return centerDistance < sumOfRadii;
    }
}
