package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmark {

    @Param({"10", "100", "1000"})
    private int matrixSize;

    @Param({"0.1", "0.5", "0.8"})
    private double sparsity;

    private double[][] denseMatrixA;
    private double[][] denseMatrixB;
    private Map<Integer, Map<Integer, Double>> sparseMatrixA;
    private Map<Integer, Map<Integer, Double>> sparseMatrixB;

    @Setup
    public void setup() {
        denseMatrixA = MatrixGenerator.generateDenseMatrix(matrixSize, matrixSize);
        denseMatrixB = MatrixGenerator.generateDenseMatrix(matrixSize, matrixSize);

        sparseMatrixA = MatrixGenerator.generateSparseMatrix(matrixSize, matrixSize, sparsity);
        sparseMatrixB = MatrixGenerator.generateSparseMatrix(matrixSize, matrixSize, sparsity);
    }

    @Benchmark
    public void testBasicMultiplication() {
        BasicMatrixMultiplication.multiply(denseMatrixA, denseMatrixB);
    }

    @Benchmark
    public void testStrassenMultiplication() {
        StrassenMatrixMultiplication.multiply(denseMatrixA, denseMatrixB);
    }

    @Benchmark
    public void testOptimizedMultiplication() {
        OptimizedMatrixMultiplication.multiply(denseMatrixA, denseMatrixB);
    }

    @Benchmark
    public void testSparseMultiplication() {
        SparseMatrixMultiplication.multiply(sparseMatrixA, sparseMatrixB);
    }
}
