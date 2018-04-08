package networks;

import utils.NetworkFunctions;
import utils.Statistics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;


import visualize.LineChartSample;

import javax.swing.*;


public class RBFN extends Network {

    RBFN() {
        try (PrintWriter saveFile = new PrintWriter("ResultsRadialBasisNetwork.txt")) {
        } catch (Exception e) {
        }
    }

    static double[][] weightMatrix;
    static double[] centers;
    static double sigma = 1;
    static int numberOfHiddenNeurons = 0;

    public void trainAndTest(String a, String t, boolean visualize, String[] args, int index) {
        double[][] input = new double[][]{NetworkFunctions.convertStringToNumbers(a)};
        double[][] target = new double[][]{NetworkFunctions.convertStringToNumbers(t)};
        trainAndTest(input, target, visualize, args, index);
    }

    public void trainAndTest(double[][] a, double[][] t, boolean visualize, String[] args, int index) {

        double[][] trainMenge = a; //Statistics.divideTrainSet(a);
        numberOfHiddenNeurons = trainMenge[0].length*2;
        System.out.println(trainMenge[0].length);
        double[][] trainPreis = t;// Statistics.divideTrainSet(t);

        double[][] testMenge = Statistics.divideTestSet(a);
        double[][] testPreis = Statistics.divideTestSet(t);

//        NetworkFunctions.printArray(testMenge);
//        NetworkFunctions.printArray(testPreis);

        centers = new double[trainMenge[0].length];
        for (int i = 0; i < trainMenge.length; i++) {
            for (int j = 0; j < trainMenge[i].length; j++) {
                centers[j] = a[i][j];
            }
        }


        NetworkFunctions.printArray(centers);

        System.out.println("Interpolation matrix is being calculated");
        double[][] matrixS = interpolationMatrix(trainMenge);
        NetworkFunctions.printArray(matrixS);
        System.out.println("Interpolation matrix is calculated");
        double[][] pseudoInverse = NetworkFunctions.pseudoInverse(matrixS);
        NetworkFunctions.printMatrixSize(pseudoInverse);
       // NetworkFunctions.printArray(pseudoInverse);
        System.out.println("Pseudo Inverse calculated: ");
        weightMatrix = NetworkFunctions.matrixMultiply(pseudoInverse, NetworkFunctions.transpose(trainPreis));
      //  System.out.println("weight matrix calculated: ");

//test


        double[][] m = interpolationMatrix(trainMenge);
        System.out.println("Predict ");
        NetworkFunctions.printMatrixSize(weightMatrix);
        NetworkFunctions.printMatrixSize(m);
        NetworkFunctions.printMatrixSize(t);
//        double[][] result = NetworkFunctions.matrixMultiply(
//                NetworkFunctions.transpose(weightMatrix), NetworkFunctions.transpose(m));
        double[][] result = NetworkFunctions.matrixMultiply(m, weightMatrix);
        NetworkFunctions.printArray(result);
        result=NetworkFunctions.transpose(result);
        System.out.println("Accuracy train:" + Statistics.measureAccuracy2(result, trainPreis));


        System.out.println("Predict test");
        double[][] m2 = interpolationMatrix(testMenge);
        System.out.println("Interpolation matrix");
        NetworkFunctions.printArray(m2);

        NetworkFunctions.printMatrixSize(weightMatrix);
//        NetworkFunctions.printMatrixSize(m);
//        NetworkFunctions.printMatrixSize(t);
        double[][] result2 = NetworkFunctions.matrixMultiply(
                NetworkFunctions.transpose(weightMatrix), NetworkFunctions.transpose(m2));

        System.out.println("Accuracy test:" + Statistics.measureAccuracy(result2, testPreis));
        NetworkFunctions.printArray(result2);
        System.out.println("Start result " + index + " *****************************************************");

        String s = "\n Data set number " + index;

        s += "\nAccuracy Train : " + Statistics.measureAccuracy(result, trainPreis);
        s += "\nAccuracy Test : " + Statistics.measureAccuracy(result2, testPreis);
        double accTrain = Statistics.measureAccuracy(result, trainPreis);
        double accTest = Statistics.measureAccuracy(result2, testPreis);
        String label = "Radial basis function network " + ", \naccuracy in training phase " + accTrain + ", " +
                "\naccuracy in test phase " + accTest;

        double[][] errorOutput = NetworkFunctions.defineErrors(trainPreis, result);
        double errors = (NetworkFunctions.matrixSum(errorOutput));
        System.out.println("error Train:" + errors);
        s += "\n Mean absolute error (train phase): " + errors;
        errorOutput = NetworkFunctions.defineErrors(testPreis, result2);
        errors = (NetworkFunctions.matrixSum(errorOutput));
        s += "\n Mean absolute error (test phase): " + errors;
        System.out.println("error Test:" + errors);
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
            LineChartSample.network = this;
            LineChartSample.setData(trainMenge, result, testMenge, result2, a, t, NetworkFunctions.getMaxValue(result) + 5,
                    NetworkFunctions.getMaxValue(t) + 15, label);
            LineChartSample.main(args);
        } else {
            LineChartSample.setData(trainMenge, result, testMenge, result2, a, t, NetworkFunctions.getMaxValue(result) + 5,
                    NetworkFunctions.getMaxValue(t) + 15, label);
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
//
        for (int i = 0; i < s.length; i++) {
            for (int k = 0; k < centers.length; k++) {
                matrixS[i][k] = calculateKernel(s[i], centers[k]);
            }
        }
//
//        for (int i = 0; i < s.length; i++) {
//                matrixS[i]=subMatrix(matrixS[i],s,i);
//        }
        //  NetworkFunctions.printArray(matrixS);

        return matrixS;
    }

    public static double[] subMatrix(double[] a, double[] s, int index) {
        for (int k = 0; k < a.length; k++) {
            for (int j = 0; j < s.length; j++)
                a[index] = calculateKernel(s[j], centers[k]);
        }
        return a;
    }


    public static double calculateKernel(double a, double b) {
        return Math.exp(-sigma * Math.pow(a - b, 2));
    }


    public static void main(String[] args) {

        double[][] input = new double[][]{NetworkFunctions.convertStringToNumbers(Data.mengen[13])};
        double[][] target = new double[][]{NetworkFunctions.convertStringToNumbers(Data.preise[13])};

        double[][] menge = {{2, 13, 34, 6, 9, 23}};
        double[][] preis = {{4, 2, 60, 8, 5, 43}};

        RBFN rbfn = new RBFN();
        //   trainAndTest(input, target, true, args, 1);

        rbfn.trainAndTest(menge, preis, false, args, 1);

//        NetworkFunctions.printMatrixSize(menge);
//        NetworkFunctions.printArray(NetworkFunctions.matrixMultiply(menge, preis));
//        NetworkFunctions.printArray(NetworkFunctions.matrixMultiply2(menge,preis));
    }


}




