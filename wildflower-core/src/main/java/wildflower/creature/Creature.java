package wildflower.creature;

import wildflower.Entity;
import wildflower.geometry.Shape;
import wildflower.geometry.Circle;

import org.joml.Vector2f;

public class Creature extends Entity implements CreatureObservation, CreatureState {
    private float radius;
    private float lineOfSight;

    public Creature(Vector2f location) {
        super(location);
    }

    @Override
    public Vector2f getLocation() {
        return super.getLocation(); // this is dumb
    }

    @Override
    public void update() {
        super.update();
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
