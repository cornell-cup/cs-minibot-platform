package basestation.bot.robot.minibot;

import basestation.BaseStation;
import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;

/**
 * Maintains our interface with a MiniBot
 */
public class MiniBot extends Bot {
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

        TCPListenerThread(TCPConnection t) {
            tcpConnection = t;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (tcpConnection.connectionActive()) {
                        String message = tcpConnection.receive();
                        if (message != null) {
                            parseIncoming(message);
                        }
                    }
                }
            } catch (RuntimeException e) {
                System.err.println("TCP receive failed");
                e.printStackTrace();
            }

        }


        /**
         * Breaks the data into key and value
         * Precondition: data != null
         *
         * @param data must start with "<<<<" and end with ">>>>". key-value
         *             should be separated by ":"
         */
        private void parseIncoming(String data) {
            if (data != null) {
                int start = data.indexOf("<<<<");
                int comma = data.indexOf(",");
                int end = data.indexOf(">>>>");
                if (start != -1 && comma != -1 && end != -1) {
                    String key = data.substring(start + 4, comma);
                    String value = data.substring(comma + 1, end);
                    actOnIncoming(key, value);
                }
            }
        }

        /**
         * Acts based on key and value, bot sending information should send
         * key and value, bot requesting information should only send key
         *
         * @param key   Must be Instruction
         * @param value Should qualify the instruction
         */
        private void actOnIncoming(String key, String value) {
            if (value.length() == 0) {
                // bot requesting information
                String valueToSend = BaseStation.getInstance().getBotManager()
                        .getBotExchange(key);
                tcpConnection.sendKV(key, valueToSend);
            } else {
                // bot sending information
                BaseStation.getInstance().getBotManager().setBotExchangeMap(key,
                        value);
            }
        }
    }
}

