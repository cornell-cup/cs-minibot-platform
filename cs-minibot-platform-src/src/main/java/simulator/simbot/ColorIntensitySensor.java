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
    private double lateralOffset;

    public ColorIntensitySensor(SimBotSensorCenter myCenter, String name, SimBot sim, double lateralOffset) {
        super(myCenter, name);
        parent = sim;
        try {
            img = ImageIO.read(new File
                    ("cs-minibot-platform-src/src/main/resources/public/img" +
                            "/line.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.lateralOffset = lateralOffset;
    }

    public JsonObject read() {
        // Get the x,y coordinates of the bot.
        // TODO: Make it so we can do this line, not the one below. VisionCoordinate parentCoordinate = BaseStation.getInstance().getVisionManager().getBotCoordinate(parent);
        List<VisionObject> allLocs = BaseStation.getInstance().getVisionManager().getAllLocationData();
        JsonObject jo = new JsonObject();

        if (allLocs.size() == 0) {
            jo.addProperty("data", -1);
            return jo;
        }

        VisionCoordinate parentCoordinate = allLocs.get(0).coord;

        if (parentCoordinate == null) {
            jo.addProperty("data", -1);
            return jo;
        }

        int[] transformed = transformToPixels(parentCoordinate, this.lateralOffset);

        // Get pixel values
        if (img == null) {
            System.err.println("null image");
            jo.addProperty("data", -1);
            return jo;
        }
        int rgb = img.getRGB(transformed[0], transformed[1]);
        if (rgb <= -16777216) {
            rgb = 1;
        } else {
            rgb = 0;
        }
        jo.addProperty("data", rgb);

        return jo;
    }

    private int[] transformToPixels(VisionCoordinate vc, double lateralOffset) {
        double X_SCALE = 100;
        double Y_SCALE = 100;
        double medial_offset = 9.5; //offset along the medial axis of the robot, positive values indicate a "forwards" offset. 62.5 corresponds with offsetting it to the front of the bot
        double lateral_offset = lateralOffset; //offset along lateral axis of robot, positive values indicate a "leftwards" offset, 0 indicates that there is no left/right offset
        int[] ret = new int[2];

        double angle = vc.getThetaOrZero();
        double angle_offset = Math.atan(lateral_offset / medial_offset) + angle; //intermediate step for calculating coordinates
        double total_offset = Math.sqrt(medial_offset * medial_offset + lateral_offset * lateral_offset); //intermediate step for calculating coordinates

        ret[0] = (int) Math.floor(vc.x * X_SCALE + total_offset * Math.cos(angle_offset));
        ret[1] = (int) Math.floor(vc.y * Y_SCALE + total_offset * Math.sin(angle_offset));
        return ret;
    }
}
