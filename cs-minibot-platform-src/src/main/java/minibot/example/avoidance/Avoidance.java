//package minibot.example.avoidance;
//
//import basestation.BaseStation;
//import basestation.vision.VisionObject;
////import matlabcontrol.MatlabConnectionException;
////import matlabcontrol.MatlabInvocationException;
////import matlabcontrol.MatlabProxy;
////import matlabcontrol.MatlabProxyFactory;
////import matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions.MatlabNumericArray;
////import matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions.MatlabTypeConverter;
//
//import java.util.List;
//
//
//public class Avoidance extends Thread {
//    @Override
//    public void run() {
//        MatlabProxyFactory factory = new MatlabProxyFactory();
//        MatlabProxy proxy = null;
//        try {
//            proxy = factory.getProxy();
//        } catch (MatlabConnectionException e) {
//            e.printStackTrace();
//        }
//        //Display 'hello world' just like when using the demo
//        double[] x_vals = new double[] {2.231,2.404,0.894};
//        double [][] x = new double[1][1];
//        double [][] y = new double[1][1];
//        x[0] = x_vals;
//        double[] y_vals = new double[] {1.673,0.428,1.7875}; //matlabcontrol
//        // .trunk.matlabcontrol.src.matlabcontrol.extensions
//        y[0] = y_vals;
//        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
//        double[][] blank = new double[1][1];
//        try {
//            processor.setNumericArray("x_vals", new MatlabNumericArray(x,
//                    blank));
//        } catch (MatlabInvocationException e) {
//            e.printStackTrace();
//        }
//        try {
//            processor.setNumericArray("y_vals", new MatlabNumericArray(y,
//                    blank));
//        } catch (MatlabInvocationException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            proxy.eval("array = transpose(x_vals)");
//            proxy.eval("array = transpose(y_vals)");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int track  = 1;
//
//        while(track <= x_vals.length) {
//            List<VisionObject> vol = BaseStation.getInstance()
//                    .getVisionManager()
//                    .getAllLocationData();
//            if( vol.size() == 0) continue;
//            double theta = vol.get
//                            (0).coord.getThetaOrZero();
//            theta = (-theta + 2 * Math.PI) % (2 * Math.PI);
//            double x_pos = BaseStation.getInstance().getVisionManager()
//                    .getAllLocationData().get(0).coord.x;
//            double y_pos = BaseStation.getInstance().getVisionManager()
//                    .getAllLocationData().get(0).coord.y;
//            try {
//            proxy.setVariable("tracker",track);
//            proxy.setVariable("x_p",x_pos);
//            proxy.setVariable("y_p",y_pos);
//            proxy.setVariable("theta",theta);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////        Object res = proxy.returningFeval("Controller_mustafa",3,0,0,0,x_vals,
////                y_vals,1);
//            try {
//            proxy.eval("main_m");
//            } catch (Exception e) {
//e.printStackTrace();
//            }
//            try {
//                Object res1 = proxy.getVariable("u1");
//                Object res2 = proxy.getVariable("u2");
//                track = (int)((double[])(proxy.getVariable("tracker")))[0];
//                double u1 = ((double[])res1)[0];
//                double u2 = ((double[])res2)[0];
//                System.out.println(u2 + "," + u1 + "," + theta);
//                BaseStation.getInstance().getBotManager().getAllTrackedBots()
//                        .stream().findFirst().ifPresent(xx->{
//                    xx.getCommandCenter().sendKV("WHEELS",u2*20+","+u1*20+"," +
//                            "0,0");
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            try {
//                Thread.sleep(20);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        proxy.disconnect();
//
//    }
//
//
////    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
////        //Create a proxy, which we will use to control MATLAB
////        MatlabProxyFactory factory = new MatlabProxyFactory();
////        MatlabProxy proxy = factory.getProxy();
////        //Display 'hello world' just like when using the demo
////        proxy.eval("disp('hello world')");
////        double[] x_vals = new double[] {1,2,3,4,5,6};
////        double [][] x = new double[1][1];
////        double [][] y = new double[1][1];
////        x[0] = x_vals;
////        double[] y_vals = new double[] {2,4,6,8,10,12}; //matlabcontrol.trunk.matlabcontrol.src.matlabcontrol.extensions
////        y[0] = y_vals;
////        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
////        double[][] blank = new double[1][1];
////        processor.setNumericArray("x_vals", new MatlabNumericArray(x,
////                blank));
////        processor.setNumericArray("y_vals", new MatlabNumericArray(y,
////                blank));
////        proxy.eval("array = transpose(x_vals)");
////        proxy.eval("array = transpose(y_vals)");
////        int track  = 1;
////        while(true) {
////            double theta = BaseStation.getInstance().getVisionManager()
////                    .getAllLocationData().get
////                            (0).coord.getThetaOrZero();
////            double x_pos = BaseStation.getInstance().getVisionManager()
////                    .getAllLocationData().get(0).coord.x;
////            double y_pos = BaseStation.getInstance().getVisionManager()
////                    .getAllLocationData().get(0).coord.y;
////            proxy.setVariable("tracker",track);
////            proxy.setVariable("x_p",x_pos);
////            proxy.setVariable("y_p",y_pos);
////            proxy.setVariable("theta",theta);
////
////            System.out.println("Evalling mustafa's code");
//////        Object res = proxy.returningFeval("Controller_mustafa",3,0,0,0,x_vals,
//////                y_vals,1);
////            proxy.eval("main_m");
////            System.out.println("done with mustafa's code");
////            Object res1 = proxy.getVariable("u1");
////            Object res2 = proxy.getVariable("u2");
////            track = (Integer)(proxy.getVariable("tracker"));
////            double u1 = ((double[])res1)[0];
////            double u2 = ((double[])res2)[0];
//////            BaseStation.getInstance().getBotManager().getAllTrackedBots()
//////                    .stream().findFirst().ifPresent(x->{
//////                x.getCommandCenter().sendKV("WHEELS",u1+","+u2+",0,0");
//////            });
////        }
////
//////        double[][] transposedArray = processor.getNumericArray("x_vals")
//////                .getRealArray2D();
//////        Double[] integerArray = Arrays.copyOf(res, 2, Double[].class);
//////        System.out.println(integerArray);
////////        System.out.println(res[0].getClass());
////////        System.out.println(res[1].getClass());
//////
//////        //Print the returned array, now transposed
//////        System.out.println("Transposed: ");
//////        for(int i = 0; i < transposedArray.length; i++)
//////        {
//////            System.out.println(Arrays.toString(transposedArray[i]));
//////        }
////
//////Disconnect the proxy from MATLAB
////
////
//////Disconnect the proxy from MATLAB
////        proxy.disconnect();
////    }
//}
