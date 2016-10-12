package wildflower;

import wildflower.geometry.Shape;
import wildflower.geometry.Circle;

import org.joml.Vector2f;

public class Creature extends Entity {
    private float radius;
    private float lineOfSight;

    public Creature(Vector2f location, long timeOfBirth) {
        super(location, timeOfBirth);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public Shape getHitBox() {
        return new Circle(location, radius);
    }

    public Shape getVisionCone() {
        return new Circle(location, lineOfSight);
    }
}
