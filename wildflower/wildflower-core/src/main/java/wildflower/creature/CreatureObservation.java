package wildflower.creature;

import wildflower.geometry.Shape;

import org.joml.Vector2f;

public interface CreatureObservation {
    public Vector2f getLocation();
    public Shape getHitBox();
}
