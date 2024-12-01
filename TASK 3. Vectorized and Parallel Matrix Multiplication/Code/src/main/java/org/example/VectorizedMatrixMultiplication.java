package org.example;

import java.util.stream.IntStream;

public class VectorizedMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = B[0].length;
        int n = A[0].length;

        double[][] result = new double[rows][cols];

        IntStream.range(0, rows).parallel().forEach(i -> {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        });

        return result;
    }
}