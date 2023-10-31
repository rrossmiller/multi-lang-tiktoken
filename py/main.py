import csv
from time import perf_counter_ns

import numpy as np
import tiktoken

if __name__ == "__main__":
    with open("../input.txt") as fin:
        data = fin.read()

    data = data.split("\n\n")
    print(f"Num samples: {len(data)}")
    print(data[-1])

    # gpt 3.5 turbo
    encoding = tiktoken.encoding_for_model("gpt-3.5-turbo")
    times = []
    all_results = []
    for _ in range(10):
        start = perf_counter_ns()
        results = []
        for d in data:
            r = encoding.encode(d)
            results.append(len(r))

        end = perf_counter_ns() - start
        times.append(end)
        all_results.append(results)

    avg_time = np.mean(times) / 1e9
    print(f"Avg Elapsed: {avg_time} seconds")

    all_results = np.mean(all_results, axis=0).astype(int)
    # write results baseline csv
    with open("baseline.txt", "w") as fout:
        writer = csv.writer(fout)
        writer.writerows(
            [
                [f"Num samples: {len(data)}"],
                [f"Avg: {avg_time} seconds"],
                all_results,
                times,
            ]
        )
