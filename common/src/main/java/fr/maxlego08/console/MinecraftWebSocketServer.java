package fr.maxlego08.console;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MinecraftWebSocketServer extends WebSocketServer {

    private final WebConsoleLogger logger;
    private final String password;
    private final int maxLogHistory;
    private final List<String> logHistory = Collections.synchronizedList(new ArrayList<String>());
    private final Set<WebSocket> authenticatedClients = ConcurrentHashMap.newKeySet();

    public MinecraftWebSocketServer(String host, int port, String password, int maxLogHistory, WebConsoleLogger logger) {
        super(new InetSocketAddress(host, port));
        this.logger = logger;
        this.password = password;
        this.maxLogHistory = maxLogHistory;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        if (this.password == null || this.password.isEmpty()) {
            authenticateClient(webSocket);
        } else {
            webSocket.send("{\"type\":\"auth_required\"}");
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        this.authenticatedClients.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        if (!this.authenticatedClients.contains(webSocket)) {
            if (message.startsWith("{\"type\":\"auth\",\"password\":\"")) {
                String providedPassword = message.substring(27, message.length() - 2);
                if (this.password.equals(providedPassword)) {
                    authenticateClient(webSocket);
                } else {
                    webSocket.send("{\"type\":\"auth_failed\"}");
                    webSocket.close(4001, "Invalid password");
                }
            }
        }
    }

    private void authenticateClient(WebSocket webSocket) {
        this.authenticatedClients.add(webSocket);
        webSocket.send("{\"type\":\"auth_success\"}");

        synchronized (this.logHistory) {
            for (String log : this.logHistory) {
                webSocket.send("{\"type\":\"log\",\"message\":\"" + escapeJson(log) + "\"}");
            }
        }
        webSocket.send("{\"type\":\"history_complete\"}");
    }

    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        this.logger.warning("WebSocket error: " + exception.getMessage());
    }

    @Override
    public void onStart() {
        this.logger.info("WebSocket server started on " + getAddress().getHostString() + ":" + getPort());
    }

    public void broadcastLog(String message) {
        synchronized (this.logHistory) {
            this.logHistory.add(message);
            while (this.logHistory.size() > this.maxLogHistory) {
                this.logHistory.remove(0);
            }
        }

        String jsonMessage = "{\"type\":\"log\",\"message\":\"" + escapeJson(message) + "\"}";
        for (WebSocket client : this.authenticatedClients) {
            if (client.isOpen()) {
                client.send(jsonMessage);
            }
        }
    }

    private String escapeJson(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    public boolean hasAuthenticatedClients() {
        return !this.authenticatedClients.isEmpty();
    }
}
