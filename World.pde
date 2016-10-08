public class World implements CreatureAPI {
  
  private Map<UUID, Entity> entities;
  private Map<UUID, Creature> creatures;
  private DiffusionEngine environment;

  public World(int W, int H) {
    this.entities = new ConcurrentHashMap<UUID, Entity>();
    this.creatures = new ConcurrentHashMap<UUID, Creature>();
    
    // ew gross how do I not use global SIZE
    this.environment = new DiffusionEngine(W/SIZE, H/SIZE, SIZE, 0.2, 5);
  }
  
  public UUID addCreature(PVector location) {
    Creature creature = new Creature(location);
    UUID id = creature.getID();
    this.creatures.put(id, creature);
    this.entities.put(id, creature);
    return id;
  }

  public void cullCreatures() {
    for (Creature creature : this.creatures.values()) {
      if (creature.isDead()) {
        UUID id = creature.getID();
        this.creatures.remove(id);
        this.entities.remove(id);
      }
    }
  }
  public void updateEntities() {
    for (Entity entity : this.entities.values()) {
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
    for (Entity entity : this.entities.values()) {
      entity.render();
    }
  }

  public Set<Entity> getCollisions(Entity entity) {
    Set<Entity> collisions = new HashSet<Entity>();
    MapArea hitBox = entity.getHitBox();    
    for (Entity e : this.entities.values()) {
      if (e.getID() != entity.getID()) {
        if (WorldHelper.isColliding(hitBox, e.getHitBox())) {
          collisions.add(e);
        }
      }
    }
    return collisions;
  }
  
  public Set<Creature> getCreaturesInArea(MapArea mapArea) {
    Set<Creature> observedCreatures = new HashSet<Creature>();
    for (Creature c : this.creatures.values()) {
      if (WorldHelper.isColliding(mapArea, c.getHitBox())) {
        observedCreatures.add(c);
      }
    }
    return observedCreatures;
  }
  
  public PVector move(UUID id, PVector direction) {
    Creature creature = this.creatures.get(id);
    if (creature == null) return null;
    
    // TODO: Normalize direction
    creature.setAcceleration(direction);
    return direction;
  }
  public Set<CreatureObservation> getSurroundingCreatures(UUID id) {
    Set<CreatureObservation> observed = new HashSet<CreatureObservation>();
    Creature creature = this.creatures.get(id);
    if (creature == null) {
      return observed;
    }

    MapArea LOS = creature.getLineOfSite();
    for (Creature observation : this.getCreaturesInArea(LOS)) {
      UUID observedId = observation.getID();
      if (observedId != creature.getID()) {
        observed.add(observation);
      }
    }
    return observed;
  }
  public PVector getLocation(UUID id) {
    Creature creature = this.creatures.get(id);
    if (creature == null) {
      return null;
    }
    return creature.getLocation();
  }
  
  public CreatureState getCreature(UUID id) {
    Creature creature = this.creatures.get(id);
    if (creature == null) {
      return null;
    }
    return creature;
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