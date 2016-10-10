package wildflower;

import wildflower.creature.CreatureApi;
import wildflower.api.CreatureModel;
import wildflower.api.ObservationModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;
import java.util.Set;

import org.joml.Vector2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.*;

public class WildflowerServer {
    public static World world = new World();
    public static int tickMillis = 5;

    public static void main(String[] args) {

        // Start up World in its own Executor thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Startng world in " + threadName);
            world.start();
        });

        // Creature Gson to marshal Model objects back and forth between their JSON representations
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
            .registerTypeAdapter(Vector2f.class, new Vector2fDeserializer())
            .create();

        // Setup port, static file location, connect websocket endpoint
        port(9090);
        staticFiles.location("/public");
        webSocket("/wildflower", WildflowerWebSocket.class);

        // request new creature at location
        post("api/v0/creature", (request, response) -> {
            return new CreatureModel(world.addCreature(gson.fromJson(request.body(), Vector2f.class)));
        }, gson::toJson);

        // get information about creature by id
        get("api/v0/creature/:id", (request, response) -> {
            return new CreatureModel(world.getCreature(UUID.fromString(request.params("id"))));
        }, gson::toJson);

        // get observation from creature by id
        get("api/v0/creature/:id/observation", (request, response) -> {
            return new ObservationModel(world.getSurroundingCreatures(UUID.fromString(request.params("id"))));
        }, gson::toJson);

        // move creature by id
        post("api/v0/creature/:id/move", (request, response) -> {
            world.move(UUID.fromString(request.params("id")), gson.fromJson(request.body(), Vector2f.class));
            return "";
        }, gson::toJson);
    }
}
