package IntList;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FirstDigitEqualsLastDigitTest {
    @Test
    public void test10() {
//        assertTrue(IntListExercises.firstDigitEqualsLastDigit(11));
        assertFalse(IntListExercises.firstDigitEqualsLastDigit(10));
    }
}
