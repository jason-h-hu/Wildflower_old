public abstract class Entity {  
  private PVector acceleration;
  private PVector velocity;
  private PVector location;
  private UUID id;

  public Entity(PVector location) {
    this.acceleration = new PVector(0, 0);
    this.velocity = new PVector(0, 0);
    this.location = location;
    this.id = UUID.randomUUID();
  }
  public void update() {
    this.velocity = PVector.add(this.velocity, this.acceleration);
    this.velocity = this.velocity.limit(2);
    this.location = PVector.add(this.location, this.velocity);
    this.acceleration = new PVector(0, 0);
  }
  public void setAcceleration(PVector acceleration) {
    this.acceleration = acceleration;
  }
  public PVector getLocation() {
    return this.location;
  }
  public PVector getVelocity() {
    return this.velocity;
  }
  public PVector getAcceleration() {
    return this.acceleration;
  }
  public UUID getID() {
    return this.id;
  }

  public abstract MapArea getHitBox();
  public abstract void render();
  public abstract boolean isDead();
}