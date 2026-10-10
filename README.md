# Bonus Task — BST and Hash Table Analysis

## Implementation and complexity

`BinarySearchTree` is an unbalanced BST: smaller keys go left, larger keys go right. `HashTable` stores keys in bucket chains and doubles its array above a 0.75 load factor. Both support insertion, search, and deletion of distinct integers.

| Structure | Operation | Best | Worst |
|---|---|---:|---:|
| BST | Insert | Θ(1) | Θ(n) |
| BST | Search | Θ(1) | Θ(n) |
| BST | Delete | Θ(1) | Θ(n) |
| Hash Table | Insert | Θ(1) | Θ(n) |
| Hash Table | Search | Θ(1) | Θ(n) |
| Hash Table | Delete | Θ(1) | Θ(n) |

BST operations follow a path in the tree: usually O(log n) for randomly inserted keys, but O(n) if the tree becomes a chain. Hash Table operations are usually O(1); collisions can make a chain length n, and resizing makes one insertion O(n).

## Experiment and results

Both structures received the same shuffled distinct keys. For each size, the program timed all insertions, then successful searches, then deletions. It used JVM warm-up and the median of three measured runs. The values below are total microseconds for each operation batch, not time per element; they can vary by computer and run. Full results: [`results/results.csv`](results/results.csv).

| n | Structure | Insert (µs) | Search (µs) | Delete (µs) |
|---:|---|---:|---:|---:|
| 100 | BST | 2.900 | 2.300 | 2.800 |
| 100 | Hash Table | 10.600 | 0.600 | 0.700 |
| 1,000 | BST | 43.500 | 30.700 | 35.200 |
| 1,000 | Hash Table | 56.800 | 5.100 | 6.700 |
| 10,000 | BST | 601.000 | 489.000 | 590.400 |
| 10,000 | Hash Table | 301.400 | 79.100 | 44.200 |

The Hash Table searched and deleted faster in this run. BST insertion was faster for the two smaller sizes, but Hash Table insertion was faster at 10,000. Hashing, collisions, resizing, memory use, and JVM timing explain why measured speed is not identical to the complexity bounds.
