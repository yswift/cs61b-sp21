package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int font;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        font = 0;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        font = (font - 1 + items.length) % items.length;
        items[font] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[(font + size) % items.length] = item;
        size++;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = items[(font + i) % items.length];
        }
        items = a;
        font = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = items[(font) % items.length];
        items[(font) % items.length] = null;
        font = (font + 1) % items.length;
        size--;
        if (size > 0 && size == items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T item = items[(font + size - 1) % items.length];
        items[(font + size - 1) % items.length] = null;
        size--;
        if (size > 0 && size == items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(font + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size != other.size()) {
            return false;
        }
        Iterator<T> it = iterator();
        Iterator<T> otherIt = null;
        if (o instanceof ArrayDeque) {
            otherIt = ((ArrayDeque<T>) o).iterator();
        } else if (o instanceof LinkedListDeque) {
            otherIt = ((LinkedListDeque<T>) o).iterator();
        }
        if (otherIt == null) {
            return false;
        }
        while (it.hasNext()) {
            if (!it.next().equals(otherIt.next())) {
                return false;
            }
        }
        return true;
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        private ArrayDequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T item = get(index);
            index++;
            return item;
        }
    }
}
