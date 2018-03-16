

import utils.NetworkFunctions;
import utils.Statistics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


import visualize.LineChartSample;


public class RBFN {

    RBFN() {
        try (PrintWriter saveFile = new PrintWriter("ResultsRadialBasisNetwork.txt")) {
        } catch (Exception e) {
        }
    }

    static double[][] weightMatrix;
    static double[] centers;
    static double sigma = 0.00008;
    static int numberOfHiddenNeurons = 0;

    public static void trainAndTest(String a, String t, boolean visualize, String[] args, int index) {
        double[][] input = new double[][]{NetworkFunctions.convertStringToNumbers(a)};
        double[][] target = new double[][]{NetworkFunctions.convertStringToNumbers(t)};
        trainAndTest(input, target, visualize, args, index);
    }

    public static void trainAndTest(double[][] a, double[][] t, boolean visualize, String[] args, int index) {
        double[][] trainMenge = Statistics.divideTrainSet(a);
        numberOfHiddenNeurons = trainMenge[0].length;
        double[][] trainPreis = Statistics.divideTrainSet(t);

        double[][] testMenge = Statistics.divideTestSet(a);
        double[][] testPreis = Statistics.divideTestSet(t);

        centers = new double[trainMenge[0].length];
        for (int i = 0; i < trainMenge.length; i++) {
            for (int j = 0; j < trainMenge[i].length; j++) {
                centers[j] = a[i][j];
            }
        }


        NetworkFunctions.printArray(centers);






        System.out.println("Interpolation matrix is being calculated");
        double[][] matrixS = interpolationMatrix(trainMenge);
        System.out.println("Interpolation matrix is calculated");
        double[][] pseudoInverse = NetworkFunctions.pseudoInverse(matrixS);
        NetworkFunctions.printMatrixSize(pseudoInverse);
        System.out.println("Pseudo Inverse calculated: ");
        weightMatrix = NetworkFunctions.matrixMultiply(pseudoInverse, NetworkFunctions.transpose(trainPreis));
        System.out.println("weight matrix calculated: ");
        NetworkFunctions.printMatrixSize(weightMatrix);
//test


        double[][] m = interpolationMatrix(trainMenge);
        System.out.println("Predict ");
        NetworkFunctions.printMatrixSize(weightMatrix);
        NetworkFunctions.printMatrixSize(m);
        NetworkFunctions.printMatrixSize(t);
        double[][] result = NetworkFunctions.matrixMultiply(
                NetworkFunctions.transpose(weightMatrix), NetworkFunctions.transpose(m));

        System.out.println("Accuracy train:" + Statistics.measureAccuracy2(result, trainPreis));
        NetworkFunctions.printArray(result);


        double[][] m2 = interpolationMatrix(testMenge);
        System.out.println("Predict ");
        NetworkFunctions.printMatrixSize(weightMatrix);
        NetworkFunctions.printMatrixSize(m);
        NetworkFunctions.printMatrixSize(t);
        double[][] result2 = NetworkFunctions.matrixMultiply(
                NetworkFunctions.transpose(weightMatrix), NetworkFunctions.transpose(m2));

        System.out.println("Accuracy test:" + Statistics.measureAccuracy(result2, testPreis));
        NetworkFunctions.printArray(result2);
        System.out.println("Start result " + index + " *****************************************************");

        String s = "\n Data set number " + index;

        s += "\nAccuracy Train : " + Statistics.measureAccuracy(result, trainPreis);
        s += "\nAccuracy Test : " + Statistics.measureAccuracy(result2, testPreis);
        double[][] errorOutput = NetworkFunctions.defineErrors(trainPreis, result);
        double errors = (NetworkFunctions.matrixSum(errorOutput));

        s += "\n Mean absolute error (train phase): " + errors;
        errorOutput = NetworkFunctions.defineErrors(testPreis, result2);
        errors = (NetworkFunctions.matrixSum(errorOutput));
        s += "\n Mean absolute error (test phase): " + errors;

        s += "\n" + "Mean price Train: " + Statistics.mean(trainPreis);
        s += "\nMean price Test: " + Statistics.mean(testPreis);
        s += "\n number of elements in train phase: " + trainMenge[0].length;
        s += "\n number of elements in test phase: " + testMenge[0].length;
        System.out.println("End result*****************************************************");
        System.out.println("***************************************************************************************");

        s += "\n------------------------------------------------------------------------\n";
        s += "\n*************************************************************************\n";
        try {
            Files.write(Paths.get("ResultsRadialBasisNetwork.txt"), s.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {

        }

        if (visualize) {
            String label = "Radial basis function network ";
            LineChartSample.setData(trainMenge, result, testMenge, result2, a, t, NetworkFunctions.getMaxValue(result) + 5,
                    NetworkFunctions.getMaxValue(t) + 15, label);
            LineChartSample.main(args);
        }


    }

    public static void predict(double[][] a, double[][] t, boolean visualize, String[] args) {
        double[][] m = interpolationMatrix(a);
        System.out.println("Predict ");
        NetworkFunctions.printMatrixSize(weightMatrix);
        NetworkFunctions.printMatrixSize(m);
        NetworkFunctions.printMatrixSize(t);
        double[][] result = NetworkFunctions.matrixMultiply(
                NetworkFunctions.transpose(weightMatrix), NetworkFunctions.transpose(m));

        System.out.println("Accuracy :" + Statistics.measureAccuracy(result, t));
        NetworkFunctions.printArray(result);

        if (visualize) {
            String label = "Radial basis function network ";
            LineChartSample.setData(a, result, a, t, NetworkFunctions.getMaxValue(result) + 5,
                    NetworkFunctions.getMaxValue(t) + 15, label);
            LineChartSample.main(args);
        }

    }


    public static double[][] interpolationMatrix(double[][] a) {

        double[] s = NetworkFunctions.convertToOneDimension(a);
        double[][] matrixS = new double[a[0].length][numberOfHiddenNeurons];

//        for (int i = 0; i < s.length; i++) {
//            for (int k = 0; k < centers.length; k++) {
//                matrixS[i][k] = calculateKernel( s[i], centers[k]);
//
//            }
//        }

        for (int i = 0; i < s.length; i++) {
                matrixS[i]=subMatrix(matrixS[i],s,i);
        }
        //NetworkFunctions.printArray(matrixS);

        return matrixS;
    }

    public static double[] subMatrix(double[] a, double[] s, int index) {
            for (int k = 0; k < a.length; k++){
                for(int j=0; j<s.length; j++)
                    a[index] = calculateKernel(s[j], centers[k]);
        }



        return a;
    }


    public static double calculateKernel(double a, double b) {
        return Math.exp(-sigma * Math.pow(a - b, 2));
    }


    public static void main(String[] args) {

        double[][] input = new double[][]{NetworkFunctions.convertStringToNumbers(Data.mengen[6])};
        double[][] target = new double[][]{NetworkFunctions.convertStringToNumbers(Data.preise[6])};

        double[][] menge = {{2, 4, 6, 8, 10, 19, 40, 14, 20, 12, 19, 6, 123,51,200}};
        double[][] preis = {{4, 7, 10, 20, 14, 34, 50, 65, 6, 1, 13, 8, 15, 8, 12}};


       trainAndTest(input, target, true, args, 1);

        //   trainAndTest(menge, preis, true, args, 1);
    }


}




