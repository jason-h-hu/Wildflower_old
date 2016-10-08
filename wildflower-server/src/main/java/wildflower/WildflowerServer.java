package wildflower;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import org.joml.Vector2f;

import org.json.JSONObject;

import static spark.Spark.*;

public class WildflowerServer {
    public static World world = new World();
    public static int tickMillis = 50;

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

        post("/creature", (request, response) -> {
            JSONObject payload = new JSONObject(request.body());
            JSONObject location = payload.getJSONObject("location");
            Vector2f locationVector = new Vector2f((float)location.getDouble("x"), (float)location.getDouble("y"));
            return world.addCreature(locationVector);
        });

        post("/creature/:id/move", (request, response) -> {
            UUID id = UUID.fromString(request.params("id"));
            JSONObject payload = new JSONObject(request.body());
            JSONObject force = payload.getJSONObject("force");
            Vector2f forceVector = new Vector2f((float)force.getDouble("x"), (float)force.getDouble("y"));
            world.move(id, forceVector);
            return "";
        });
    }
}
