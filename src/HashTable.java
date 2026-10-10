public final class HashTable implements IntSet {
    private static final double MAX_LOAD = 0.75;
    private Node[] buckets = new Node[16];
    private int size;

    private static final class Node {
        final int key;
        Node next;

        Node(int key, Node next) {
            this.key = key;
            this.next = next;
        }
    }

    private int index(int key) {
        int hash = key ^ (key >>> 16);
        return hash & (buckets.length - 1);
    }

    @Override
    public boolean insert(int key) {
        if (contains(key)) return false;
        if (size + 1 > buckets.length * MAX_LOAD) resize();
        int index = index(key);
        buckets[index] = new Node(key, buckets[index]);
        size++;
        return true;
    }

    @Override
    public boolean contains(int key) {
        for (Node node = buckets[index(key)]; node != null; node = node.next) {
            if (node.key == key) return true;
        }
        return false;
    }

    @Override
    public boolean delete(int key) {
        int index = index(key);
        Node previous = null;
        Node current = buckets[index];
        while (current != null) {
            if (current.key == key) {
                if (previous == null) buckets[index] = current.next;
                else previous.next = current.next;
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    private void resize() {
        Node[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        for (Node head : oldBuckets) {
            while (head != null) {
                Node next = head.next;
                int index = index(head.key);
                head.next = buckets[index];
                buckets[index] = head;
                head = next;
            }
        }
    }

    @Override
    public int size() {
        return size;
    }
}
