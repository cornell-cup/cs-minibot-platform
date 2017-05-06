package basestation.bot.robot.minibot;

import basestation.BaseStation;
import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;

/**
 * Maintains our interface with a MiniBot
 */
public class MiniBot extends Bot{
    private final transient MiniBotCommandCenter commandCenter;
    private final transient MiniBotSensorCenter sensorCenter;


    /**
     * Currently minibots are implemented using a TCP connection
     *
     * @param c a TCP connection that has already been created
     */
    public MiniBot(TCPConnection c) {
        super(c);
        this.commandCenter = new MiniBotCommandCenter(c, this);
        this.sensorCenter = new MiniBotSensorCenter();
        // run TCPListenerThread
        (new TCPListenerThread(c)).start();
    }

    public MiniBot(TCPConnection c, String name) {
        super(c, name);
        this.commandCenter = new MiniBotCommandCenter(c, this);
        this.sensorCenter = new MiniBotSensorCenter();
        // run TCPListenerThread
        (new TCPListenerThread(c)).start();
    }

    @Override
    public MiniBotCommandCenter getCommandCenter() {
        return commandCenter;
    }

    @Override
    public SensorCenter getSensorCenter() {
        return sensorCenter;
    }

    private class TCPListenerThread extends Thread {

        private TCPConnection tcpConnection;

        public TCPListenerThread(TCPConnection t) {
            tcpConnection = t;
        }

        @Override
        public void run() {
            while (true) {
                if (tcpConnection.connectionActive()) {
                    String message = tcpConnection.receive();
                    if (message != null) {
                        parseIncoming(message);
                    }
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Breaks the data into key and value
         * @param data must start with "<<<<" and end with ">>>>". key-value
         *             should be separated by ":"
         */
        private void parseIncoming(String data) {
            int start = data.indexOf("<<<<");
            int comma = data.indexOf(",");
            int end = data.indexOf(">>>>");
            if (start != -1 && comma != -1 && end != -1) {
                String key = data.substring(start + 4, comma);
                String value = data.substring(comma + 1, end);
                actOnIncoming(key, value);
            }
        }

        /**
         * Acts based on key and value
         * @param key Must be Instruction
         * @param value Should qualify the instruction
         */
        private void actOnIncoming(String key, String value) {
            if (value.length() == 0) {
                // bot requesting information
                String valueToSend = BaseStation.getInstance().getBotManager()
                        .getBotExchange(key);
                System.out.println("valueToSend: " + valueToSend);
                tcpConnection.sendKV(key, valueToSend);
            } else {
                // bot sending information
                System.out.println("key: " + key + ", value: " + value);
                BaseStation.getInstance().getBotManager().setBotExchangeMap(key,
                        value);
            }

            // pull request changes end---
            /*BaseStation.getInstance().getBotManager();
            if (key.equals("PUT_IP")) {
                int comma = value.indexOf(",");
                String HashKey = value.substring(0, comma);
                String HashValue = value.substring(comma + 1);
                BaseStation.getInstance().getBotManager().setBotIPMap
                        (HashKey, HashValue);

                // pull request changes below
                // trevor says the bot need not send in its information, just
                // use the info present on basestation
                BaseStation.getInstance().getBotManager().setBotIPMap(getName
                        (), getConnection().getIP());
            }
            if (key.equals("GET_IP") && value
                    .toLowerCase().equals("swarm")) {
                // a Minion in the swarm requested the ip, forward it
                // 1. find the ip of the master
                String IP = BaseStation.getInstance().getBotManager()
                        .getBotIP("SwarmMaster");

                if (IP != null)
                    // found IP of the master
                    tcpConnection.sendKV(key + "SwarmMaster", IP);
            }*/
        }
    }
}

