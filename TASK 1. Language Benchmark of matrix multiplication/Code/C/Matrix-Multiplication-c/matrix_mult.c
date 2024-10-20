#include "matrix_mult.h"
#include <stdlib.h>
#include <stdio.h>

double** create_matrix(int n) {
    double** matrix = (double**) malloc(n * sizeof(double*));
    for (int i = 0; i < n; i++) {
        matrix[i] = (double*) malloc(n * sizeof(double));
    }
    return matrix;
}

void free_matrix(int n, double** matrix) {
    for (int i = 0; i < n; i++) {
        free(matrix[i]);
    }
    free(matrix);
}

void matrix_mult(int n, double** a, double** b, double** result) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            result[i][j] = 0.0;
            for (int k = 0; k < n; k++) {
                result[i][j] += a[i][k] * b[k][j];
            }
        }
    }
}

void init_matrix(int n, double** matrix) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            matrix[i][j] = rand() % 10;
        }
    }
}
