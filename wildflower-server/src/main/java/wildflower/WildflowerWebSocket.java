package wildflower;

import wildflower.api.EntityRenderingModel;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joml.Vector2f;

@WebSocket
public class WildflowerWebSocket {

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        System.out.println("Connected!");

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
            .registerTypeAdapter(Vector2f.class, new Vector2fDeserializer())
            .create();

        while (session.isOpen()) {
            List<EntityRenderingModel> entitiesToRender = WildflowerServer.world.getEntities().stream()
                .map(EntityRenderingModel::new).collect(Collectors.toList());
            session.getRemote().sendString(gson.toJson(entitiesToRender));
            try {
                Thread.sleep(WildflowerServer.webSocketPushDelay);
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
