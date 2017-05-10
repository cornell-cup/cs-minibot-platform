package minibot.example.avoidance;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions.MatlabTypeConverter;

import java.util.Arrays;


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
        double[] x_vals = new double[] {1,2,3,4,5,6};
        double [][] x = new double[1][1];
        x[0] = x_vals;
        double[] y_vals = new double[] {2,4,6,8,10,12}; //matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        double[][] blank = new double[1][1];
        processor.setNumericArray("x_vals", new MatlabNumericArray(x,
                        blank));
        proxy.eval("array = transpose(x_vals)");
        double[][] transposedArray = processor.getNumericArray("x_vals")
                .getRealArray2D();

        //Print the returned array, now transposed
        System.out.println("Transposed: ");
        for(int i = 0; i < transposedArray.length; i++)
        {
            System.out.println(Arrays.toString(transposedArray[i]));
        }

//Disconnect the proxy from MATLAB


//Disconnect the proxy from MATLAB
        proxy.disconnect();
    }
}
