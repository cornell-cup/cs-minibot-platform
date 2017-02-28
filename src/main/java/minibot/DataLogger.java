package minibot;

import basestation.BaseStation;
import basestation.bot.connection.IceConnection;
import basestation.bot.robot.modbot.ModBot;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by CornellCup on 2/14/2017.
 */
public class DataLogger extends Thread {

    BaseStation bs;
    boolean logging;
    public final long MILLIS_PER_LOG = 250;
    IceConnection ic;

    public DataLogger(BaseStation bs) {
        this.bs = bs;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
        if (logging)
            ic = ((IceConnection) ((ModBot) bs.getBotManager().getBotById(0)).myConnection);
    }

    @Override
    public void run() {
        int dataCount = 0;
        try {
            //if (!BaseHTTPInterface.LEARNING) return;
            FileWriter pw = new FileWriter("./log/commands.csv", true);
            while (true) {
                try {
                    Thread.sleep(MILLIS_PER_LOG);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!logging) {
                    continue;
                }

                System.out.println("Doing a log " + dataCount);
                // Grab command
                int[] arr = ic.getMotorPowerArray();
                int res = commandToInt(arr);
                pw.append((dataCount-1) + "," + res + "\n");
                pw.flush();

                // Grab image
                Image image = null;
                URL url = null;
                try {
                    url = new URL("http://192.168.4.217:8888/out.jpg");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //URL url = new URL("https://cdn.pixabay.com/photo/2014/03/29/09/17/cat-300572_960_720.jpg");
                InputStream in = null;
                try {
                    in = new BufferedInputStream(url.openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                try {
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] response = out.toByteArray();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream("./log/" + res + "/IMG_" + res + "_" + dataCount++ + ".jpg");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int STOP = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public static final int CLOCKWISE = 3;
    public static final int CCLOCKWISE = 4;

    public int commandToInt(int[] wheelArr) {
        if (wheelArr[0] == 0) {
            return STOP;
        } else if (wheelArr[0] == wheelArr[1] && wheelArr[1] == wheelArr[2]) {
            if (wheelArr[0] > 0) {
                return FORWARD;
            } else {
                return BACKWARD;
            }
        } else if (wheelArr[0] > 0) {
            return CLOCKWISE;
        } else {
            return CCLOCKWISE;
        }
    }
}
