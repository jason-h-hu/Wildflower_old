package wildflower.websocket;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import wildflower.WildflowerServer;
import wildflower.api.EntityRenderingModel;

import org.eclipse.jetty.websocket.api.Session;
import wildflower.api.ViewportModel;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static wildflower.WildflowerServer.gson;
import static wildflower.WildflowerServer.tryIndexSession;
import static wildflower.WildflowerServer.sessionsToBrowserSessionIds;
import static wildflower.WildflowerServer.browserSessionIdsToViewports;
import static wildflower.WildflowerServer.webSocketPushDelay;
import static wildflower.WildflowerServer.world;

@WebSocket
public class EntityWebSocket {
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Starting entity socket stream in " + threadName);
            while (true) {
                sessionsToBrowserSessionIds.entrySet().forEach(entry -> {
                    Session session = entry.getKey();
                    UUID browserSessionID = entry.getValue();
                    ViewportModel viewport = browserSessionIdsToViewports.get(browserSessionID);
                    if (session.isOpen()) {
                        List<EntityRenderingModel> entitiesToRender = world.getEntities().stream()
                                .map(EntityRenderingModel::new).collect(Collectors.toList());
                        try {
                            session.getRemote().sendString(gson.toJson(entitiesToRender));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                this.getClass().getName(), session.getRemoteAddress().getHostName());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        System.out.printf("%s closing session with %s: (%d) { %s }%n",
                this.getClass().getName(), session.getRemoteAddress().getHostName(), statusCode, reason);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        tryIndexSession(session, message);
    }
}
