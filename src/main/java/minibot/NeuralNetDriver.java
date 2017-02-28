package minibot;

import basestation.BaseStation;
import basestation.bot.robot.modbot.ModbotCommandCenter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.data.ImageWritable;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.loader.ImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.*;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NeuralNetDriver extends Thread{

    MultiLayerNetwork model;
    ImageTransform transform;
    ParentPathLabelGenerator labelMaker;
    BaseStation bs;

    public NeuralNetDriver(BaseStation bs) {
        this.bs = bs;
        autonomous = false;
    }

    int testW = 128;
    int testH = 96;
    int nChannels = 3; // Number of input channels
    public boolean autonomous;


    public void learnstuff() throws Exception {
        int outputNum = 4; // The number of possible outcomes
        int batchSize = 16; // Test batch size
        int nEpochs = 27; // Number of training epochs
        int iterations = 1; // Number of training iterations
        int seed = 123; //


        /*
            Create an iterator using the batch size for one iteration
         */
        //log.info("Load data....");
        File parentDir = new File(System.getProperty("user.dir"), "log");
        Random randNumGen = new Random();
        FileSplit filesInDir = new FileSplit(parentDir, BaseImageLoader.ALLOWED_FORMATS, randNumGen);
        labelMaker = new ParentPathLabelGenerator();
        BalancedPathFilter pathFilter = new BalancedPathFilter(randNumGen, BaseImageLoader.ALLOWED_FORMATS, labelMaker);
        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 85, 15);
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];
        transform = new MultiImageTransform(new ResizeImageTransform(testW,testH), new ShowImageTransform("After transform"));
        //ImageTransform transform = new ScaleImageTransform(0.1f);
        ImageRecordReader recordReader = new ImageRecordReader(testH,testW,nChannels,labelMaker);
        recordReader.initialize(trainData,transform);
        //DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, 2, 0, outputNum);
        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader,batchSize);
        ImageRecordReader testrecordReader = new ImageRecordReader(testH,testW,nChannels,labelMaker);
        testrecordReader.initialize(testData);
        DataSetIterator test = new RecordReaderDataSetIterator(testrecordReader,batchSize);




        /*
            Construct the neural network
         */
        //log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations) // Training iterations as above
                .learningRate(.005)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.RMSPROP).rmsDecay(0.95)//.momentum(0.9)
                .list(
                        new ConvolutionLayer.Builder(5, 5)
                                //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                                .nIn(nChannels)
                                .stride(2, 2)
                                .nOut(20)
                                .dropOut(0.70)
                                .activation(Activation.RELU)
                                .weightInit(WeightInit.RELU)
                                .build(),

                        new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                                .kernelSize(2,2)
                                //.stride(1,1)
                                //.activation(Activation.RELU)
                                .build(),


                        new ConvolutionLayer.Builder(5, 5)
                        //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                           // .nIn(nChannels)
                            .stride(1, 1)
                            .nOut(40)
                            .dropOut(0.70)
                            .activation(Activation.RELU)
                            .build(),
                        new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                                .kernelSize(2,2)
                                .stride(1,1)
                                .activation(Activation.RELU)
                                .build(),


                        new DenseLayer.Builder().activation(Activation.RELU)
                                .nOut(100).build(),
                        new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nOut(outputNum)
                                .activation(Activation.SOFTMAX)
                                .build())
                .setInputType(InputType.convolutional(testH,testW,1)) //See note below
                .backprop(true).pretrain(false).build();

        /*
        Regarding the .setInputType(InputType.convolutionalFlat(28,28,1)) line: This does a few things.
        (a) It adds preprocessors, which handle things like the transition between the convolutional/subsampling layers
            and the dense layer
        (b) Does some additional configuration validation
        (c) Where necessary, sets the nIn (number of input neurons, or input depth in the case of CNNs) values for each
            layer based on the size of the previous layer (but it won't override values manually set by the user)
        InputTypes can be used with other layer types too (RNNs, MLPs etc) not just CNNs.
        For normal images (when using ImageRecordReader) use InputType.convolutional(height,width,depth).
        MNIST record reader is a special case, that outputs 28x28 pixel grayscale (nChannels=1) images, in a "flattened"
        row vector format (i.e., 1x784 vectors), hence the "convolutionalFlat" input type used here.
        */

        model = new MultiLayerNetwork(conf);
        model.init();


        //log.info("Train model....");
        model.setListeners(new ScoreIterationListener(1));
        for( int i=0; i<nEpochs; i++ ) {
            model.fit(dataIter);
            //log.info("*** Completed epoch {} ***", i);
            recordReader.initialize(testData);

           // log.info("Evaluate model....");
            System.out.println("moo");

            Evaluation eval = new Evaluation(outputNum);
            System.out.println("moo");
            while(test.hasNext()){
                DataSet ds = test.next();
                //System.out.println(ds);
                INDArray output = model.output(ds.getFeatureMatrix(), false);
                eval.eval(ds.getLabels(), output);
            }
            System.out.println(eval.stats());
            //log.info(eval.stats());
            dataIter.reset();
            test.reset();
        }
        //log.info("****************Example finished********************");
    }

    public int decideWhatToDo() throws IOException {
        ImageGrabber.grabImage();
        File f = new File("./predict/wtf/currentImage.jpg");
        //NativeImageLoader nil = new NativeImageLoader(640,480,3);
        //INDArray image = nil.asMatrix(f);
        /*
        Java2DFrameConverter helpmee = new Java2DFrameConverter();
        ImageLoader ahhhhh = new ImageLoader();
        BufferedImage bi = ImageIO.read(f);
        Frame myFrame = (new Java2DFrameConverter()).convert(bi);
        ImageWritable help = new ImageWritable(myFrame);
        ImageWritable result = transform.transform(help);
        INDArray thisDumb = ahhhhh.asMatrix(helpmee.getBufferedImage(result.getFrame()));
        */
        // f.delete();
        //int[] prediction = model.predict(thisDumb);
        File parentDir = new File(System.getProperty("user.dir"), "predict");
        Random randNumGen = new Random();
        FileSplit filesInDir = new FileSplit(parentDir, BaseImageLoader.ALLOWED_FORMATS, randNumGen);
        BalancedPathFilter pathFilter = new BalancedPathFilter(randNumGen, BaseImageLoader.ALLOWED_FORMATS, labelMaker);
        ImageRecordReader predictRecordReader = new ImageRecordReader(testH,testW,nChannels,labelMaker);
        predictRecordReader.initialize(filesInDir, transform);
        DataSetIterator test = new RecordReaderDataSetIterator(predictRecordReader,1);
        INDArray output = model.output(test.next().getFeatureMatrix(),false);
        System.out.println(output);
        int maxDex = -1;
        double maxProb = 0;
        for (int i = 0; i < 4; i++) {
            double curProb = output.getDoubleUnsafe(i);
            if (curProb > maxProb) {
                maxDex = i;
                maxProb = curProb;
            }
        }

        // Decode index
        switch (maxDex) {
            case 0:
                System.out.println("STOP");
                return DataLogger.STOP;
            case 1:
                System.out.println("FORWARD");
                return DataLogger.FORWARD;
            case 2:
                System.out.println("CLOCKWISE");
                return DataLogger.CLOCKWISE;
            case 3:
                System.out.println("C-CLOCKWISE");
                return DataLogger.CCLOCKWISE;
            /*
            case 2:
                System.out.println("BACKWARD");
                return DataLogger.BACKWARD;
            case 3:
                System.out.println("CLOCKWISE");
                return DataLogger.CLOCKWISE;
            case 4:
                System.out.println("C-CLOCKWISE");
                return DataLogger.CCLOCKWISE;
                */
        }
        return -1;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int power = 12;
                Thread.sleep(450);
                if (!autonomous) continue;
                int decision = decideWhatToDo();
                if (decision == 0) {
                    ((ModbotCommandCenter)bs.getBotManager().getBotById(0).getCommandCenter()).setWheelPower(-0,0,-0,0);
                } else if (decision == 1) {
                    ((ModbotCommandCenter)bs.getBotManager().getBotById(0).getCommandCenter()).setWheelPower(power,power,power,power);
                } else if (decision == 2) {
                    ((ModbotCommandCenter)bs.getBotManager().getBotById(0).getCommandCenter()).setWheelPower(-power,-power,-power,-power);
                } else if (decision == 3) {
                    ((ModbotCommandCenter)bs.getBotManager().getBotById(0).getCommandCenter()).setWheelPower(power,-power,power,-power);
                } else {
                    ((ModbotCommandCenter)bs.getBotManager().getBotById(0).getCommandCenter()).setWheelPower(-power,power,-power,power);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}