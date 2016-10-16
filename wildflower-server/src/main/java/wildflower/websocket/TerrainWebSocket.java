package wildflower.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import wildflower.api.ClientModel;
import wildflower.api.TerrainTileModel;
import wildflower.geometry.AxisAlignedBox;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static wildflower.WildflowerServer.clearSession;
import static wildflower.WildflowerServer.clientsBySession;
import static wildflower.WildflowerServer.gson;
import static wildflower.WildflowerServer.indexSession;
import static wildflower.WildflowerServer.world;

@WebSocket
public class TerrainWebSocket {
    public static void speakTo(Session session) {
        ClientModel client = clientsBySession.get(session);
        if (session.isOpen() && client.state.needsNewTerrain) {
            AxisAlignedBox region = new AxisAlignedBox(client.viewport.upperLeft, client.viewport.lowerRight);
            Set<TerrainTileModel> terrainTileModels = world.getTerrainFor(region).stream()
                    .map(TerrainTileModel::new).collect(Collectors.toSet());
            try {
                session.getRemote().sendString(WebSocketConstants.START_STREAM);
                for (TerrainTileModel terrainTileModel : terrainTileModels) {
                    session.getRemote().sendString(gson.toJson(terrainTileModel));
                }
                session.getRemote().sendString(WebSocketConstants.END_STREAM);
            } catch (IOException e) {
                e.printStackTrace();
            }

            client.state.needsNewTerrain = false;
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
        clearSession(TerrainWebSocket.class, session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        indexSession(TerrainWebSocket.class, session, message);
    }

}
