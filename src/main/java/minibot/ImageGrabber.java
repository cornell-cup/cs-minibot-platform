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
public class ImageGrabber  {

    public static void grabImage() throws IOException {
        // Grab image
        Image image = null;
        URL url = null;
        url = new URL("http://192.168.4.217:8888/out.jpg");
        //URL url = new URL("https://cdn.pixabay.com/photo/2014/03/29/09/17/cat-300572_960_720.jpg");
        InputStream in = null;
        in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        FileOutputStream fos = null;
        fos = new FileOutputStream("./predict/wtf/currentImage.jpg");
        fos.write(response);
        fos.close();
    }
}
