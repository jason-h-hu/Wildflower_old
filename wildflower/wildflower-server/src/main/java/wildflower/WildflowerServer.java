package wildflower;

import static spark.Spark.*;

public class WildflowerServer {
    public static World world = new World();

    public static void main(String[] args) {
        port(9090);
        webSocket("/wildflower", WildflowerWebSocket.class);
        staticFiles.location("/public");
        get("/ball", (request, response) -> {
            return world.createNewBall().toString();
        });
        post("/ball/:id/move", (request, response) -> {
            System.out.println(request.body());
            return "";
        });
    }
}
