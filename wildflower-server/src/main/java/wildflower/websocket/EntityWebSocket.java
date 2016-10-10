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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static wildflower.WildflowerServer.clearSession;
import static wildflower.WildflowerServer.gson;
import static wildflower.WildflowerServer.sessionsByEndpoint;
import static wildflower.WildflowerServer.indexSession;
import static wildflower.WildflowerServer.clientsBySession;
import static wildflower.WildflowerServer.webSocketPushDelay;
import static wildflower.WildflowerServer.world;

@WebSocket
public class EntityWebSocket {
    static {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Starting entity socket stream in " + threadName);
            while (true) {
                List<Session> sessions = sessionsByEndpoint.get(EntityWebSocket.class);
                if (sessions != null) {
                    sessions.forEach(session -> {
                        ClientModel client = clientsBySession.get(session);
                        //TODO: filter entities by whether they are contained within this client's viewport

                        if (session.isOpen()) {
                            List<RenderableEntityModel> entitiesToRender = world.getEntities().stream()
                                    .map(RenderableEntityModel::new).collect(Collectors.toList());
                            try {
                                session.getRemote().sendString(gson.toJson(entitiesToRender));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(webSocketPushDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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
