package org.example;

public class OptimizedMatrixMultiplication {
    public static double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        double[][] C = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                double sum = 0;
                for (int k = 0; k < colsA; k += 2) {
                    sum += A[i][k] * B[k][j];
                    if (k + 1 < colsA) {
                        sum += A[i][k + 1] * B[k + 1][j];
                    }
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
}
