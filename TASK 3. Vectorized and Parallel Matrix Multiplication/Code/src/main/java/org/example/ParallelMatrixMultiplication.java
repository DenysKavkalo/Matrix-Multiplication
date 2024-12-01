package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B, int numThreads) throws InterruptedException {
        int rows = A.length;
        int cols = B[0].length;
        int n = A[0].length;

        double[][] result = new double[rows][cols];

        // Create a fixed thread pool with the specified number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < rows; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < cols; j++) {
                    for (int k = 0; k < n; k++) {
                        result[row][j] += A[row][k] * B[k][j];
                    }
                }
            });
        }

        // Shutdown executor gracefully
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            System.err.println("Timeout while waiting for tasks to finish");
        }

        return result;
    }
}
