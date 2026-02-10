package fr.maxlego08.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class WebServerPlugin extends JavaPlugin {

    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8765;
    private static final int DEFAULT_MAX_HISTORY = 500;

    private MinecraftWebSocketServer webSocketServer;
    private ConsoleLogAppender consoleLogAppender;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String host = getConfig().getString("websocket-host", DEFAULT_HOST);
        int port = getConfig().getInt("websocket-port", DEFAULT_PORT);
        String password = getConfig().getString("websocket-password", "");
        int maxHistory = getConfig().getInt("max-log-history", DEFAULT_MAX_HISTORY);

        startWebSocketServer(host, port, password, maxHistory);
        registerLogAppender();

        getLogger().info("WebConsoleServer enabled on " + host + ":" + port);
    }

    @Override
    public void onDisable() {
        unregisterLogAppender();
        stopWebSocketServer();

        getLogger().info("WebConsoleServer disabled");
    }

    private void startWebSocketServer(String host, int port, String password, int maxHistory) {
        this.webSocketServer = new MinecraftWebSocketServer(host, port, password, maxHistory, getLogger());
        this.webSocketServer.start();
    }

    private void stopWebSocketServer() {
        if (this.webSocketServer != null) {
            try {
                this.webSocketServer.stop();
            } catch (InterruptedException exception) {
                getLogger().warning("Error stopping WebSocket server: " + exception.getMessage());
            }
        }
    }

    private void registerLogAppender() {
        this.consoleLogAppender = new ConsoleLogAppender(this.webSocketServer);
        this.consoleLogAppender.start();

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(this.consoleLogAppender);
    }

    private void unregisterLogAppender() {
        if (this.consoleLogAppender != null) {
            Logger rootLogger = (Logger) LogManager.getRootLogger();
            rootLogger.removeAppender(this.consoleLogAppender);
            this.consoleLogAppender.stop();
        }
    }
}