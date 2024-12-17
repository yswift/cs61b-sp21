package deque;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> adeq = new ArrayDeque<String>();

        assertTrue("A newly initialized ArrayDeque should be empty", adeq.isEmpty());
        adeq.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, adeq.size());
        assertFalse("adeq should now contain 1 item", adeq.isEmpty());

        adeq.addLast("middle");
        assertEquals(2, adeq.size());

        adeq.addLast("back");
        assertEquals(3, adeq.size());

        System.out.println("Printing out deque: ");
        adeq.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> adeq = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("adeq should be empty upon initialization", adeq.isEmpty());

        adeq.addFirst(10);
        // should not be empty
        assertFalse("adeq should contain 1 item", adeq.isEmpty());

        adeq.removeFirst();
        // should be empty
        assertTrue("adeq should be empty after removal", adeq.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> adeq = new ArrayDeque<>();
        adeq.addFirst(3);

        adeq.removeLast();
        adeq.removeFirst();
        adeq.removeLast();
        adeq.removeFirst();

        int size = adeq.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  ad1 = new ArrayDeque<String>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> adeq = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, adeq.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, adeq.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigArrayDequeTest() {
        ArrayDeque<Integer> adeq = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            adeq.addLast(i);
        }

        for (int i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, adeq.removeFirst().intValue());
        }

        for (int i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, adeq.removeLast().intValue());
        }
    }

    @Test
    public void testAddRemove() {
        ArrayDeque<Integer> adeq = new ArrayDeque<Integer>();
        for (int i = 0; i < 8; i++) {
            adeq.addLast(i);
        }
        adeq.addLast(8);
        assertEquals(9, adeq.size());
        assertEquals(0, adeq.removeFirst().intValue());
        assertEquals(8, adeq.size());
        assertEquals(8, adeq.removeLast().intValue());
    }
}
