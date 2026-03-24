package umm.digiquilt.model;

import static junit.framework.Assert.*;

import org.junit.Test;

import umm.digiquilt.model.Fraction;

/**
 * @author deragonmr
 *
 */
public class FractionTest {

    /**
     * Test the getNumerator and getDenominator methods
     */
    @Test
    public void testGets(){
        Fraction testFrac1 = new Fraction(5, 10);
        Fraction testFrac2 = new Fraction(10, 20);
        Fraction testFrac3 = new Fraction(1, 4);

        assertEquals("The expected Numerator should be 5",5,testFrac1.getNumerator());
        assertEquals("The expected Numerator should be 10",10,testFrac2.getNumerator());
        assertEquals("The expected Numerator should be 1",1,testFrac3.getNumerator());
        assertEquals("The expected Numerator should be 10",10,testFrac1.getDenominator());
        assertEquals("The expected Numerator should be 20",20,testFrac2.getDenominator());
        assertEquals("The expected Numerator should be 4",4,testFrac3.getDenominator());
    }

    /**
     * Test that equivalent fractions are equal with equals()
     */
    @Test
    public void testEquals(){
        Fraction oneHalf = new Fraction(1, 2);
        Fraction twoFourths = new Fraction(2, 4);
        Fraction oneTwentyEight = new Fraction (128, 256);

        assertEquals("Equivalent fractions should be equal", 
                oneHalf, twoFourths);
        assertEquals("Equivalent fractions should be equal",
                oneHalf, oneTwentyEight);
        assertEquals("Equivalent fractions should be equal",
                twoFourths, oneTwentyEight);

        assertFalse("equals(null) should be false", oneHalf.equals(null));
        assertFalse("Fractions that are different shouldn't be equal", 
                new Fraction(1, 2).equals(new Fraction(1, 4)));
        
    }

    /**
     * Test the reduce function on an assortment of values
     */
    @Test
    public void testReduce(){
        Fraction oneHalf = new Fraction(128, 256);
        Fraction reduction = oneHalf.reduced();

        int numerator = reduction.getNumerator();
        int denominator = reduction.getDenominator();

        assertEquals("Reduction of 128/256 should be 1/2, was "+reduction, 
                1, numerator);
        assertEquals("Reduction of 128/256 should be 1/2, was "+reduction,
                2, denominator);

        assertEquals("Original should remain unchanged", 
                128, oneHalf.getNumerator());
        assertEquals("Original should remain unchanged", 
                256, oneHalf.getDenominator());
        assertEquals("Hashcode for equal fractions should be the same",
                oneHalf.hashCode(), reduction.hashCode());


        // Should work even with nonstandard denominators:
        Fraction oneThird = new Fraction(30, 90);
        reduction = oneThird.reduced();

        assertEquals("Reduction of 30/90 should be 1/3, was "+reduction,
                1, reduction.getNumerator());
        assertEquals("Reduction of 30/90 should be 1/3, was "+reduction,
                3, reduction.getDenominator());

        Fraction zero = new Fraction(0, 10);
        reduction = zero.reduced();
        assertEquals("Reduction of 0/10 should be 0/1, not "+reduction,
                0, reduction.getNumerator());
        assertEquals("Reduction of 0/10 should be 0/1, not "+reduction,
                1, reduction.getDenominator());


        Fraction nan = new Fraction(10, 0);
        reduction = nan.reduced();
        assertEquals("Reduction of 10/0 should be 1/0, not "+reduction,
                1, reduction.getNumerator());
        assertEquals("Reduction of 1/10 should be 1/0, not "+reduction,
                0, reduction.getDenominator());

        nan = new Fraction(0,0);
        reduction = nan.reduced();
        assertEquals("Reduction of 0/0 should be 0/0, not "+reduction,
                0, reduction.getNumerator());
        assertEquals("Reduction of 0/0 should be 0/0, not "+reduction,
                0, reduction.getDenominator());

    }
    
    /**
     * 
     */
    @Test
    public void testComparable(){
        Fraction quarter = new Fraction(1, 4);
        Fraction third = new Fraction(1, 3);
        Fraction half = new Fraction(1, 2);
        Fraction halfInflated = new Fraction(32, 64);
        Fraction threeQuarters = new Fraction(3, 4);
        Fraction one = new Fraction(1, 1);
                
        
        assertTrue(quarter.compareTo(quarter) == 0);
        assertTrue(quarter.compareTo(third) < 0);
        assertTrue(quarter.compareTo(half) < 0);
        assertTrue(quarter.compareTo(halfInflated) < 0);
        assertTrue(quarter.compareTo(threeQuarters) < 0);
        assertTrue(quarter.compareTo(one) < 0);
        
        assertTrue(third.compareTo(quarter) > 0);
        assertTrue(third.compareTo(third) == 0);
        assertTrue(third.compareTo(half) < 0);
        assertTrue(third.compareTo(halfInflated) < 0);
        assertTrue(third.compareTo(threeQuarters) < 0);
        assertTrue(third.compareTo(one) < 0);
        
        assertTrue(half.compareTo(quarter) > 0);
        assertTrue(half.compareTo(third) > 0);
        assertTrue(half.compareTo(half) == 0);
        assertTrue(half.compareTo(halfInflated) == 0);
        assertTrue(half.compareTo(threeQuarters) < 0);
        assertTrue(half.compareTo(one) < 0);
        
        assertTrue(halfInflated.compareTo(quarter) > 0);
        assertTrue(halfInflated.compareTo(third) > 0);
        assertTrue(halfInflated.compareTo(halfInflated) == 0);
        assertTrue(halfInflated.compareTo(half) == 0);
        assertTrue(halfInflated.compareTo(threeQuarters) < 0);
        assertTrue(halfInflated.compareTo(one) < 0);

        assertTrue(threeQuarters.compareTo(quarter) > 0);
        assertTrue(threeQuarters.compareTo(third) > 0);
        assertTrue(threeQuarters.compareTo(half) > 0);
        assertTrue(threeQuarters.compareTo(halfInflated) > 0);
        assertTrue(threeQuarters.compareTo(threeQuarters) == 0);
        assertTrue(threeQuarters.compareTo(one) < 0);
        
        
        assertTrue(one.compareTo(quarter) > 0);
        assertTrue(one.compareTo(third) > 0);
        assertTrue(one.compareTo(half) > 0);
        assertTrue(one.compareTo(halfInflated) > 0);
        assertTrue(one.compareTo(threeQuarters) > 0);
        assertTrue(one.compareTo(one) == 0);
        
    }

    /**
     * Test incrementing fractions, so that they have the same value
     * but show up with higher numbers (1/2 becomes 2/4, etc.) 
     */
    @Test
    public void testInflated(){
        Fraction oneHalf = new Fraction(1,2);
        Fraction increment = oneHalf.inflated();
        
        assertEquals("1/2 should inflate at first to 2/4, not "+increment,
                2, increment.getNumerator());
        assertEquals("1/2 should inflate at first to 2/4, not "+increment,
                4, increment.getDenominator());
        
        for (int i=0; i<100; i++){
            increment = increment.inflated();
            assertEquals("The two fractions should be equal after inflating",
                    oneHalf, increment);
        }
        
        Fraction highFraction = new Fraction(24, 256);
        increment = highFraction.inflated();
        assertEquals("High fraction should have wrapped around to 3/32", 
                3, increment.getNumerator());
        assertEquals("High fraction should have wrapped around to 3/32", 
                32, increment.getDenominator());
        
    }

}
