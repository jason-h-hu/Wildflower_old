package wildflower;

import org.eclipse.jetty.websocket.api.Session;
import wildflower.api.CreatureModel;
import wildflower.api.ObservationModel;
import wildflower.api.ViewportModel;
import wildflower.gson.Vector2fSerializer;
import wildflower.gson.Vector2fDeserializer;
import wildflower.websocket.EntityWebSocket;
import wildflower.websocket.ViewportWebSocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;


public class WildflowerServer {
    public static final World world;
    private static final long ONE_MILLISECOND;
    private static final long ONE_SECOND;
    private static final int TARGET_FPS;
    private static final long OPTIMAL_TIME;
    private static boolean running;
    public static final int webSocketPushDelay;
    public static final Map<Session, UUID> sessionsToBrowserSessionIds;
    public static final Map<UUID, ViewportModel> browserSessionIdsToViewports;
    public static final Gson gson;

    static {
        world = new World();
        ONE_MILLISECOND = 1000000;
        ONE_SECOND = 1000000000;
        TARGET_FPS = 60;
        OPTIMAL_TIME = ONE_SECOND / TARGET_FPS;
        running = false;
        webSocketPushDelay = 10;
        sessionsToBrowserSessionIds = new ConcurrentHashMap<>();
        browserSessionIdsToViewports = new ConcurrentHashMap<>();
        gson = new GsonBuilder()
                .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
                .registerTypeAdapter(Vector2f.class, new Vector2fDeserializer())
                .create();
    }

    public static boolean tryIndexSession(String whichSocket, Session session, String message) {
        if (!sessionsToBrowserSessionIds.containsKey(session)) {
            UUID id = UUID.fromString(message);
            sessionsToBrowserSessionIds.put(session, id);
            System.out.printf("Associated %s session from %s with id %s%n",
                    whichSocket, session.getRemoteAddress().getHostName(), id.toString());
            return true;
        }
        return false;
    }

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
            long framesPerSecond = 0;
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

        // Setup port, static file location, connect websocket endpoint
        port(9090);
        staticFiles.location("/public");

        // Setup web socket endpoints
        webSocket("/entity", EntityWebSocket.class);
        webSocket("/viewport", ViewportWebSocket.class);

        // (PUBLIC) request new creature at location
        post("api/v0/creature", (request, response) -> {
            return new CreatureModel(world.addCreature(gson.fromJson(request.body(), Vector2f.class)));
        }, gson::toJson);

        // (PUBLIC) get information about creature by id
        get("api/v0/creature/:id", (request, response) -> {
            return new CreatureModel(world.getCreature(UUID.fromString(request.params("id"))));
        }, gson::toJson);

        // (PUBLIC) get observation from creature by id
        get("api/v0/creature/:id/observation", (request, response) -> {
            return new ObservationModel(world.getSurroundingCreatures(UUID.fromString(request.params("id"))));
        }, gson::toJson);

        // (PUBLIC) move creature by id
        post("api/v0/creature/:id/move", (request, response) -> {
            world.move(UUID.fromString(request.params("id")), gson.fromJson(request.body(), Vector2f.class));
            return "";
        }, gson::toJson);
    }
}
