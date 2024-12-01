package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS) // 3 warmup iterations, each lasting 1 second
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS) // 5 measurement iterations, each lasting 2 seconds
public class BenchmarkMatrixMultiplication {

    @Param({"10", "100", "1000"}) // Parametrize matrix sizes
    private int MATRIX_SIZE;

    @Param({"1", "2", "4"}) // Parametrize number of threads
    private int NUM_THREADS;

    private double[][] matrixA;
    private double[][] matrixB;

    @Setup(Level.Iteration)
    public void setup() {
        matrixA = generateMatrix();
        matrixB = generateMatrix();
    }

    @Benchmark
    public double[][] basicMultiplication() {
        return BasicMatrixMultiplication.multiply(matrixA, matrixB);
    }

    @Benchmark
    public double[][] vectorizedMultiplication() {
        return VectorizedMatrixMultiplication.multiply(matrixA, matrixB);
    }

    @Benchmark
    public double[][] parallelMultiplication() throws InterruptedException {
        return ParallelMatrixMultiplication.multiply(matrixA, matrixB, NUM_THREADS);
    }

    private double[][] generateMatrix() {
        double[][] matrix = new double[MATRIX_SIZE][MATRIX_SIZE];
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
}
