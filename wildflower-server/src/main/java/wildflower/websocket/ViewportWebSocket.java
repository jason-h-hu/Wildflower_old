package wildflower.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import wildflower.api.ViewportModel;

import java.io.IOException;

import static wildflower.WildflowerServer.clearSession;
import static wildflower.WildflowerServer.gson;
import static wildflower.WildflowerServer.clientsBySession;
import static wildflower.WildflowerServer.indexSession;

@WebSocket
public class ViewportWebSocket {

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
        if (indexSession(ViewportWebSocket.class, session, message)) return;
        clientsBySession.get(session).viewport = gson.fromJson(message, ViewportModel.class);
    }
}
