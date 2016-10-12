package wildflower;

import org.eclipse.jetty.websocket.api.Session;
import wildflower.api.ClientModel;
import wildflower.api.CreatureModel;
import wildflower.api.ObservationModel;
import wildflower.gson.UuidDeserializer;
import wildflower.gson.UuidSerializer;
import wildflower.gson.Vector2fSerializer;
import wildflower.gson.Vector2fDeserializer;
import wildflower.websocket.EntityWebSocket;
import wildflower.websocket.TerrainWebSocket;
import wildflower.websocket.ViewportWebSocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;
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
    public static final Map<Class<?>, List<Session>> sessionsByEndpoint;
    public static final Map<Session, ClientModel> clientsBySession;
    public static final Gson gson;

    static {
        world = new World();
        ONE_MILLISECOND = 1000000;
        ONE_SECOND = 1000000000;
        TARGET_FPS = 60;
        OPTIMAL_TIME = ONE_SECOND / TARGET_FPS;
        running = false;
        webSocketPushDelay = 10;
        clientsBySession = new ConcurrentHashMap<>();
        sessionsByEndpoint = new ConcurrentHashMap<>();
        gson = new GsonBuilder()
                .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
                .registerTypeAdapter(Vector2f.class, new Vector2fDeserializer())
                .registerTypeAdapter(UUID.class, new UuidSerializer())
                .registerTypeAdapter(UUID.class, new UuidDeserializer())
                .create();
    }

    public static boolean indexSession(Class<?> endpoint, Session session, String message) {
        sessionsByEndpoint.putIfAbsent(endpoint, new LinkedList<>());
        if (!clientsBySession.containsKey(session)) {
            ClientModel clientModel = gson.fromJson(message, ClientModel.class);
            clientsBySession.put(session, clientModel);
            System.out.printf("Associated %s session from %s with client %s%n",
                    endpoint.getSimpleName(), session.getRemoteAddress().getHostName(), clientModel.id.toString());
            sessionsByEndpoint.get(endpoint).add(session);
            return true;
        }
        return false;
    }

    public static void clearSession(Class<?> endpoint, Session session) {
        clientsBySession.remove(session);
        sessionsByEndpoint.get(endpoint).remove(session);
    }

    private static void runWorld() {
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
    }

    private static void runWebSockets() {
        String threadName = Thread.currentThread().getName();
        System.out.println("Starting websocket streams in " + threadName);

        while (running) {
            if (sessionsByEndpoint.containsKey(EntityWebSocket.class)) {
                sessionsByEndpoint.get(EntityWebSocket.class).forEach(EntityWebSocket::speakTo);
            }

            if (sessionsByEndpoint.containsKey(TerrainWebSocket.class)) {
                sessionsByEndpoint.get(TerrainWebSocket.class).forEach(TerrainWebSocket::speakTo);
            }

            try {
                Thread.sleep(webSocketPushDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        // Setup port, static file location
        port(9090);
        staticFiles.location("/public");

        // Setup web socket endpoints
        webSocket("/entity", EntityWebSocket.class);
        webSocket("/viewport", ViewportWebSocket.class);
        webSocket("/terrain", TerrainWebSocket.class);

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

        // Kick things off!
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(WildflowerServer::runWorld);
        executor.submit(WildflowerServer::runWebSockets);
    }
}
