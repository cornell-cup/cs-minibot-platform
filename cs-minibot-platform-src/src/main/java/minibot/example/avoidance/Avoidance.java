package minibot.example.avoidance;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import simulator.Simulator;


public class Avoidance extends Thread {
    @Override
    public void run() {
        while(true) {
            BaseStation.getInstance().getVisionManager().getAllLocationData()
                    .stream().findFirst().ifPresent(bot->{
                VisionCoordinate coord = bot.coord;

            });

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
        //Create a proxy, which we will use to control MATLAB
        MatlabProxyFactory factory = new MatlabProxyFactory();
        MatlabProxy proxy = factory.getProxy();
        //Display 'hello world' just like when using the demo
        proxy.eval("disp('hello world')");

//Disconnect the proxy from MATLAB
        proxy.disconnect();
    }
}
