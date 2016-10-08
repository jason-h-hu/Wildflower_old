package wildflower.creature;

import java.util.UUID;
import java.util.Set;

import org.joml.Vector2f;

public interface CreatureApi {
    public void move(UUID id, Vector2f direction);
    public Set<CreatureObservation> getSurroundingCreatures(UUID id);
    public Vector2f getLocation(UUID id);
    public UUID addCreature(Vector2f location);
    public CreatureState getCreatureState(UUID id);
}
