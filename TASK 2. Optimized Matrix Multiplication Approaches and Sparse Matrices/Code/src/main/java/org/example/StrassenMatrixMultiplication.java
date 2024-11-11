package org.example;

public class StrassenMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        int m = nextPowerOfTwo(n);
        double[][] APrep = padMatrix(A, m);
        double[][] BPrep = padMatrix(B, m);

        double[][] result = strassenMultiply(APrep, BPrep);

        return trimMatrix(result, n);
    }

    private static double[][] strassenMultiply(double[][] A, double[][] B) {
        int n = A.length;

        if (n == 1) {
            return new double[][] {{ A[0][0] * B[0][0] }};
        }

        int newSize = n / 2;

        double[][] A11 = new double[newSize][newSize];
        double[][] A12 = new double[newSize][newSize];
        double[][] A21 = new double[newSize][newSize];
        double[][] A22 = new double[newSize][newSize];

        double[][] B11 = new double[newSize][newSize];
        double[][] B12 = new double[newSize][newSize];
        double[][] B21 = new double[newSize][newSize];
        double[][] B22 = new double[newSize][newSize];

        splitMatrix(A, A11, 0, 0);
        splitMatrix(A, A12, 0, newSize);
        splitMatrix(A, A21, newSize, 0);
        splitMatrix(A, A22, newSize, newSize);

        splitMatrix(B, B11, 0, 0);
        splitMatrix(B, B12, 0, newSize);
        splitMatrix(B, B21, newSize, 0);
        splitMatrix(B, B22, newSize, newSize);

        double[][] M1 = strassenMultiply(addMatrices(A11, A22), addMatrices(B11, B22));
        double[][] M2 = strassenMultiply(addMatrices(A21, A22), B11);
        double[][] M3 = strassenMultiply(A11, subtractMatrices(B12, B22));
        double[][] M4 = strassenMultiply(A22, subtractMatrices(B21, B11));
        double[][] M5 = strassenMultiply(addMatrices(A11, A12), B22);
        double[][] M6 = strassenMultiply(subtractMatrices(A21, A11), addMatrices(B11, B12));
        double[][] M7 = strassenMultiply(subtractMatrices(A12, A22), addMatrices(B21, B22));

        double[][] C11 = addMatrices(subtractMatrices(addMatrices(M1, M4), M5), M7);
        double[][] C12 = addMatrices(M3, M5);
        double[][] C21 = addMatrices(M2, M4);
        double[][] C22 = addMatrices(subtractMatrices(addMatrices(M1, M3), M2), M6);

        double[][] C = new double[n][n];
        joinMatrix(C11, C, 0, 0);
        joinMatrix(C12, C, 0, newSize);
        joinMatrix(C21, C, newSize, 0);
        joinMatrix(C22, C, newSize, newSize);

        return C;
    }

    private static int nextPowerOfTwo(int n) {
        return (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2)));
    }

    private static double[][] padMatrix(double[][] matrix, int size) {
        double[][] padded = new double[size][size];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, padded[i], 0, matrix[i].length);
        }
        return padded;
    }

    private static double[][] trimMatrix(double[][] matrix, int size) {
        double[][] trimmed = new double[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrix[i], 0, trimmed[i], 0, size);
        }
        return trimmed;
    }

    private static void splitMatrix(double[][] source, double[][] target, int row, int col) {
        for (int i = 0; i < target.length; i++) {
            System.arraycopy(source[row + i], col, target[i], 0, target.length);
        }
    }

    private static void joinMatrix(double[][] source, double[][] target, int row, int col) {
        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, target[row + i], col, source.length);
        }
    }

    private static double[][] addMatrices(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    private static double[][] subtractMatrices(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }
}
