package wildflower.api;

import wildflower.geometry.Shape;
import wildflower.geometry.Circle;

import org.joml.Vector2f;

import java.util.List;
import java.util.ArrayList;

public class ShapeModel {
    public String type;
    public List<Vector2f> vertices;
    public Vector2f center;
    public float radius;
    public List<ShapeModel> components;

    public ShapeModel() {
        // No arg constructor Gson
    }

    public ShapeModel(Shape shape) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            this.type = "circle";
            this.vertices = new ArrayList<>();
            this.center = circle.getCenter();
            this.radius = circle.getRadius();
            this.components = new ArrayList<>();
        }
    }
}
