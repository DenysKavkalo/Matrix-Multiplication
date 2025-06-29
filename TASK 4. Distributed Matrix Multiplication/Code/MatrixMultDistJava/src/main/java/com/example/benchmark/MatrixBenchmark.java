package com.example.benchmark;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class MatrixBenchmark {

    private static final int[] MATRIX_SIZES = {10, 100, 500, 1000, 3000};
    private static final int NUM_WORKERS = 4;

    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = new XmlClientConfigBuilder("hazelcast-client.xml").build();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IExecutorService executor = client.getExecutorService("default");

        MetricsLogger logger = new MetricsLogger();

        try {
            for (int size : MATRIX_SIZES) {
                System.out.println("Running benchmark for matrix of size " + size);
                try {
                    benchmarkMatrix(size, executor, logger);
                } catch (Exception e) {
                    System.err.println("Error during benchmark of size " + size + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("Benchmark completed. CSV generated at /app/results/benchmark_results.csv");
        } finally {
            logger.close();
            client.shutdown();
        }
    }

    private static void benchmarkMatrix(int size, IExecutorService executor, MetricsLogger logger) throws Exception {
        double[][] A = Utils.generateRandomMatrix(size, size);
        double[][] B = Utils.generateRandomMatrix(size, size);

        long memBefore = Utils.getUsedMemoryMB();
        double cpuBefore = Utils.getProcessCpuLoadPercent();

        long uploadStart = System.currentTimeMillis();

        int rowsPerWorker = size / NUM_WORKERS;
        List<Future<double[][]>> futures = new ArrayList<>();

        for (int i = 0; i < NUM_WORKERS; i++) {
            int fromRow = i * rowsPerWorker;
            int toRow = (i == NUM_WORKERS - 1) ? size : fromRow + rowsPerWorker;
            MatrixMultiplicationTask task = new MatrixMultiplicationTask(A, B, fromRow, toRow);
            futures.add(executor.submit(task));
        }
        long uploadEnd = System.currentTimeMillis();
        long uploadTime = uploadEnd - uploadStart;

        long computeStart = System.currentTimeMillis();

        double[][] C = new double[size][size];
        int currentRow = 0;
        for (Future<double[][]> future : futures) {
            double[][] part = future.get();
            for (double[] row : part) {
                System.arraycopy(row, 0, C[currentRow], 0, size);
                currentRow++;
            }
        }

        long computeEnd = System.currentTimeMillis();
        long computeTime = computeEnd - computeStart;
        long totalTime = computeEnd - uploadStart;

        long memAfter = Utils.getUsedMemoryMB();
        double cpuAfter = Utils.getProcessCpuLoadPercent();

        long memUsed = memAfter - memBefore;

        double networkTransferMB = 2.0 * size * size * 8 / (1024.0 * 1024.0);
        double transferRateMBs = networkTransferMB / (uploadTime / 1000.0 + 0.0001);

        logger.log(size, totalTime, uploadTime, computeTime, networkTransferMB, transferRateMBs,
                memBefore, memAfter, memUsed, cpuBefore, cpuAfter, NUM_WORKERS);

        System.out.printf("Size: %d, Total: %d ms, Upload: %d ms, Compute: %d ms\n", size, totalTime, uploadTime, computeTime);
    }
}
