package wildflower.api;

import wildflower.creature.Creature;

import org.joml.Vector2f;

public class CreatureObservationModel {
    public Vector2f location;
    public ShapeModel hitBox;

    public CreatureObservationModel() {
        // No arg constructor for Gson
    }

    public CreatureObservationModel(Creature creature) {
        this.location = creature.getLocation();
        this.hitBox = new ShapeModel(creature.getHitBox());
    }
}
