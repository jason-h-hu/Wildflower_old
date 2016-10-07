public class Agent {
  private CreatureAPI creatureAPI;
  private UUID id;

  public Agent(CreatureAPI creatureAPI, UUID id) {
    this.creatureAPI = creatureAPI;
    this.id = id;
  }
    
  public void act() {
    CreatureObservation closest = null;
    PVector location = this.creatureAPI.getLocation();
    for (CreatureObservation observation : this.creatureAPI.getSurroundingCreatures()) {
      if (closest == null) {
        closest = observation;
      } else {
        float distance = PVector.sub(location, observation.getLocation()).mag();
        float currentDistance = PVector.sub(location, closest.getLocation()).mag();
        if (distance < currentDistance) {
          closest = observation;
        }
      }
    }
    PVector acceleration = closest == null ? 
      new PVector(random(-1, 1), random(-1, 1)) : 
      PVector.sub(location, closest.getLocation()).normalize();
      
    this.creatureAPI.move(acceleration);
  }
}