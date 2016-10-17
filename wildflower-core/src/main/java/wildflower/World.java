package wildflower;

import wildflower.geometry.AxisAlignedBox;
import wildflower.geometry.Shape;

import java.util.UUID;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector2f;
import wildflower.terrain.TerrainEngine;
import wildflower.terrain.TerrainTile;

public class World {
    private Map<UUID, Entity> entities;
    private Map<UUID, Creature> creatures;
    private TerrainEngine terrainEngine;

    public World() {
        this.entities = new ConcurrentHashMap<>();
        this.creatures = new ConcurrentHashMap<>();
        this.terrainEngine = new TerrainEngine();
    }

    public Creature addCreature(Vector2f location) {
        Creature creature = new Creature(location, System.nanoTime());
        UUID id = creature.getID();
        this.creatures.put(id, creature);
        this.entities.put(id, creature);
        return creature;
    }

    public Set<TerrainTile> getTerrainFor(AxisAlignedBox region) {
        return terrainEngine.getTerrainFor(region);
    }

    public Creature getCreature(UUID id) {
        return this.creatures.get(id);
    }

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

    public void move(UUID id, Vector2f force) {
        Creature creature = this.creatures.get(id);
        if (creature != null) {
            creature.applyForce(force);
        }
    }

    public Collection<Entity> getEntities() {
        return this.entities.values();
    }

    public void update(float delta) {
        this.reapDeadCreatures();
        this.updateEntities();
    }

    private void reapDeadCreatures() {
        this.creatures.values().forEach(creature -> {
            if (creature.isDead()) {
                UUID id = creature.getID();
                this.creatures.remove(id);
                this.entities.remove(id);
            }
        });
    }

    private void updateEntities() {
        this.entities.values().forEach(Entity::update);
    }

    private Set<Creature> getCreaturesInArea(Shape area) {
        return this.creatures.values().stream()
            .filter(creature -> creature.getHitBox().isOverlapping(area))
            .collect(Collectors.toSet());
    }
}
