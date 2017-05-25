package basestation.bot.robot;

import basestation.BaseStation;
import basestation.bot.commands.CommandCenter;
import basestation.bot.connection.Connection;
import basestation.bot.sensors.SensorCenter;

/**
 * Represents any bot. We assume bots may present information and receive information,
 * so this is separated into the commandCenter and sensorCenter. Bots also have a persistent
 * connection which is represented by connection.
 */
public abstract class Bot {

    private Connection connection;
    private String name;

    public Bot(Connection c) {
        this.connection = c;
        this.name = safeEscapeName("Unnamed Bot");
    }

    public Bot(Connection c, String name) {
        this.connection = c;
        this.name = safeEscapeName(name);
    }

    /**
     * TODO: Use reflections on this if necessary.
     *
     * @return A command center for controlling the bot.
     */
    public abstract CommandCenter getCommandCenter();

    /**
     * @return The bot's sensor center.
     */
    public abstract SensorCenter getSensorCenter();

    /**
     * Returns the connection associated with this bot. Should be used sparingly, as the CommandCenter
     * and SensorCentor abstractions are built for convenience over the connection.
     *
     * @return the bot's connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the name associated with the bot, if one was provided.
     *
     * @return The name associated with the bot
     */
    public String getName() {
        return name;
    }

    /**
     * Terminates the connection to the bot as safely as possible
     */
    public void destroy() {
        connection.destroy();
    }

    /**
     * Safely escapes the name of the bot to ensure it is unique and returns that string.
     * This has a simple implementation for now, but could be extended to guarantee uniqueness.
     * For anyone using a bot, this means names should only use [a-zA-Z] characters.
     *
     * @param name the name to be escaped
     * @return The safely escaped name
     */
    private String safeEscapeName(String name) {
        if (BaseStation.getInstance().getBotManager().getBotByName(name).isPresent()) {
            name = name + BaseStation.getInstance().getBotManager().generateBotNumber();
        }

        return name;
    }

    // Default overrides

    @Override
    public String toString() {
        return "[Bot|" + connection + "|" + getName() + "]";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Bot && o.toString().equals(toString()));
    }

}
