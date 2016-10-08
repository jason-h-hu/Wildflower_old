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
            String toSend = "" + System.nanoTime();
            System.out.println("Sending " + toSend);
            session.getRemote().sendString("" + System.nanoTime());
            try {
                Thread.sleep(1000);
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
