package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MatrixMultBenchmarking {

    @Param({"10", "100", "1000"})
    private int n;

    private double[][] a;
    private double[][] b;

    @Setup(Level.Trial)
    public void setup() {
        a = new double[n][n];
        b = new double[n][n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextDouble();
                b[i][j] = random.nextDouble();
            }
        }
    }

    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @Benchmark
    public void multiplication() {
        new MatrixMult().execute(a, b);
    }
}
