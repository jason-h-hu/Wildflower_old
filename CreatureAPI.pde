public class CreatureAPI {
  private World world;
  private Creature creature;
  private UUID id;
  
  public CreatureAPI(World world, Creature creature, UUID id) {
    this.world = world;
    this.creature = creature;
    this.id = id;
  }
  public PVector move(PVector direction) { 
    // TODO: Get the maximum acceleration of the creature
    // TODO: Normalize vector against that creature
    this.creature.setAcceleration(direction);
    return direction;
  }
  public Set<CreatureObservation> getSurroundingCreatures() {
    MapArea LOS = this.creature.getLineOfSite();
    Set<CreatureObservation> observed = new HashSet<CreatureObservation>();
    for (CreatureObservation observation : this.world.getCreaturesInArea(LOS)) {
      PVector observedLocation = observation.getLocation();
      if (observedLocation != this.getLocation()) {
        observed.add(observation);
      }
    }
    return observed;
  }
  public PVector getLocation() {
    return this.creature.getLocation();
  }
}