package basestation.bot.commands;

import basestation.bot.connection.TCPConnection;
import basestation.bot.connection.Connection;

/**
 * Class who's methods are all the commands that can be issued to a bot
 * <p>
 * Each bot must implement this class with their own commands.
 * <p>
 * If there are any important commands we believe all robots should have then place them here
 */
public interface CommandCenter {

    /**
     * toggle logging of data
     */
    void toggleLogging();

    /**
     * Returns whether or not data is currently being logged.
     * @return True if the command center is currently logging data
     */
    boolean isLogging();

    /**
     * Sends an arbitrary key and value over the associated bot's connection. Should only be used for quick prototyping,
     * with the goal of creating a method wrapper around sending the KV.
     * @param key A key to identify the type of command
     * @param value The value of the command
     * @return True if the command seems to have sent correctly
     */
    boolean sendKV(String key, String value);

    /**
     * Returns Connection object
     * @return Connection
     */
    Connection getConnection();
}