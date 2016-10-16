package wildflower.websocket;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import wildflower.api.ClientModel;
import wildflower.api.RenderableEntityModel;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static wildflower.WildflowerServer.clearSession;
import static wildflower.WildflowerServer.gson;
import static wildflower.WildflowerServer.indexSession;
import static wildflower.WildflowerServer.clientsBySession;
import static wildflower.WildflowerServer.world;

@WebSocket
public class EntityWebSocket {
    public static void speakTo(Session session) {
        ClientModel client = clientsBySession.get(session);
        //TODO: filter entities by whether they are contained within this client's viewport

        if (session.isOpen()) {
            List<RenderableEntityModel> entitiesToRender = world.getEntities().stream()
                    .map(RenderableEntityModel::new).collect(Collectors.toList());
            try {
                session.getRemote().sendString("");
                for (RenderableEntityModel entityModel : entitiesToRender) {
                    session.getRemote().sendString(gson.toJson(entityModel));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        System.out.printf("%s received connection from %s%n",
                this.getClass().getSimpleName(), session.getRemoteAddress().getHostName());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        System.out.printf("%s closing session with %s: (%d) { %s }%n",
                this.getClass().getSimpleName(), session.getRemoteAddress().getHostName(), statusCode, reason);
        clearSession(EntityWebSocket.class, session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        indexSession(EntityWebSocket.class, session, message);
    }
}
