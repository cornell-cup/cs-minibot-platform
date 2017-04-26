package basestation.bot.connection;

/**
 * Manages a connection between the base station and a bot.
 */
public abstract class Connection {
    /**
     * @return True if the connection is still active. Otherwise returns false.
     */
    public abstract boolean connectionActive();

    /**
     * Safely closes the connection.
     * Invariant: After calling destroy, connectionActive() must return false.
     */
    public abstract void destroy();
}
