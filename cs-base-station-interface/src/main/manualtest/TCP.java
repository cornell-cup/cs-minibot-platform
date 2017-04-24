import basestation.bot.connection.TCPConnection;
import org.junit.Assert;
import org.junit.Test;

public class TCPTest {

    /**
     * Requires: a server is running at the ip and port specified
     */
//    @Test
    // Requires python server on. Currently not automated.
    public void testConnect() {
        String ip = "192.168.4.84";
        int port = 10000;
        TCPConnection tcon = new TCPConnection(ip,port);
        Assert.assertTrue(tcon.sendKV("IMPORTANT", "HI"));
    }
}
