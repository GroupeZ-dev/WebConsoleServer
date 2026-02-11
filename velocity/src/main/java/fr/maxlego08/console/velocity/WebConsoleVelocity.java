package fr.maxlego08.console.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.maxlego08.console.MinecraftWebSocketServer;
import fr.maxlego08.console.WebConsoleLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Plugin(
        id = "webconsoleserver",
        name = "WebConsoleServer",
        version = "1.0",
        authors = {"Maxlego08"},
        description = "Console for your server"
)
public class WebConsoleVelocity {

    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8765;
    private static final int DEFAULT_MAX_HISTORY = 500;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private MinecraftWebSocketServer webSocketServer;
    private VelocityLogHandler logHandler;

    @Inject
    public WebConsoleVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        saveDefaultConfig();
        Properties config = loadConfig();

        String host = config.getProperty("websocket-host", DEFAULT_HOST);
        int port = Integer.parseInt(config.getProperty("websocket-port", String.valueOf(DEFAULT_PORT)));
        String password = config.getProperty("websocket-password", "");
        int maxHistory = Integer.parseInt(config.getProperty("max-log-history", String.valueOf(DEFAULT_MAX_HISTORY)));

        startWebSocketServer(host, port, password, maxHistory);
        registerLogHandler();

        this.logger.info("WebConsoleServer enabled on {}:{}", host, port);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        unregisterLogHandler();
        stopWebSocketServer();

        this.logger.info("WebConsoleServer disabled");
    }

    private void startWebSocketServer(String host, int port, String password, int maxHistory) {
        WebConsoleLogger webLogger = new VelocityWebConsoleLogger(this.logger);
        this.webSocketServer = new MinecraftWebSocketServer(host, port, password, maxHistory, webLogger);
        this.webSocketServer.start();
    }

    private void stopWebSocketServer() {
        if (this.webSocketServer != null) {
            try {
                this.webSocketServer.stop();
            } catch (InterruptedException exception) {
                this.logger.warn("Error stopping WebSocket server: {}", exception.getMessage());
            }
        }
    }

    private void registerLogHandler() {
        this.logHandler = new VelocityLogHandler(this.webSocketServer);
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.addHandler(this.logHandler);
    }

    private void unregisterLogHandler() {
        if (this.logHandler != null) {
            java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
            rootLogger.removeHandler(this.logHandler);
        }
    }

    private void saveDefaultConfig() {
        try {
            if (!Files.exists(this.dataDirectory)) {
                Files.createDirectories(this.dataDirectory);
            }

            Path configPath = this.dataDirectory.resolve("config.properties");
            if (!Files.exists(configPath)) {
                try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    } else {
                        String defaultConfig = """
                                # WebConsoleServer Configuration

                                # IP address to bind the WebSocket server (use 0.0.0.0 for all interfaces)
                                websocket-host=0.0.0.0

                                # Port for the WebSocket server
                                websocket-port=8765

                                # Password for WebSocket authentication (leave empty for no authentication)
                                websocket-password=changeme

                                # Maximum number of log lines to keep in history
                                max-log-history=500
                                """;
                        Files.writeString(configPath, defaultConfig);
                    }
                }
            }
        } catch (IOException exception) {
            this.logger.warn("Failed to save default config: {}", exception.getMessage());
        }
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        Path configPath = this.dataDirectory.resolve("config.properties");

        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                properties.load(in);
            } catch (IOException exception) {
                this.logger.warn("Failed to load config: {}", exception.getMessage());
            }
        }

        return properties;
    }
}
