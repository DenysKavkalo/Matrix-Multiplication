package org.example;

import java.util.HashMap;
import java.util.Map;

public class SparseMatrixMultiplication {
    public static Map<Integer, Map<Integer, Double>> multiply(
            Map<Integer, Map<Integer, Double>> A,
            Map<Integer, Map<Integer, Double>> B) {
        Map<Integer, Map<Integer, Double>> C = new HashMap<>();

        for (int i : A.keySet()) {
            for (int k : A.get(i).keySet()) {
                if (B.containsKey(k)) {
                    for (int j : B.get(k).keySet()) {
                        C.computeIfAbsent(i, x -> new HashMap<>())
                                .merge(j, A.get(i).get(k) * B.get(k).get(j), Double::sum);
                    }
                }
            }
        }
        return C;
    }
}
