    #include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "matrix_mult.h"

void benchmark_matrix_mult(int n, int num_warmups, int num_executions) {
    double** a = create_matrix(n);
    double** b = create_matrix(n);
    double** result = create_matrix(n);

    init_matrix(n, a);
    init_matrix(n, b);

    size_t matrix_size_bytes = n * n * sizeof(double);
    size_t alloc_bytes_per_execution = 3 * matrix_size_bytes;
    size_t total_allocated_memory = 0;
    size_t free_count = 0;

    for (int warmup = 0; warmup < num_warmups; warmup++) {
        matrix_mult(n, a, b, result);
    }

    double total_time = 0;

    for (int exec = 0; exec < num_executions; exec++) {
        clock_t start = clock();
        matrix_mult(n, a, b, result);
        clock_t end = clock();

        double time_spent = (double)(end - start) * 1000 / CLOCKS_PER_SEC;
        total_time += time_spent;
        total_allocated_memory += alloc_bytes_per_execution;
    }

    double avg_time = total_time / num_executions;
    double alloc_rate = (double)total_allocated_memory / (total_time / 1000) / (1024 * 1024);

    printf("Benchmark,Mode,Threads,Samples,Score,Unit,Param: n\n");
    printf("matrix_mult,avgt,1,%d,%f,ms/op,%d\n", num_executions, avg_time, n);
    printf("matrix_mult_alloc_rate,avgt,1,%d,%f,MB/sec,%d\n", num_executions, alloc_rate, n);
    printf("matrix_mult_alloc_rate_norm,avgt,1,%d,%zu,B/op,%d\n", num_executions, alloc_bytes_per_execution, n);
    printf("matrix_mult_gc_count,avgt,1,%d,%zu,counts,%d\n", num_executions, free_count, n);

    free_matrix(n, a);
    free_matrix(n, b);
    free_matrix(n, result);
    free_count++;
}
