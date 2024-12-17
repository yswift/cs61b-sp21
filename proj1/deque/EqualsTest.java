package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EqualsTest {
    @Test
    public void testEquals() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();

        initDeque(a);
        initDeque(b);
        a.printDeque();
        b.printDeque();
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    public void testIterator() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();

        initDeque(a);
        initDeque(b);
        for (int i : a) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i : b) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private void initDeque(Deque<Integer> deque) {
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addLast(3);
    }
}
