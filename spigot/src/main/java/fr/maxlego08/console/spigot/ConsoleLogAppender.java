package fr.maxlego08.console.spigot;

import fr.maxlego08.console.MinecraftWebSocketServer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogAppender extends AbstractAppender {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");

    private final MinecraftWebSocketServer webSocketServer;

    public ConsoleLogAppender(MinecraftWebSocketServer webSocketServer) {
        super("WebConsoleAppender", null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void append(LogEvent event) {
        if (this.webSocketServer == null) {
            return;
        }

        String time = TIME_FORMATTER.format(new Date(event.getTimeMillis()));
        String level = event.getLevel().name();
        String loggerName = event.getLoggerName();
        String message = event.getMessage().getFormattedMessage();

        String formattedMessage = String.format("[%s] [%s] [%s]: %s", time, level, loggerName, message);

        this.webSocketServer.broadcastLog(formattedMessage);
    }
}
