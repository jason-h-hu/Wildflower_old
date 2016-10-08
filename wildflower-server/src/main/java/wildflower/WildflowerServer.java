package wildflower;

import wildflower.creature.CreatureState;
import wildflower.creature.CreatureObservation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;
import java.util.Set;

import org.joml.Vector2f;

import org.json.JSONObject;
import org.json.JSONArray;

import static spark.Spark.*;

public class WildflowerServer {
    public static World world = new World();
    public static int tickMillis = 5;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Startng world in " + threadName);
            world.start();
        });

        port(9090);
        staticFiles.location("/public");
        webSocket("/wildflower", WildflowerWebSocket.class);

        post("/api/v0/creature", (request, response) -> {
            JSONObject payload = new JSONObject(request.body());
            JSONObject location = payload.getJSONObject("location");
            Vector2f locationVector = new Vector2f((float)location.getDouble("x"), (float)location.getDouble("y"));
            return world.addCreature(locationVector);
        });

        get("/api/v0/creature/:id", (request, response) -> {
            UUID id = UUID.fromString(request.params("id"));
            CreatureState creatureState = world.getCreatureState(id);
            JSONObject creatureLocation = new JSONObject();
            creatureLocation.put("x", creatureState.getLocation().x);
            creatureLocation.put("y", creatureState.getLocation().y);
            JSONObject creatureData = new JSONObject();
            creatureData.put("location", creatureLocation);
            return creatureData.toString();
        });

        get("/api/v0/creature/:id/observation", (request, response) -> {
            UUID id = UUID.fromString(request.params("id"));
            Set<CreatureObservation> creatureObservations = world.getSurroundingCreatures(id);
            JSONArray creatures = new JSONArray();
            creatureObservations.forEach(creatureObservation -> {
                JSONObject location = new JSONObject();
                location.put("x", creatureObservation.getLocation().x);
                location.put("y", creatureObservation.getLocation().y);
                JSONObject observationData = new JSONObject();
                observationData.put("location", location);
                creatures.put(observationData);
            });
            return creatures.toString();
        });

        post("/api/v0/creature/:id/move", (request, response) -> {
            UUID id = UUID.fromString(request.params("id"));
            JSONObject payload = new JSONObject(request.body());
            JSONObject force = payload.getJSONObject("force");
            Vector2f forceVector = new Vector2f((float)force.getDouble("x"), (float)force.getDouble("y"));
            world.move(id, forceVector);
            return "";
        });
    }
}
