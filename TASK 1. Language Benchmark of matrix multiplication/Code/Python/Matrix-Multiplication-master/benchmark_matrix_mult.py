import numpy as np
import tracemalloc
import csv
import os
import pytest
import time


def create_matrix(n):
    """Creates an n x n matrix with random values."""
    return np.random.rand(n, n)


def matrix_multiply(matrix_a, matrix_b):
    """
    Multiplies two matrices using the matrix multiplication method.

    This method has a time complexity of O(n^3), where n is the size of the matrix.
    """
    return np.dot(matrix_a, matrix_b)


def save_results_csv(n, current, peak, memory_rate, exec_time_per_op):
    """Saves memory results to a CSV file."""
    filename = "python_results.csv"
    file_exists = os.path.isfile(filename)

    with open(filename, mode='a', newline='') as file:
        writer = csv.writer(file)
        if not file_exists:
            writer.writerow(["Matrix Size", "Current Memory (MB)", "Peak Memory (MB)", "Memory Allocation Rate (MB/s)",
                             "Execution Time (ms/op)"])
        writer.writerow([n, current / 10 ** 6, peak / 10 ** 6, memory_rate, exec_time_per_op])


@pytest.mark.parametrize("n", [10, 100, 1000])
def test_matrix_multiply(benchmark, n):
    """Performance test for matrix multiplication."""
    for _ in range(3):
        matrix_a = create_matrix(n)
        matrix_b = create_matrix(n)
        matrix_multiply(matrix_a, matrix_b)

    tracemalloc.start()

    start_time = time.time()
    result = benchmark(matrix_multiply, create_matrix(n), create_matrix(n))
    end_time = time.time()

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    execution_time = end_time - start_time

    memory_rate = (peak - current) / execution_time / 10 ** 6
    exec_time_per_op = (execution_time * 1000) / (n * n)

    save_results_csv(n, current, peak, memory_rate, exec_time_per_op)
