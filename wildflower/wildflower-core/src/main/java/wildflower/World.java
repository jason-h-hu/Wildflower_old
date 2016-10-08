package wildflower;

import wildflower.creature.Creature;
import wildflower.creature.CreatureObservation;
import wildflower.creature.CreatureState;
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
    private boolean running;

    public World() {
        this.entities = new ConcurrentHashMap<>();
        this.creatures = new ConcurrentHashMap<>();
        running = false;
    }

    public void start() {
        running = true;
        while(running) {
            this.update();
        }
    }

    public Collection<Entity> getEntities() {
        return this.entities.values();
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

    public void update() {
        this.reapDeadCreatures();
        this.updateEntities();
    }

    private Set<Creature> getCreaturesInArea(Shape area) {
        return this.creatures.values().stream()
            .filter(creature -> creature.getHitBox().isOverlapping(area))
            .collect(Collectors.toSet());
    }

    @Override
    public void move(UUID id, Vector2f force) {
        Creature creature = this.creatures.get(id);
        if (creature != null) {
            creature.applyForce(force);
        }
    }

    @Override
    public Set<CreatureObservation> getSurroundingCreatures(UUID id) {
        Set<CreatureObservation> observed = new HashSet<>();
        Creature creature = this.creatures.get(id);
        if (creature == null) {
            return observed;
        }

        Shape visionCone = creature.getVisionCone();
        observed.addAll(this.getCreaturesInArea(visionCone));
        observed.remove(creature);
        return observed;
    }

    @Override
    public Vector2f getLocation(UUID id) {
        Creature creature = this.creatures.get(id);
        if (creature == null) {
            return null;
        }
        return creature.getLocation();
    }

    @Override
    public UUID addCreature(Vector2f location) {
        Creature creature = new Creature(location);
        UUID id = creature.getID();
        this.creatures.put(id, creature);
        this.entities.put(id, creature);
        return id;
    }

    @Override
    public CreatureState getCreatureState(UUID id) {
        Creature creature = this.creatures.get(id);
        if (creature == null) {
            return null;
        }
        return creature;
    }
}
