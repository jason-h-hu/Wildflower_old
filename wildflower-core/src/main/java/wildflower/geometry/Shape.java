package wildflower.geometry;

public interface Shape {
    boolean isOverlapping(Shape other);
    boolean isOverlappingCircle(Circle other);
}
