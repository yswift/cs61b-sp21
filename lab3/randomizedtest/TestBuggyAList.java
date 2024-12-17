package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        L.addLast(5);
        B.addLast(5);
        L.addLast(10);
        B.addLast(10);
        L.addLast(15);
        B.addLast(15);
        assertEquals(L.size(), B.size());
        assertEquals(L.removeLast(), B.removeLast());
        assertEquals(L.removeLast(), B.removeLast());
        assertEquals(L.removeLast(), B.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
//                System.out.println("size: " + size);
                assertEquals(size, B.size());
            } else if (operationNumber == 2 && L.size() > 0) {
                Integer val = L.getLast();
//                System.out.println("getLast: " + val);
                assertEquals(val, B.getLast());
                val = L.removeLast();
                assertEquals(val, B.removeLast());
//                System.out.println("removeLast: " + val);
            }
        }
    }
}
