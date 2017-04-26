import basestation.BaseStation;
import basestation.bot.connection.UDPConnectionListener;
import org.junit.Test;

import java.util.Set;

public class UDPTest {

    /**
     * Requires: a server is running at the ip and port specified
     */
//    @Test
    // Not automated. Manual.
    public void testConnect() {

        UDPConnectionListener udpConnection = new UDPConnectionListener();
        udpConnection.start();
        try {
            while(true){
                Thread.sleep(1000);
                Set<String> test = udpConnection.getAddressSet();
                System.out.println(test.size());
            }
        }
        catch(InterruptedException e){

        }

    }
}
