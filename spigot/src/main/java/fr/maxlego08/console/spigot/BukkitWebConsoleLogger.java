package fr.maxlego08.console.spigot;

import fr.maxlego08.console.WebConsoleLogger;

import java.util.logging.Logger;

public class BukkitWebConsoleLogger implements WebConsoleLogger {

    private final Logger logger;

    public BukkitWebConsoleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warning(message);
    }
}
