package com.example.benchmark;


import java.io.Serializable;
import java.util.concurrent.Callable;

public class MatrixMultiplicationTask implements Callable<double[][]>, Serializable {

    private final double[][] A;
    private final double[][] B;
    private final int fromRow;
    private final int toRow;

    public MatrixMultiplicationTask(double[][] A, double[][] B, int fromRow, int toRow) {
        this.A = A;
        this.B = B;
        this.fromRow = fromRow;
        this.toRow = toRow;
    }

    @Override
    public double[][] call() {
        int n = B[0].length;
        int m = A[0].length;
        double[][] resultPart = new double[toRow - fromRow][n];
        for (int i = fromRow; i < toRow; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < m; k++) {
                    sum += A[i][k] * B[k][j];
                }
                resultPart[i - fromRow][j] = sum;
            }
        }
        return resultPart;
    }
}
