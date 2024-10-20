#ifndef MATRIX_MULT_H
#define MATRIX_MULT_H

double** create_matrix(int n);
void free_matrix(int n, double** matrix);
void matrix_mult(int n, double** a, double** b, double** result);
void init_matrix(int n, double** matrix);
void benchmark_matrix_mult(int n, int num_warmups, int num_executions);

#endif // MATRIX_MULT_H
