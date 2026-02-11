package fr.maxlego08.console.velocity;

import fr.maxlego08.console.MinecraftWebSocketServer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class VelocityLogHandler extends Handler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private final MinecraftWebSocketServer webSocketServer;

    public VelocityLogHandler(MinecraftWebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void publish(LogRecord record) {
        if (this.webSocketServer == null) {
            return;
        }

        String time = TIME_FORMATTER.format(Instant.ofEpochMilli(record.getMillis()));
        String level = record.getLevel().getName();
        String loggerName = record.getLoggerName();
        String message = record.getMessage();

        String formattedMessage = String.format("[%s] [%s] [%s]: %s", time, level, loggerName, message);

        this.webSocketServer.broadcastLog(formattedMessage);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
