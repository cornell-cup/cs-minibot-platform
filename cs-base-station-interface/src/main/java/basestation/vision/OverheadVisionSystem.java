package basestation.vision;

import Ice.Current;
import VisionModule.Blob;
import VisionModule._BaseInterfaceDisp;

import java.util.HashSet;
import java.util.Set;

/**
 * Adapter for the OverheadVision Ice communication.
 * This serves as a listen server for vision data. Vision connects to this
 * on port 10009, then data is constantly received and can be accessed through the VisionSystem methods.
 */
public class OverheadVisionSystem extends VisionSystem {

    private volatile HashSet<VisionObject> trackedObjectSet;

    public OverheadVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        VisionListenServerThread vlst = new VisionListenServerThread(this);
        vlst.start();
        trackedObjectSet = new HashSet<VisionObject>();
    }

    /**
     * Handles a new input of data
     *
     * @param data The data sent from overhead vision
     */
    private void processBlobs(Blob[] data) {
        HashSet<VisionObject> newSet = new HashSet<>();
        for (Blob b : data) {
            VisionCoordinate vc = new VisionCoordinate(b.x, b.y, Math
                    .toRadians(((((-b
                    .orientation % (2 * Math.PI)) * 180 / (Math.PI))) + 180)
                            % 360));
            VisionObject vo = new VisionObject(this, b.botID, vc);
            newSet.add(vo);
        }

        this.trackedObjectSet = newSet;
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
