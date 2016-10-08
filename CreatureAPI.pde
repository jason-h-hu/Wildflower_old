public interface CreatureAPI {
  
  public PVector move(UUID id, PVector direction);
  public Set<CreatureObservation> getSurroundingCreatures(UUID id);
  public PVector getLocation(UUID id);
  public UUID addCreature(PVector location);
  public CreatureState getCreature(UUID id);
}