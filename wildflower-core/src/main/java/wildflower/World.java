package wildflower;

import wildflower.creature.Creature;
import wildflower.creature.CreatureApi;
import wildflower.geometry.Shape;

import java.util.UUID;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector2f;

public class World implements CreatureApi {
    private Map<UUID, Entity> entities;
    private Map<UUID, Creature> creatures;

    public World() {
        this.entities = new ConcurrentHashMap<>();
        this.creatures = new ConcurrentHashMap<>();
    }

    @Override
    public Creature addCreature(Vector2f location) {
        Creature creature = new Creature(location, System.nanoTime());
        UUID id = creature.getID();
        this.creatures.put(id, creature);
        this.entities.put(id, creature);
        return creature;
    }

    @Override
    public Creature getCreature(UUID id) {
        return this.creatures.get(id);
    }

    @Override
    public Set<Creature> getSurroundingCreatures(UUID id) {
        Set<Creature> observed = new HashSet<>();
        Creature creature = this.creatures.get(id);
        if (creature == null) {
            return observed;
        }
        observed.addAll(this.getCreaturesInArea(creature.getVisionCone()));
        observed.remove(creature);
        return observed;
    }

    @Override
    public void move(UUID id, Vector2f force) {
        Creature creature = this.creatures.get(id);
        if (creature != null) {
            creature.applyForce(force);
        }
    }

    public Collection<Entity> getEntities() {
        return this.entities.values();
    }

    public void update(double delta) {
        this.reapDeadCreatures();
        this.updateEntities();
    }

    public void reapDeadCreatures() {
        this.creatures.values().forEach(creature -> {
            if (creature.isDead()) {
                UUID id = creature.getID();
                this.creatures.remove(id);
                this.entities.remove(id);
            }
        });
    }

    public void updateEntities() {
        this.entities.values().forEach(Entity::update);
    }

    private Set<Creature> getCreaturesInArea(Shape area) {
        return this.creatures.values().stream()
            .filter(creature -> creature.getHitBox().isOverlapping(area))
            .collect(Collectors.toSet());
    }
}
