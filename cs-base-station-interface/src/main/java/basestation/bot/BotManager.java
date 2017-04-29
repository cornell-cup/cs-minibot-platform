package basestation.bot;

import basestation.bot.connection.UDPConnectionListener;
import basestation.bot.robot.Bot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks and manages all bots. Any bot that you create a connection to should be tracked under
 * this manager to prevent errors. Bots are uniquely identified by their names.
 */
public class BotManager {
    // Provides unique IDs for all bots
    private int botCounter;

    // Mapping from Bot Names to actual bots
    private Map<String, Bot> botMap;

    private UDPConnectionListener udpConnection;

    private HashMap<String, String> botIPMap;

    /**
     * Initializes the bot manager with a fresh map and counter
     */
    public BotManager() {
        botCounter = 0;
        botMap = new ConcurrentHashMap<>();
        udpConnection = new UDPConnectionListener();
        udpConnection.start();
        botIPMap = new HashMap<>();

    }

    /**
     * Begins tracking bot under the BotManager and return its managed name
     *
     * @param bot The bot object that was created for the bot
     * @return The name of the bot
     */
    public String addBot(Bot bot) throws Exception {
        if (bot.getConnection().connectionActive()) {
            botMap.put(bot.getName(), bot);
            botIPMap.put(bot.getName(), bot.getCommandCenter().getConnection().getIP());
            return bot.getName();
        } else {
            throw new Exception("The connection was not active. Not adding the bot.");
        }
    }

    /**
     * Returns the bot associated with name if it exists
     *
     * @param name The name provided for the bot when it was created
     * @return The bot or null if it does not exist
     */
    public Optional<Bot> getBotByName(String name) {
        return Optional.ofNullable(botMap.get(name));
    }

    /**
     * Removes bot with id botId from the botMap and returns it
     *
     * @param botName the managed name of the bot
     * @return An optional of the removed bot
     */
    public Optional<Bot> removeBotByName(String botName) {
        botIPMap.remove(botName);
        return Optional.ofNullable(botMap.remove(botName));
    }

    /**
     * Gets all the bots currently tracked
     *
     * @return A collection of all bots being tracked by the BotManager.
     */
    public Collection<Bot> getAllTrackedBots() {
        return botMap.values();
    }

    /**
     * Returns the next available int for a bot number. This increments the botCounter.
     * Generally should not be used by anyone using the BaseStation.
     *
     * @return the next available int
     */
    public int generateBotNumber() {
        return botCounter++;
    }

    public Set<String> getAllDiscoveredBots() {
        return udpConnection.getAddressSet();
    }

    /**
     * Returns the IP associated with Bot IP Mapping
     *
     * @return String
     */
    public String getBotIP (String name) {
        return botIPMap.get(name);
    }
}
