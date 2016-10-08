package wildflower;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class WildflowerWebSocket {

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        System.out.println("Connected!");
        while (true) {
            String balls = "[";
            for (Ball ball : WildflowerServer.world.getBalls()) {
                balls += String.format("{x:%f,y:%f},", ball.getPositionX(), ball.getPositionY());
            }
            if (balls.endsWith(",")) balls = balls.substring(0, balls.length() - 1);
            balls += "]";
            System.out.println(balls);
            session.getRemote().sendString(balls);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        System.out.println("Closed!");
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);
        session.getRemote().sendString(message);
    }
}
