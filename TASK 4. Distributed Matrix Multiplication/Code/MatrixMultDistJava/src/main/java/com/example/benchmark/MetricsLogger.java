package com.example.benchmark;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MetricsLogger {

    private static final String DIRECTORY = "/app/results";
    private static final String FILE_PATH = DIRECTORY + "/benchmark_results.csv";

    private CSVPrinter printer;

    public MetricsLogger() throws IOException {
        Path dirPath = Paths.get(DIRECTORY);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = Paths.get(FILE_PATH);
        boolean fileExists = Files.exists(filePath);

        BufferedWriter writer = Files.newBufferedWriter(filePath, fileExists ?
                java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);

        printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

        if (!fileExists) {
            printer.printRecord("Matrix Size", "Total Time (ms)", "Upload Time (ms)", "Compute Time (ms)",
                    "Network Transfer (MB)", "Transfer Rate (MB/s)", "Memory Before (MB)", "Memory After (MB)",
                    "Memory Used (MB)", "CPU Before (%)", "CPU After (%)", "Num Workers");
        }
    }

    public void log(int matrixSize, long totalTime, long uploadTime, long computeTime,
                    double networkTransferMB, double transferRateMBps,
                    long memBeforeMB, long memAfterMB, long memUsedMB,
                    double cpuBefore, double cpuAfter, int numWorkers) throws IOException {

        printer.printRecord(matrixSize, totalTime, uploadTime, computeTime,
                networkTransferMB, transferRateMBps, memBeforeMB, memAfterMB, memUsedMB,
                cpuBefore, cpuAfter, numWorkers);
        printer.flush();
    }

    public void close() throws IOException {
        printer.close();
    }
}
