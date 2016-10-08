package wildflower;

import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class World {

    private Map<UUID, Ball> balls = new ConcurrentHashMap<>();

    public UUID createNewBall() {
        UUID uuid = UUID.randomUUID();
        Ball ball = new Ball(10000, 10000);
        balls.put(uuid, ball);
        return uuid;
    }

    public void moveBall(UUID uuid, float newPx, float newPy) {
        balls.get(uuid).setPosition(newPx, newPy);
    }

    public Collection<Ball> getBalls() {
        return balls.values();
    }

    public void run() {
        while (true) {
            balls.forEach((uuid, ball) -> {
                ball.update();
            });
        }
    }

}
