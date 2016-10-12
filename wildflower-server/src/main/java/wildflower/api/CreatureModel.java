package wildflower.api;

import wildflower.Creature;

import java.util.UUID;

import org.joml.Vector2f;

public class CreatureModel {
    public UUID id;
    public Vector2f location;
    public long timeOfBirth;
    public boolean isAlive;
    public long timeOfDeath;

    public CreatureModel() {
        // No arg constructor for Gson
    }

    public CreatureModel(Creature basis) {
        this.id = basis.getID();
        this.location = basis.getLocation();
        this.timeOfBirth = basis.getTimeOfBirth();
        this.isAlive = !basis.isDead();
        this.timeOfDeath = basis.getTimeOfDeath();
    }
}
