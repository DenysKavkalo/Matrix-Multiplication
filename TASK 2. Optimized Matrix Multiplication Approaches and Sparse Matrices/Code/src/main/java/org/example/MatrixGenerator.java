package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MatrixGenerator {
    public static double[][] generateDenseMatrix(int rows, int cols) {
        Random rand = new Random();
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = rand.nextDouble();
            }
        }
        return matrix;
    }

    public static Map<Integer, Map<Integer, Double>> generateSparseMatrix(
            int rows, int cols, double sparsity) {
        Random rand = new Random();
        Map<Integer, Map<Integer, Double>> matrix = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rand.nextDouble() > sparsity) {
                    matrix.computeIfAbsent(i, x -> new HashMap<>()).put(j, rand.nextDouble());
                }
            }
        }
        return matrix;
    }
}
