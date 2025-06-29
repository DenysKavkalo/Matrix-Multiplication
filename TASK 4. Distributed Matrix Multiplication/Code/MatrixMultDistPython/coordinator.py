import hazelcast
import numpy as np
import time

def distribute_and_collect(A, B, num_workers=4):
    client = hazelcast.HazelcastClient(cluster_members=[
        "localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704"
    ])
    data_map = client.get_map("matrix_blocks").blocking()

    blocks = np.array_split(A, num_workers)

    print(f"Uploading data to Hazelcast...")
    for i, block in enumerate(blocks):
        data_map.put(f"A_{i}", block.tolist())
    data_map.put("B", B.tolist())

    print("Waiting for results from workers...")
    results = []
    for i in range(num_workers):
        while True:
            res = data_map.get(f"result_{i}")
            if res is not None:
                results.append(np.array(res))
                break
            time.sleep(0.5)

    client.shutdown()

    C = np.vstack(results)
    return C

if __name__ == "__main__":
    size = 1000
    num_workers = 4

    print(f"Generating random matrices of size {size}x{size}...")
    A = np.random.rand(size, size)
    B = np.random.rand(size, size)

    start = time.perf_counter()
    C = distribute_and_collect(A, B, num_workers)
    end = time.perf_counter()

    print(f"Distributed multiplication took {(end - start):.2f} seconds.")
    print(f"Result shape: {C.shape}")
