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
    private boolean running;
    private final int targetFps = 60;
    private final long optimalTime = 1000000000 / targetFps;
    private int frameRate = 0;

    public World() {
        this.entities = new ConcurrentHashMap<>();
        this.creatures = new ConcurrentHashMap<>();
        running = false;
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

    public void start() {
        running = true;

        long lastLoopTime = System.nanoTime();
        int fps = 0;
        long lastFpsTime = 0;

        while(running) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double) optimalTime);

            lastFpsTime += updateLength;
            fps++;

            if (lastFpsTime >= 1000000000) {
                this.frameRate = fps;
                lastFpsTime = 0;
                fps = 0;
            }

            this.update(delta);
            try {
                Thread.sleep((lastLoopTime - System.nanoTime() + optimalTime) / 1000000);
            } catch (Exception e) {}
        }
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
