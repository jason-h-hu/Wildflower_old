package wildflower.creature;

import java.util.UUID;
import java.util.Set;

import org.joml.Vector2f;

public interface CreatureApi {
    public void move(UUID id, Vector2f direction);
    public Set<Creature> getSurroundingCreatures(UUID id);
    public Creature addCreature(Vector2f location);
    public Creature getCreature(UUID id);
}
