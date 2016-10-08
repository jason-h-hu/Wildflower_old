public class Creature extends Entity implements CreatureObservation, CreatureState {
  private float radius = 20;
  private float lineOfSite = 80;
  
  private int fill = 155;
  
  public Creature(PVector location) {
    super(location);
  }
  public void render() {
    PVector location = this.getLocation();
    strokeWeight(1);
    fill(0, 0, 0, 0);
    stroke(100);
    ellipse(location.x, location.y, this.lineOfSite*2, this.lineOfSite*2);
    strokeWeight(0);
    fill(this.fill);
    ellipse(location.x, location.y, this.radius*2, this.radius*2);
  }
  public void update() {
    // TODO: Update health/calories based off acceleration
    super.update();
  }
  public boolean isDead() {
    // TODO: Calculate whether it's dead based off of calories
    return false;
  }
  
  public void paint(int fill) {
    this.fill = fill;
  }
    
  public MapArea getHitBox() {
    PVector location = this.getLocation();
    return new MapArea(new PVector(location.x, location.y, this.radius));
  }
    
  public MapArea getLineOfSite() {
    PVector location = this.getLocation();
    return new MapArea(new PVector(location.x, location.y, this.lineOfSite));
  }
}

// What you look like to the outside
public interface CreatureObservation {
  public PVector getLocation();
  public MapArea getHitBox();
}

public interface CreatureState {}