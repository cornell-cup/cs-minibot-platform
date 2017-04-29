package basestation;

import basestation.bot.BotManager;
import basestation.bot.robot.Bot;
import basestation.vision.VisionManager;

import java.util.HashMap;

/**
 * Contains logic to manage and unify input and output between bots and vision sources.
 * This class is a singleton to prevent accidental BaseStation duplication.
 */
public class BaseStation {

    private static BaseStation instance;
    private BotManager bManager;
    private VisionManager vManager;

    /**
     * Part of the singleton pattern, returns the singleton BaseStation
     * @return the singleton BaseStation instance
     */
    public static BaseStation getInstance() {
        if (instance == null) {
            instance = new BaseStation();
        }

        return instance;
    }

    private BaseStation() {
        bManager = new BotManager();
        vManager = new VisionManager();
    }

    /**
     * Returns the bot manager held by BaseStation which can be used to lookup bots
     *
     * @return The bot manager
     */
    public BotManager getBotManager() {
        return bManager;
    }

    /**
     * Returns the vision manager held by BaseStation which can be used to track bots and objects
     *
     * @return The vision manager
     */
    public VisionManager getVisionManager() {
        return vManager;
    }
}
