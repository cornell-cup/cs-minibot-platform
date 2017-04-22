package basestation.vision;

import java.io.*;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * VisionSystem to take in data from the Tango.  Unfinished.
 * TODO (Fall 2016): Finish this interface
 */
public class TangoVisionSystem extends VisionSystem {

    private VisionObject myObj;
    private TangoReader myReader;

    public TangoVisionSystem() {
        super(new VisionCoordinate(0,0)); // TODO: Calibration
        myReader = new TangoReader();
        myReader.start();

    }
    @Override
    public Set<VisionObject> getAllObjects() {
        HashSet<VisionObject> hset = new HashSet<>();
        hset.add(myObj);
        return hset;
    }

    private class TangoReader extends Thread {

        Socket client;
        Socket dumb;

        @Override
        public void run() {
            System.out.println("Connecting...");
            while (true) {
                try {
                    client = new Socket("192.168.1.108", 2055);
                    dumb = new Socket("192.168.1.108", 2056);
                    System.out.println("Connected");
                } catch (IOException e) {
                }

                if (dumb != null && client.isConnected()) {
                    try {
                        InputStream is = client.getInputStream();
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        out.writeBytes("start 0.001\n");
                        (new DataOutputStream(dumb.getOutputStream())).writeBytes("read 0.001\n");
                        out.flush();


                        while (true) {
                            if (!client.isConnected()) break;
                            double x = 0;
                            double y = 0;
                            double z = 0;
                            double q0 = 0;
                            double q1 = 0;
                            double q2 = 0;
                            double q3 = 0;
                            int status = 0;
                            double timeStamp = 0;
                            double theta = 0;

                            float[] header = new float[9];
                            System.out.println("asdf");
                            for (int i = 0; i < 9; i++) {
                                header[i] = is.read();
                                System.out.println(header[i]);
                            }

                            x = header[0];
                            y = header[1];
                            z = header[2];
                            q0 = header[3];
                            q1 = header[4];
                            q2 = header[5];
                            q3 = header[6];

                            status = (int) header[7];
                            timeStamp = (double) header[8];

                            //theta = Math.Atan2(2 * (Tango.q0 * Tango.q3 + Tango.q1 * Tango.q2), (1 - 2 * (Math.Pow(Tango.q2, 2) + Math.Pow(Tango.q3, 2))));
                            theta = Math.atan2(2 * (q0 * q1 + q2 * q3), (1 - 2 * (Math.pow(q1, 2) + Math.pow(q2, 2))));
                            VisionCoordinate vc = new VisionCoordinate(x, y, theta);
                            //System.out.println(vc);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        client = null;
                    }
                }
            }
        }
    }
}
