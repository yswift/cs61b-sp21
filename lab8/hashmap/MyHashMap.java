package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author yswift
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }

//        public final int hashCode() {
//            return Objects.hashCode(key);
//        }
//
//        public final boolean equals(Object o) {
//            if (o == this)
//                return true;
//            if (o == null)
//                return false;
//            if (o.getClass() != this.getClass())
//                return false;
//            Node n = (Node) o;
//            return Objects.equals(key, n.key);
//        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private Set<K> keySet;
    private int capacity;
    private final double loadFactor;
    private int size;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        capacity = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private int getIndex(K key) {
        int h;
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        return (capacity - 1) & hash;
    }

    @Override
    public void clear() {
        if (size == 0) {
            return;
        }
        for (int i = 0; i < capacity; i++) {
            buckets[i].clear();
        }
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        Node n = getNode(key);
        return n != null;
//        int idx = getIndex(key);
//        Node n = createNode(key, null);
//        return buckets[idx].contains(n);
    }

    @Override
    public V get(K key) {
        Node n = getNode(key);
        return n == null ? null : n.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int idx = getIndex(key);
        Node n = getNode(key);
        if (n != null) {
            n.value = value;
        } else {
            n = createNode(key, value);
            buckets[idx].add(n);
            size++;
        }
        reSize();
    }

    @Override
    public Set<K> keySet() {
        return keySet == null ? (keySet = new KeySet()) : keySet;
    }

    @Override
    public V remove(K key) {
        int idx = getIndex(key);
        Node n = getNode(key);
        if (n != null) {
            buckets[idx].remove(n);
            size--;
            return n.value;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int idx = getIndex(key);
        Node n = getNode(key);
        if (n != null && n.value.equals(value)) {
            buckets[idx].remove(n);
            size--;
            return n.value;
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new KeyIterator();
    }

    private Node getNode(K key) {
        int idx = getIndex(key);
        for (Node n : buckets[idx]) {
            if (n.key.equals(key)) {
                return n;
            }
        }
        return null;
    }

    private void reSize() {
        if (size > capacity * loadFactor) {
            Collection<Node>[] oldBuckets = buckets;
            capacity *= 2;
            buckets = createTable(capacity);
            for (Collection<Node> bucket : oldBuckets) {
                for (Node n : bucket) {
                    int idx = getIndex(n.key);
                    buckets[idx].add(n);
                }
            }
        }
    }

    private class KeySet extends AbstractSet<K> {
        public int size() {
            return size;
        }

        public void clear() {
            MyHashMap.this.clear();
        }

        public Iterator<K> iterator() {
            return new KeyIterator();
        }
    }

    private class KeyIterator implements Iterator<K> {
        Iterator<Node> bucketIter;
        int currentBucket;

        public KeyIterator() {
            currentBucket = 0;
            bucketIter = buckets[currentBucket].iterator();
        }

        @Override
        public boolean hasNext() {
            if (bucketIter.hasNext()) {
                return true;
            }
            currentBucket++;
            while (currentBucket < buckets.length && buckets[currentBucket].isEmpty()) {
                currentBucket++;
            }
            if (currentBucket < buckets.length) {
                bucketIter = buckets[currentBucket].iterator();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public K next() {
            return bucketIter.next().key;
        }

    }

}
