package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EqualsTest {
    @Test
    public void testArrayDequeEquals() {
        Deque<Integer> a = new ArrayDeque<>();
        Deque<Integer> b = new ArrayDeque<>();

        initDeque(a);
        initDeque(b);
        a.printDeque();
        b.printDeque();
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    public void testMaxArrayDequeEquals() {
        Comparator<Integer> c = (a, b) -> a - b;
        Deque<Integer> a = new ArrayDeque<>();
        MaxArrayDeque<Integer> b = new MaxArrayDeque<>(c);

        initDeque(a);
        initDeque(b);
        a.printDeque();
        b.printDeque();
        System.out.println(b.max());
        System.out.println(b.max((a1, b1) -> b1 - a1));
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    public void testArrayDequeEqualsLinkedListDeque() {
        Deque<Integer> a = new ArrayDeque<>();
        Deque<Integer> b = new LinkedListDeque<>();

        initDeque(a);
        initDeque(b);
        a.printDeque();
        b.printDeque();
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    public void testLinkedListDequeEquals() {
        Deque<Integer> a = new LinkedListDeque<>();
        Deque<Integer> b = new LinkedListDeque<>();

        initDeque(a);
        initDeque(b);
        a.printDeque();
        b.printDeque();
        assertEquals(a, b);
        assertEquals(b, a);
    }

    private void initDeque(Deque<Integer> deque) {
        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < 10; i++) {
            deque.addFirst(i);
        }
        for (int i = 0; i < 2; i++) {
            deque.removeFirst();
        }
        for (int i = 0; i < 3; i++) {
            deque.removeLast();
        }
    }
}
