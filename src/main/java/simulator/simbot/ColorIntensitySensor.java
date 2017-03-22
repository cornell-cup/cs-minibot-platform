package simulator.simbot;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A sensor that measures color intensity based on an image provided to the simulator.
 */
public class ColorIntensitySensor extends Sensor {

    transient BufferedImage img;
    private transient SimBot parent;

    public ColorIntensitySensor(SimBotSensorCenter myCenter, String name, SimBot parent) {
        super(myCenter, name);
        this.parent = parent;
        try {
            img = ImageIO.read(new File("./src/main/resources/public/img/line.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject read() {
        // Get the x,y coordinates of the bot.
        // TODO: Make it so we can do this line, not the one below. VisionCoordinate parentCoordinate = BaseStation.getInstance().getVisionManager().getBotCoordinate(parent);
        List<VisionObject> allLocs = BaseStation.getInstance().getVisionManager().getAllLocationData();
        JsonObject jo = new JsonObject();

        if (allLocs.size() == 0) {
            jo.addProperty("error","no object");
            return jo;
        }

        VisionCoordinate parentCoordinate = allLocs.get(0).coord;

        if (parentCoordinate == null) {
            jo.addProperty("error","null object");
            return jo;
        }

        // TODO: Coordinate transform, for now just read at the middle

        int[] transformed = transformToPixels(parentCoordinate);

        // Get pixel values
        int rgb = img.getRGB(transformed[0], transformed[1]);
        jo.addProperty("data",rgb);

        System.out.printf("%d, %d\n",transformed[0], transformed[1]);

        return jo;
    }

    private int[] transformToPixels(VisionCoordinate vc) {
        double X_SCALE = 10;
        double Y_SCALE = 10;
        int[] ret = new int[2];

        ret[0] = (int) Math.floor(vc.x * X_SCALE);
        ret[1] = (int) Math.floor(vc.y * Y_SCALE);
        return ret;
    }
}
