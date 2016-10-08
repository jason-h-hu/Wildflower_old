public class World {
  
  private Set<Entity> entities;
  private Set<Creature> creatures;
  private DiffusionEngine environment;

  public World(int W, int H) {
    this.entities = new HashSet<Entity>();
    this.creatures = new HashSet<Creature>();
    
    // ew gross how do I not use global SIZE
    this.environment = new DiffusionEngine(W/SIZE, H/SIZE, SIZE, 0.2, 5);

  }
  
  public void addCreature(Creature creature) {
    this.entities.add(creature);
    this.creatures.add(creature);
  }

  public void cullCreatures() {
    HashSet<Creature> removeList = new HashSet<Creature>();
    for (Creature creature : this.creatures) {
      if (creature.isDead()) removeList.add(creature);
    }
    this.creatures.removeAll(removeList);
    this.entities.removeAll(removeList);
  }
  public void updateEntities() {
    for (Entity entity : this.entities) {
      Set<Entity> collisions = this.getCollisions(entity);
      entity.update();
      if (collisions.size() > 0) {
      }
    }
  }
  
  public void update() {
    this.cullCreatures();
    this.updateEntities();
    this.environment.update();
  }
  
  public void render() {
    environment.render();
    for (Entity entity : this.entities) {
      entity.render();
    }
  }

  public Set<Entity> getCollisions(Entity entity) {
    Set<Entity> collisions = new HashSet<Entity>();
    MapArea hitBox = entity.getHitBox();    
    for (Entity e : this.entities) {
      if (e.getID() != entity.getID()) {
        if (WorldHelper.isColliding(hitBox, e.getHitBox())) {
          collisions.add(e);
        }
      }
    }
    return collisions;
  }
  
  public Set<CreatureObservation> getCreaturesInArea(MapArea mapArea) {
    Set<CreatureObservation> observedCreatures = new HashSet<CreatureObservation>();
    for (Creature c : this.creatures) {
      if (WorldHelper.isColliding(mapArea, c.getHitBox())) {
        observedCreatures.add(c);
      }
    }
    return observedCreatures;
  }
}

// TODO: This currently describes only a circle in the world
// We want it to eventually be able to prepresent any generic
// area in the map.
public class MapArea {
  // x, y is the center, z is the radius
  private PVector mapArea;
  public MapArea(PVector mapArea) {
    this.mapArea = mapArea;
  }
  
  public PVector getCenter() {
    return new PVector(this.mapArea.x, this.mapArea.y);
  }
  
  public float getRadius() {
    return this.mapArea.z;
  }
}

public static class WorldHelper {
  public static boolean isColliding(MapArea a, MapArea b) {
    PVector aCenter = a.getCenter();
    float aDistance = a.getRadius();
    
    PVector bCenter = b.getCenter();
    float bDistance = b.getRadius();

    float distance = aDistance+bDistance;
    return (PVector.sub(aCenter, bCenter).mag() < distance);
  }
}