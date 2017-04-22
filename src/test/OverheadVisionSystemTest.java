import VisionModule.Blob;
import basestation.vision.OverheadVisionSystem;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by jimmychen on 4/21/17.
 */
public class OverheadVisionSystemTest {


    @BeforeClass
    public static void setUpClass() {
    }


    @Test
    public void test_update(Blob[] data){
        OverheadVisionSystem ovs = new OverheadVisionSystem();
        Blob test_1 = new Blob(5.0, 10.0, 1, null, null, 90.0, 2.0, 4.0, 0.0, 1, 0.0);
        Blob test_2 = new Blob(5.0, 10.0, 1, null, null, 90.0, 2.0, 4.0, 0.0, 1, 0.0);
        Blob[] data = new Blob[2];
        data[0] = test_1;
        data[1] = test_2;
        ovs


    }
}
