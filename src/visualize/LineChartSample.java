package visualize;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javafx.animation.PauseTransition;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LineChartSample extends Application {

    static double[][] a;
    static double[][] b;
    static double[][] c;
    static double[][] d;
    static double[][] t1;
    static double[][] t2;
    static String label = "";
    static double rX;
    static double rY;

    @Override
    public void start(Stage stage) {
            // the wumpus doesn't leave when the last stage is hidden.

            final NumberAxis xAxis = new NumberAxis(0, rX, 10);
            final NumberAxis yAxis = new NumberAxis(0, rY, 1);

            xAxis.setLabel("Quantity");
            yAxis.setLabel("Price");
            final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
            final LineChart<Number, Number> lc = new LineChart<>(xAxis, yAxis);

            lc.setTitle(label);
            XYChart.Series series1 = new XYChart.Series();

            series1.setName("Trained Data");
            // populating the series with data
            if (a != null && b != null) {
                for (int i = 0; i < a.length; i++) {
                    for (int j = 0; j < a[i].length; j++) {
                        series1.getData().add(new XYChart.Data(a[i][j], b[i][j]));
                    }
                }
            }
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Original Data");
            if (c != null && d != null) {
                for (int i = 0; i < c.length; i++) {
                    for (int j = 0; j < c[i].length; j++) {
                        series2.getData().add(new XYChart.Data(c[i][j], d[i][j]));
                    }
                }
            }

            XYChart.Series series3 = new XYChart.Series();
            series3.setName("Test Data");
            if (t1 != null && t2 != null) {
                for (int i = 0; i < t1.length; i++) {
                    for (int j = 0; j < t1[i].length; j++) {
                        series3.getData().add(new XYChart.Data(t1[i][j], t2[i][j]));
                    }
                }
            }

            // StackPane pane=new StackPane();
            // pane.setPrefSize(700,600);
            // pane.getChildren().add(sc);
            // pane.getChildren().add(lc);
            // sc.setOpacity(.4);
            // lc.setOpacity(0.5);

            lc.getData().addAll(series1, series2, series3);
            lc.setStyle("-fx-font-size: " + 25 + "px;");
            lc.setStyle("-fx-font-size: " + 25 + "px;");

            Scene scene = new Scene(lc, 1200, 800);
            stage.setScene(scene);
            stage.show();


    }

    public static void setData(double[][] a1, double[][] b1, double rangeX, double rangeY, String s) {
        a = a1;
        b = b1;
        rX = rangeX;
        rY = rangeY;
        label = s;
    }

    public static void setData(double[][] a1, double[][] b1, double[][] c1, double[][] d1, double rangeX,
                               double rangeY, String s) {
        a = a1;
        b = b1;
        c = c1;
        d = d1;
        rX = rangeX;
        rY = rangeY;
        label = s;
    }

    public static void setData(double[][] a1, double[][] b1, double[][] test1, double[][] test2, double[][] c1,
                               double[][] d1, double rangeX, double rangeY, String s) {
        a = a1;
        b = b1;
        c = c1;
        d = d1;
        t1 = test1;
        t2 = test2;
        rX = rangeX;
        rY = rangeY;
        label = s;
    }

    public static void main(String[] args) {
        launch(args);
    }
}