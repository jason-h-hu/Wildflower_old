package wildflower;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class World {

    private Map<UUID, Ball> balls = new HashMap<>();

    public UUID createNewBall() {
        UUID uuid = UUID.randomUUID();
        Ball ball = new Ball(0, 0);
        balls.put(uuid, ball);
        return uuid;
    }

    public void moveBall(UUID uuid, float newPx, float newPy) {
        balls.get(uuid).setPosition(newPx, newPy);
    }

}
