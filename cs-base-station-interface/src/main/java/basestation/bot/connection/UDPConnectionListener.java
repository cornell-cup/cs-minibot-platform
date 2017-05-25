package basestation.bot.connection;

import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * UDPListener for receiving the IP addresses of the minibots currently active
 * TODO: This is currently not working.
 */
public class UDPConnectionListener extends Thread {

    /**
     * The amount of seconds needed to pass before evicting an address
     */
    public static final int UPDATE_THRESHOLD = 40;
    public static final int PORT_NUMBER = 5001;

    /**
     * Contains the current list of IP addresses and when they last contacted the server
     */
    private Hashtable<String, Timestamp> ipList = new Hashtable<>();

    /**
     * Main method for unit testing
     *
     * @param args
     */
    public static void main(String[] args) {
        UDPConnectionListener udpConnectionListener = new UDPConnectionListener();
        udpConnectionListener.start();
        while (true) ;
    }

    /**
     * Returns the current time as a Timestamp object
     *
     * @return the current time as a Timestamp object
     */
    private static Timestamp getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        //Gets the current date from calendar and the time from the current date
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Initializes a thread running an instance of UDPConnectionListener
     * <p>
     * Taken as a reference from:
     * http://stackoverflow.com/questions/5472269/java-datagramsocket-listening-on-a-broadcast-address
     */
    @Override
    public void run() {
        try {
            //Receives a packet from any IP address
            DatagramSocket socket = new DatagramSocket(PORT_NUMBER, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (true) {
                //Start accepting packets
                socket.receive(packet);

                //Get packet header (IP address) as a string for storage
                String address = packet.getAddress().toString();

                //If address is already present, just replaces its current value with this one
                ipList.put(address, getCurrentTime());
            }
        } catch (SocketException e) {
            System.out.println("Socket not available");
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
        } catch (IOException e) {
            System.out.println("I/O Exception");
        }
    }

    /**
     * Returns the set of IP addresses currently being tracked
     * by the UDP server
     *
     * @returns a set of IP addresses currently being tracked
     */
    public Set<String> getAddressSet() {
        //Check for any inactive addresses and removes them before returning the set of addresses in ipList
        cleanOut();

        return ipList.keySet();
    }

    /**
     * Removes IP addresses from ipList that have not be used in a certain
     * time frame, designated by UPDATE_THRESHOLD
     */
    private void cleanOut() {
        //Get the collection of IP addresses
        Enumeration<String> address_keys = ipList.keys();
        //Get the current time in milliseconds
        long now = getCurrentTime().getTime();
        //Get update threshold
        long threshold = TimeUnit.SECONDS.toMillis(UPDATE_THRESHOLD);

        //Check each address if it should be evicted
        while (address_keys.hasMoreElements()) {
            //Get the logged time of the address in milliseconds
            String address = address_keys.nextElement();
            long then = ipList.get(address).getTime();

            //If the time difference exceeds the threshold, then evict
            if (now - then > threshold) {
                ipList.remove(address);
            }
        }

    }
}
