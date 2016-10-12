package wildflower.geometry;

public interface Shape {
    boolean isOverlapping(Shape other);
    boolean isOverlappingCircle(Circle circle);
    boolean isOverlappingAxisAlignedBox(AxisAlignedBox axisAlignedBox);

}
