package wildflower;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.*;

public class WildflowerServer {
    public static World world = new World();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Startng world in " + threadName);
            world.run();
        });

        port(9090);
        webSocket("/wildflower", WildflowerWebSocket.class);
        staticFiles.location("/public");
        post("/ball", (request, response) -> {
            return world.createNewBall().toString();
        });
        post("/ball/:id/move", (request, response) -> {
            System.out.println(request.body());
            return "";
        });
    }
}
