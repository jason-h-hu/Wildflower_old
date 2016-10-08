package wildflower;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joml.Vector2f;

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
        webSocket("/wildflower", WildflowerWebSocket.class);

        post("/creature", (request, response) -> {
            Vector2f location = new Vector2f(
                Float.parseFloat(request.queryParams("x")),
                Float.parseFloat(request.queryParams("y")));
            return world.addCreature(location);
        });

        staticFiles.location("/public");
    }
}
