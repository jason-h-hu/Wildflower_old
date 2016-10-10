package wildflower;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;
import wildflower.api.CreatureModel;
import wildflower.api.ObservationModel;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;


public class WildflowerServer {
    private static final long ONE_MILLISECOND = 1000000;
    private static final long ONE_SECOND = 1000000000;
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = ONE_SECOND / TARGET_FPS;

    static World world = new World();
    static int webSocketPushDelay = 5;

    private static int framesPerSecond = 0;
    private static boolean running = false;

    public static void main(String[] args) {

        // Start up World in its own Executor thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Startng world in " + threadName);
            running = true;

            double delta = 0;
            long lastLoopTime = System.nanoTime();
            long lastFpsTime = 0;
            long now = lastLoopTime;
            long updateLength = 0;
            int fps = 0;

            while (running) {
                now = System.nanoTime();
                updateLength = now - lastLoopTime;
                lastLoopTime = now;
                delta = updateLength / ((double) OPTIMAL_TIME);
                lastFpsTime += updateLength;
                fps++;

                if (lastFpsTime >= ONE_SECOND) {
                    framesPerSecond = fps;
                    lastFpsTime = 0;
                    fps = 0;
                }

                world.update(delta);

                try {
                    Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / ONE_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

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
