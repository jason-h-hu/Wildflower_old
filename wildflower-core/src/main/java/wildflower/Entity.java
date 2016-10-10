package wildflower;

import wildflower.geometry.Shape;

import java.util.UUID;

import org.joml.Vector2f;

public abstract class Entity {
    private Vector2f force;
    private Vector2f velocity;
    protected Vector2f location;
    private float mass;
    private long timeOfBirth;
    private long timeOfDeath;
    private UUID id;

    public Entity(Vector2f location, long timeOfBirth) {
        this.force = new Vector2f(0, 0);
        this.velocity = new Vector2f(0, 0);
        this.mass = 1;
        this.location = location;
        this.timeOfBirth = timeOfBirth;
        this.timeOfDeath = Long.MIN_VALUE;
        this.id = UUID.randomUUID();
    }

    public void update() {
        this.velocity.add(this.force.mul(1 / this.mass));
        // TODO: limit velocity
        this.location.add(this.velocity);
        this.force.set(0, 0);
    }

    public long getTimeOfBirth() {
        return timeOfBirth;
    }

    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    public void applyForce(Vector2f force) {
        this.force.add(force);
    }

    public Vector2f getLocation() {
        return this.location;
    }

    public Vector2f getVelocity() {
        return this.velocity;
    }

    public UUID getID() {
        return this.id;
    }

    public abstract Shape getHitBox();
    public abstract boolean isDead();
}
