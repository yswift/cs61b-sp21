package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    private StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
    private ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();

    private interface Action {
        void apply();
    }

    private Action[] actions = {
        () -> {
            int i = StdRandom.uniform(0, 5000);
            sad.addFirst(i);
            ads.addFirst(i);
        },
        () -> {
            int i = StdRandom.uniform(0, 5000);
            sad.addLast(i);
            ads.addLast(i);
        },
        () -> {
            if (sad.isEmpty() || ads.isEmpty()) {
                return;
            }
            int i = StdRandom.uniform(0, sad.size());
            Assert.assertEquals("get", ads.get(i), sad.get(i));
        },
        () -> {
            if (sad.isEmpty() || ads.isEmpty()) {
                return;
            }
            Assert.assertEquals("removeFirst", ads.removeFirst(), sad.removeFirst());
        },
        () -> {
            if (sad.isEmpty() || ads.isEmpty()) {
                return;
            }
            Assert.assertEquals("removeLast", ads.removeLast(), sad.removeLast());
        }
    };

    @Test
    public void RandomTest(){
        Assert.assertTrue( sad.isEmpty() );

        int N=5000;
        for(int i = 0; i<N; ++i){
            int option = StdRandom.uniform(0,5);
            actions[option].apply();
        }
        Assert.assertEquals( "size()", ads.size(), sad.size() );
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestArrayDequeEC.class);
    }
}