cmake_minimum_required(VERSION 3.10)

project(MatrixMultiplicationBenchmark C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED True)

add_executable(matrix_mult_benchmark
        main.c
        matrix_mult.c
        benchmark.c
)

target_include_directories(matrix_mult_benchmark PUBLIC "${PROJECT_SOURCE_DIR}")
