package com.example.benchmark;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class Utils {

    private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public static long getUsedMemoryMB() {
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return usedBytes / (1024 * 1024);
    }

    public static double getProcessCpuLoadPercent() {
        double load = osBean.getProcessCpuLoad();
        return load < 0 ? 0.0 : load * 100;
    }
    public static double[][] generateRandomMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Math.random() * 10;
            }
        }
        return matrix;
    }

}
