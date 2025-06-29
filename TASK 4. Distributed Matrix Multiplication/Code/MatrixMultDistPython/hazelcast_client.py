import numpy as np
import time
from multiprocessing import Process

def upload_worker(block_key, block_data, map_name):
    import hazelcast
    client = hazelcast.HazelcastClient(cluster_members=[
        "localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704"
    ])
    my_map = client.get_map(map_name).blocking()
    my_map.put(block_key, block_data.tolist())
    client.shutdown()

def parallel_upload(A, B, num_workers):
    processes = []
    map_name = "matrix_blocks"

    for i in range(num_workers):
        block_key = f"A_{i}"
        block_data = A[i]
        p = Process(target=upload_worker, args=(block_key, block_data, map_name))
        p.start()
        processes.append(p)

    p = Process(target=upload_worker, args=("B", B, map_name))
    p.start()
    processes.append(p)

    for p in processes:
        p.join()

def multiply_distributed(A, B, num_workers):
    import psutil
    import hazelcast

    process = psutil.Process()
    mem_before = process.memory_info().rss

    start_upload = time.perf_counter()
    parallel_upload(A, B, num_workers)
    end_upload = time.perf_counter()
    upload_time = end_upload - start_upload

    data_transferred_MB = (A.nbytes + B.nbytes) / (1024 * 1024)
    transfer_rate_MB_per_s = data_transferred_MB / upload_time if upload_time > 0 else float('inf')

    client = hazelcast.HazelcastClient(cluster_members=[
        "localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704"
    ])
    my_map = client.get_map("matrix_blocks").blocking()

    start_compute = time.perf_counter()
    results = []
    for i in range(num_workers):
        a_block = np.array(my_map.get(f"A_{i}"))
        b_matrix = np.array(my_map.get("B"))
        result_row = np.dot(a_block, b_matrix)
        results.append(result_row)
    end_compute = time.perf_counter()
    compute_time = end_compute - start_compute

    C = np.array(results)
    client.shutdown()

    mem_after = process.memory_info().rss
    mem_used_MB = (mem_after - mem_before) / (1024 * 1024)

    return {
        "C": C,
        "execution_time_ms": compute_time * 1000,
        "upload_time_ms": upload_time * 1000,
        "compute_time_ms": compute_time * 1000,
        "network_transfer_MB": data_transferred_MB,
        "transfer_rate_MB_per_s": transfer_rate_MB_per_s,
        "memory_used_MB": mem_used_MB,
        "num_workers": num_workers
    }
