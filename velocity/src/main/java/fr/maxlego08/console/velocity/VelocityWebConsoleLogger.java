package fr.maxlego08.console.velocity;

import fr.maxlego08.console.WebConsoleLogger;
import org.slf4j.Logger;

public class VelocityWebConsoleLogger implements WebConsoleLogger {

    private final Logger logger;

    public VelocityWebConsoleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warn(message);
    }
}
