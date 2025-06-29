import numpy as np
import time
import psutil
import csv
import hazelcast

NUM_WORKERS = 4
CLUSTER_MEMBERS = [
    "localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704"
]

def clear_maps(client):
    m = client.get_map("matrix_blocks").blocking()
    m.clear()

def upload_tasks(data_map, A_blocks, B):
    for i, block in enumerate(A_blocks):
        data_map.put(f"task_{i}", {"A_block": block.tolist(), "B": B.tolist()})

def wait_for_results(data_map, num_workers):
    results = []
    for i in range(num_workers):
        while True:
            res = data_map.get(f"result_{i}")
            if res is not None:
                results.append(np.array(res))
                data_map.remove(f"result_{i}")
                break
            time.sleep(0.1)
    return results

def benchmark_matrix(size):
    process = psutil.Process()
    mem_before = process.memory_info().rss / (1024 * 1024)
    cpu_before = psutil.cpu_percent(interval=None)

    A = np.random.rand(size, size)
    B = np.random.rand(size, size)

    A_blocks = np.array_split(A, NUM_WORKERS)

    client = hazelcast.HazelcastClient(cluster_members=CLUSTER_MEMBERS)
    data_map = client.get_map("matrix_blocks").blocking()
    clear_maps(client)

    start_upload = time.perf_counter()
    upload_tasks(data_map, A_blocks, B)
    end_upload = time.perf_counter()

    upload_time = end_upload - start_upload

    data_transferred_MB = (A.nbytes + B.nbytes) / (1024 * 1024)
    transfer_rate_MB_per_s = data_transferred_MB / upload_time if upload_time > 0 else float('inf')

    print(f"Uploaded tasks for size {size}x{size} in {upload_time:.2f}s.")

    start_compute = time.perf_counter()
    results = wait_for_results(data_map, NUM_WORKERS)
    end_compute = time.perf_counter()

    compute_time = end_compute - start_compute

    C = np.vstack(results)

    mem_after = process.memory_info().rss / (1024 * 1024)
    cpu_after = psutil.cpu_percent(interval=None)

    client.shutdown()

    total_time = upload_time + compute_time

    print(f"Completed distributed multiplication for size {size}x{size} in {total_time:.2f}s.")

    return {
        "Matrix Size": f"{size}x{size}",
        "Total Time (ms)": total_time * 1000,
        "Upload Time (ms)": upload_time * 1000,
        "Compute Time (ms)": compute_time * 1000,
        "Network Transfer (MB)": data_transferred_MB,
        "Transfer Rate (MB/s)": transfer_rate_MB_per_s,
        "Memory Before (MB)": mem_before,
        "Memory After (MB)": mem_after,
        "Memory Used (MB)": mem_after - mem_before,
        "CPU Before (%)": cpu_before,
        "CPU After (%)": cpu_after,
        "Num Workers": NUM_WORKERS
    }

def save_to_csv(filename, data, header):
    file_exists = False
    try:
        with open(filename, 'r'):
            file_exists = True
    except FileNotFoundError:
        pass

    with open(filename, 'a', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=header)
        if not file_exists:
            writer.writeheader()
        writer.writerow(data)

def main():
    sizes = [10, 100, 500, 1000, 3000]
    csv_file = "benchmark_distributed.csv"
    header = [
        "Matrix Size", "Total Time (ms)", "Upload Time (ms)", "Compute Time (ms)",
        "Network Transfer (MB)", "Transfer Rate (MB/s)", "Memory Before (MB)", "Memory After (MB)",
        "Memory Used (MB)", "CPU Before (%)", "CPU After (%)", "Num Workers"
    ]

    for size in sizes:
        print(f"\n--- Benchmarking matrix size {size}x{size} ---")
        results = benchmark_matrix(size)
        save_to_csv(csv_file, results, header)
        print(f"Saved results for size {size}x{size}.")

if __name__ == "__main__":
    main()
