package fr.maxlego08.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class WebServerPlugin extends JavaPlugin {

    private static final int DEFAULT_PORT = 8765;

    private MinecraftWebSocketServer webSocketServer;
    private ConsoleLogAppender consoleLogAppender;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        int port = getConfig().getInt("websocket-port", DEFAULT_PORT);

        startWebSocketServer(port);
        registerLogAppender();

        getLogger().info("WebConsoleServer enabled on port " + port);
    }

    @Override
    public void onDisable() {
        unregisterLogAppender();
        stopWebSocketServer();

        getLogger().info("WebConsoleServer disabled");
    }

    private void startWebSocketServer(int port) {
        this.webSocketServer = new MinecraftWebSocketServer(port, getLogger());
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