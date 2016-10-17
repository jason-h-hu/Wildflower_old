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
    public static final Gson gson;

    public static final Map<Class<?>, List<Session>> sessionsByEndpoint;
    public static final Map<Session, ClientModel> clientsBySession;
    public static final Map<UUID, ClientModel> clientsById;

    private static boolean running = false;

    static {
        world = new World();
        clientsBySession = new ConcurrentHashMap<>();
        clientsById = new ConcurrentHashMap<>();
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
            ClientModel tempClientModel = gson.fromJson(message, ClientModel.class);

            // Only index a new client model if we have not seen this ID before
            // No two client models should be in clientsBySession with the same ID
            clientsById.putIfAbsent(tempClientModel.id, tempClientModel);
            clientsBySession.put(session, clientsById.get(tempClientModel.id));

            System.out.printf("Associated %s session from %s with client %s%n",
                    endpoint.getSimpleName(), session.getRemoteAddress().getHostName(), tempClientModel.id.toString());
            sessionsByEndpoint.get(endpoint).add(session);
            return true;
        }
        return false;
    }

    public static void clearSession(Class<?> endpoint, Session session) {
        clientsBySession.remove(session);
        sessionsByEndpoint.get(endpoint).remove(session);
    }

    private static void tickWebSockets(float delta) {
        if (sessionsByEndpoint.containsKey(EntityWebSocket.class)) {
            sessionsByEndpoint.get(EntityWebSocket.class).forEach(EntityWebSocket::speakTo);
        }

        if (sessionsByEndpoint.containsKey(TerrainWebSocket.class)) {
            sessionsByEndpoint.get(TerrainWebSocket.class).forEach(TerrainWebSocket::speakTo);
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
        running = true;
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> new Ticker("World", 60, world::update, () -> running).run());
        executor.submit(() -> new Ticker("WebSockets", 60, WildflowerServer::tickWebSockets, () -> running).run());
    }
}
