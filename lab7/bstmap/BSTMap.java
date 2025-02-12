package bstmap;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private static class Node<K extends Comparable<K>, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size = 0;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = getNode(root, key);
        return node == null ? null : node.value;
    }

    private Node<K, V> getNode(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return getNode(node.left, key);
        } else if (cmp > 0) {
            return getNode(node.right, key);
        } else {
            return node;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new java.util.HashSet<>();
        keySet(root, keySet);
        return keySet;
    }

    private void keySet(Node<K, V> node, Set<K> keySet) {
        if (node == null) {
            return;
        }
        keySet(node.left, keySet);
        keySet.add(node.key);
        keySet(node.right, keySet);
    }

    @Override
    public V remove(K key) {
        // 找到要删除的节点
        Node<K, V> removedNode = getNode(root, key);
        if (removedNode == null) {
            return null;
        }
        root = remove(root, key);
        size--;
        return removedNode.value;
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            // 如果节点有两个子节点，则找到右子树中的最小节点替换当前节点
            Node<K, V> temp = node;
            node = min(temp.right);
            node.right = removeMin(temp.right);
            node.left = temp.left;
        }
        return node;
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeMin(node.left);
        return node;
    }

    private Node<K, V> min(Node<K, V> node) {
        if (node.left == null) {
            return node;
        }
        return min(node.left);
    }

    @Override
    public V remove(K key, V value) {
        Node<K, V> removedNode = getNode(root, key);
        if (removedNode == null || !removedNode.value.equals(value)) {
            return null;
        }
        root = remove(root, key);
        size--;
        return removedNode.value;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {
        private Stack<Node<K, V>> stack = new Stack<>();

        public BSTMapIterator() {
            pushLeft(root);
        }

        private void pushLeft(Node<K, V> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node<K, V> node = stack.pop();
            pushLeft(node.right);
            return node.key;
        }
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node<K, V> node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key + " " + node.value);
        printInOrder(node.right);
    }
}
