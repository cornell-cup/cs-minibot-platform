import basestation.vision.VisionCoordinate;
import org.junit.Test;
import examples.gobot.*;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AITest {

    @Test
    public void basicEquationTests() {
        /**
         * Tests basic operations to check that equations are generated correctly and that
         * intersection points are correct.
         */
        AIUtil ai = new AIUtil(5, 0, Math.PI);
        VisionCoordinate whereBot = new VisionCoordinate(2.5,0.5,Math.PI/2);
        Equation verticalBot = new Equation(whereBot,Math.PI/2);
        Equation topLine = new Equation(new VisionCoordinate(0,3), new VisionCoordinate(3, 3));
        Point point = ai.intersection(verticalBot,topLine);
        assertTrue(point.xcor == 2.5);
        assertTrue(point.ycor == 3.0);

        whereBot = new VisionCoordinate(2.5,0.5,Math.PI/2 + 0.05);
        verticalBot = new Equation(whereBot,Math.PI/2 + 0.05);
        point = ai.intersection(verticalBot,topLine);
        assertTrue(point.xcor > 2.3);
        assertTrue(point.xcor < 2.4);
        assertTrue(point.ycor == 3.0);


    }
}
