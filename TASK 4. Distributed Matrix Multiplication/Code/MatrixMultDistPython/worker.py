import time
import numpy as np
import hazelcast

CLUSTER_MEMBERS = [
    "hazelcast1:5701",
    "hazelcast2:5701",
    "hazelcast3:5701",
    "hazelcast4:5701"
]

def worker_loop(worker_id):
    client = hazelcast.HazelcastClient(cluster_members=CLUSTER_MEMBERS)
    data_map = client.get_map("matrix_blocks").blocking()

    print(f"Worker {worker_id} started and waiting for tasks...")

    while True:
        task_key = f"task_{worker_id}"
        result_key = f"result_{worker_id}"

        task = data_map.get(task_key)
        if task is not None:
            A_block = np.array(task["A_block"])
            B = np.array(task["B"])

            print(f"Worker {worker_id} processing block...")

            result = np.dot(A_block, B)

            data_map.put(result_key, result.tolist())
            data_map.remove(task_key)

            print(f"Worker {worker_id} finished processing and result uploaded.")

        else:
            time.sleep(0.1)

if __name__ == "__main__":
    import os
    worker_id = int(os.environ.get("WORKER_ID", 0))
    worker_loop(worker_id)
