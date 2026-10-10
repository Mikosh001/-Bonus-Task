import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000};
    private static final int MEASURED_RUNS = 3;
    private static final long SEED = 20261010L;

    private Benchmark() { }

    private record Times(long insert, long search, long delete) { }

    public static void main(String[] args) throws IOException {
        for (int run = 0; run < 3; run++) {
            int[] keys = shuffledKeys(10_000, SEED + run);
            time(new BinarySearchTree(), keys);
            time(new HashTable(), keys);
        }
        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(output))) {
            csv.println("n,structure,insert_us,search_us,delete_us");
            System.out.println("n,structure,insert_us,search_us,delete_us");
            for (int n : SIZES) {
                long[][] bst = new long[3][MEASURED_RUNS];
                long[][] hash = new long[3][MEASURED_RUNS];
                for (int run = 0; run <= MEASURED_RUNS; run++) {
                    int[] keys = shuffledKeys(n, SEED + n + run);
                    Times bstTimes;
                    Times hashTimes;
                    if (run % 2 == 0) {
                        bstTimes = time(new BinarySearchTree(), keys);
                        hashTimes = time(new HashTable(), keys);
                    } else {
                        hashTimes = time(new HashTable(), keys);
                        bstTimes = time(new BinarySearchTree(), keys);
                    }
                    if (run > 0) {
                        save(bst, run - 1, bstTimes);
                        save(hash, run - 1, hashTimes);
                    }
                }
                writeRow(csv, n, "BST", bst);
                writeRow(csv, n, "HashTable", hash);
            }
        }
        System.out.println("Saved to " + output);
    }

    private static int[] shuffledKeys(int n, long seed) {
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) keys[i] = Integer.rotateLeft(i * 0x9E3779B9, 13);
        Random random = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = keys[i];
            keys[i] = keys[j];
            keys[j] = temp;
        }
        return keys;
    }

    private static Times time(IntSet set, int[] keys) {
        long start = System.nanoTime();
        for (int key : keys) {
            if (!set.insert(key)) throw new IllegalStateException("Duplicate insertion");
        }
        long afterInsert = System.nanoTime();
        for (int key : keys) {
            if (!set.contains(key)) throw new IllegalStateException("Missing key");
        }
        long afterSearch = System.nanoTime();
        for (int key : keys) {
            if (!set.delete(key)) throw new IllegalStateException("Deletion failed");
        }
        long afterDelete = System.nanoTime();
        if (set.size() != 0) throw new IllegalStateException("Structure is not empty");
        return new Times(afterInsert - start, afterSearch - afterInsert, afterDelete - afterSearch);
    }

    private static void save(long[][] samples, int index, Times times) {
        samples[0][index] = times.insert();
        samples[1][index] = times.search();
        samples[2][index] = times.delete();
    }

    private static void writeRow(PrintWriter csv, int n, String name, long[][] samples) {
        String row = String.format(Locale.US, "%d,%s,%.3f,%.3f,%.3f", n, name,
                median(samples[0]) / 1_000.0,
                median(samples[1]) / 1_000.0,
                median(samples[2]) / 1_000.0);
        csv.println(row);
        System.out.println(row);
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }
}
