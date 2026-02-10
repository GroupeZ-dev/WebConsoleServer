package fr.maxlego08.console;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class MinecraftWebSocketServer extends WebSocketServer {

    private final Logger logger;

    public MinecraftWebSocketServer(int port, Logger logger) {
        super(new InetSocketAddress(port));
        this.logger = logger;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String address = webSocket.getRemoteSocketAddress().toString();
        this.logger.info("WebSocket client connected: " + address);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        String address = webSocket.getRemoteSocketAddress().toString();
        this.logger.info("WebSocket client disconnected: " + address);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        // Handle incoming messages from clients if needed
    }

    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        this.logger.warning("WebSocket error: " + exception.getMessage());
    }

    @Override
    public void onStart() {
        this.logger.info("WebSocket server started on port " + getPort());
    }

    public void broadcastMessage(String message) {
        broadcast(message);
    }
}
