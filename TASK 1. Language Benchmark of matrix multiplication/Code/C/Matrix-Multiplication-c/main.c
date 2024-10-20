#include <stdio.h>
#include "matrix_mult.h"

int main() {
    int sizes[] = {10, 100, 1000};
    int num_warmups = 3;
    int num_executions = 5;

    for (int i = 0; i < 3; i++) {
        int n = sizes[i];
        printf("Benchmarking for matrix size: %dx%d\n", n, n);
        benchmark_matrix_mult(n, num_warmups, num_executions);
    }

    return 0;
}
