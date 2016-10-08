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
        while (session.isOpen()) {
            StringBuilder jsonPre = new StringBuilder();
            jsonPre.append("[");
            WildflowerServer.world.getEntities().forEach(entity -> {
                jsonPre.append(String.format("{\"x\":%f,\"y\":%f},",
                    entity.getLocation().x,
                    entity.getLocation().y));
            });
            String json = jsonPre.toString();
            if (json.endsWith(",")) {
                json = json.substring(0, json.length() - 1);
            }
            json += "]";
            session.getRemote().sendString(json);
            try {
                Thread.sleep(WildflowerServer.tickMillis);
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
    }
}
