package basestation.vision;

import Ice.Current;
import VisionModule.Blob;
import VisionModule._BaseInterfaceDisp;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import org.ejml.simple.*;

/**
 * Adapter for the OverheadVision Ice communication.
 * This serves as a listen server for vision data. Vision connects to this
 * on port 10009, then data is constantly received and can be accessed through the VisionSystem methods.
 */
public class OverheadVisionSystem extends VisionSystem {

    private volatile HashSet<VisionObject> trackedObjectSet;
    private volatile HashMap<Integer, VisionObject> trackedObjectMapKalman;
    private volatile long prevTime;

    public OverheadVisionSystem() {
        super(new VisionCoordinate(0, 0, 0, 0,0));
        VisionListenServerThread vlst = new VisionListenServerThread(this);
        vlst.start();
        trackedObjectSet = new HashSet<VisionObject>();
        trackedObjectMapKalman = new HashMap();
        prevTime = System.currentTimeMillis();
    }

    /**
     * Handles a new input of data
     *
     * @param data The data sent from overhead vision
     */
    private void processBlobs(Blob[] data) {
        HashSet<VisionObject> newSet = new HashSet<>();
        for (Blob b : data) {
            VisionCoordinate vc = new VisionCoordinate(b.x, b.y, b.velocityx, b.velocityy, ((((b.orientation % (2 * Math.PI)) * 180 / (Math.PI))) + 180) % 360);
            VisionObject vo = new VisionObject(this, b.botID, vc);
            newSet.add(vo);

            if (this.trackedObjectMapKalman.containsKey(b.botID)) {
                VisionCoordinate vck = kalmanFilter(vo, this.trackedObjectMapKalman.get(b.botID));
                VisionObject vok = new VisionObject(this, b.botID, vck);
                this.trackedObjectMapKalman.put(b.botID, vok);
            } else {
                this.trackedObjectMapKalman.put(b.botID, vo);
            }
        }
        this.trackedObjectSet = newSet;
    }

    public VisionCoordinate kalmanFilter (VisionObject current_vo, VisionObject prev_vo) {
        long dt = System.currentTimeMillis() - this.prevTime;

        double[][] H_matrix = {{1, 0, dt, 0},{0, 1, 0, dt},{0, 0, 1, 0},{0, 0, 0, 1}};
        double[][] R_matrix = {{0.1, 0, 0, 0},{0, 0.1, 0, 0},{0, 0, 0.1, 0},{0, 0, 0, 0.1}};
        double[][] x_vector = {{prev_vo.coord.getX()},
                               {prev_vo.coord.getY()},
                               {prev_vo.coord.getVelocityX()},
                               {prev_vo.coord.getVelocityY()}};
        double[][] z_vector = {{current_vo.coord.getX()},
                        {current_vo.coord.getY()},
                        {current_vo.coord.getVelocityX()},
                        {current_vo.coord.getVelocityY()}};

        SimpleMatrix H = new SimpleMatrix(H_matrix);
        SimpleMatrix R = new SimpleMatrix(R_matrix);
        SimpleMatrix P = new SimpleMatrix(prev_vo.coord.getP());
        SimpleMatrix x = new SimpleMatrix(x_vector);
        SimpleMatrix z = new SimpleMatrix(z_vector);

        // y = z - H x
        SimpleMatrix y = z.minus(H.mult(x));

        // S = H P H' + R
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);

        // K = PH'S^(-1)
        SimpleMatrix K = P.mult(H.transpose().mult(S.invert()));

        // x = x + Ky
        x = x.plus(K.mult(y));

        // P = (I-kH)P = P - KHP
        P = P.minus(K.mult(H).mult(P));

        VisionCoordinate vc = new VisionCoordinate(x.get(0,0), x.get(0,1), x.get(0,2), x.get(0,3));
        double[][] P_matrix = {{P.get(0,0),P.get(1,0),P.get(2,0),P.get(3,0)},
                               {P.get(0,1),P.get(1,1),P.get(2,1),P.get(3,1)},
                               {P.get(0,2),P.get(1,2),P.get(2,2),P.get(3,2)},
                               {P.get(0,3),P.get(1,3),P.get(2,3),P.get(3,3)}};
        vc.setP(P_matrix);

        this.prevTime = System.currentTimeMillis();
        return vc;
    }

    @Override
    public Set<VisionObject> getAllObjects() {
        return trackedObjectSet;
    }

    private class UpdateHandler extends _BaseInterfaceDisp {

        OverheadVisionSystem parent;

        UpdateHandler(OverheadVisionSystem parent) {
            this.parent = parent;
        }

        @Override
        public double ping(Current __current) {
            System.out.println("I have been pinged!");
            return 0;
        }

        /**
         * Called every time the actual vision system pushes an update to this server
         * @param data All objects currently seen by the system, in Blob form
         * @param __current
         * @return
         */
        @Override
        public int update(Blob[] data, Current __current) {
            parent.processBlobs(data);
            return 0;
        }
    }

    /**
     * The ping server for listening occurs in its own thread to prevent program flow from
     * being blocked.
     */
    private class VisionListenServerThread extends Thread {

        OverheadVisionSystem parent;

        VisionListenServerThread(OverheadVisionSystem parent) {
            this.parent = parent;
        }

        public void run() {
            Ice.Communicator ic = Ice.Util.initialize();
            Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("Ping", "tcp -h * -p 10009");
            adapter.add(new UpdateHandler(parent), ic.stringToIdentity("Ping"));
            adapter.activate();
            System.out.println("waiting for vision connection");
            System.out.println(adapter.getEndpoints().toString());
            ic.waitForShutdown();
            ic.destroy();
        }
    }

}
