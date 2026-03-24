/**
 * 
 */
package umm.digiquilt.model;

/**
 * @author Michael Deragon
 *
 * This is the object to keep track of the numerator and denomonator 
 * of a fraction.
 * 
 * also holds methods to increase and decrease the numerator, and
 * return both the "raw" and simplified fractions.
 */

public class Fraction implements Comparable<Fraction>{

    /**
     * A list of denominators that should be used when incrementing. This is
     * so that if you have something like 1/2, you don't have to increment 
     * through every single even number that there is. These are the ones
     * that make the most sense for blocks of size 4, 9, and 16.
     */
    static final int[] GOOD_DENOMS = new int[]{
        2, 3, 4, 6, 8, 9, 12, 16, 18, 24, 32, 
        36, 48, 64, 72, 100, 128, 144, 256
    };

    /**
     * The numerator of this Fraction.
     */
    private final int numerator;
    /**
     * The denominator of this Fraction.
     */
    private final int denominator;

    /**
     * Create a fraction with the specified numerator and denominator.
     * @param num
     * @param denum
     */
    public Fraction(int num, int denum){
        numerator = num;
        denominator = denum;
    }

    /**
     * Method to get the raw unsimplified numerator of 
     * the fraction. 
     * 
     * @return the unsimplified numerator.
     */	
    public int getNumerator(){
        return numerator;
    }

    /**
     * Method to get the unsimplified denomonator
     * of the fraction.
     * 
     * @return the unsimplified denomonator.
     */
    public int getDenominator(){
        return denominator;
    }

    /**
     * Create a Fraction which is equal to this one, but the numerator and
     * denominator are fully reduced.
     * 
     * @return a reduced version of this Fraction
     */
    public Fraction reduced(){
        int gcd = getGCD(numerator, denominator);
        if (gcd == 0){
            // This fraction is undefined, good luck to whoever's
            // using us, I guess
            return new Fraction(0,0);
        }
        return new Fraction(numerator/gcd, denominator/gcd);
    }

    /**
     * Return a fraction which is equivalent to this one, but has a larger
     * numerator and denominator. The resulting fraction should be the same
     * as this one when both are reduced(). The amount to increase both
     * numbers depends on an internal, hard-coded list of denominators that
     * make sense for DigiQuilt's purposes. 
     * 
     * If this fraction is already as inflated as it can be, this will simply
     * return a reduced() version of this fraction.
     * 
     * @return an equivalent fraction with bigger numbers (assuming they're
     * not too big already)
     */
    public Fraction inflated(){
        // Find a starting point in the array of denominators
        int i = 0;
        while ( i<GOOD_DENOMS.length && denominator >= GOOD_DENOMS[i]){
            i++;
        }
        // Okay, now i should be at the first denominator which is larger than
        // ours. Now we want to find the lowest one we can use after that.
        for (int j=i; j<GOOD_DENOMS.length; j++){
            //We want numerator/denominator == n/GOOD_DENOMS[j] where n is
            //an integer. Solving for n gives us:
            if ((numerator*GOOD_DENOMS[j]) % denominator == 0){
                // Bingo, this is what we want.
                return new Fraction(
                        numerator*GOOD_DENOMS[j] / denominator,
                        GOOD_DENOMS[j]);
            }
            
        }
        // We ran out of denominators. Time to wrap around to the smallest
        // instead...
        return reduced();
    }

    @Override
    public boolean equals(Object other){
        // Can only equal Fractions
        if (!(other instanceof Fraction)){
            return false;
        }
        Fraction otherF = (Fraction) other;

        if (otherF.numerator == this.numerator 
                && otherF.denominator == this.denominator){
            // Fraction with exact same values
            return true;
        }

        // Check to see if we're equivalent
        Fraction ourReduced = this.reduced();
        Fraction theirReduced = otherF.reduced();

        if (ourReduced.numerator == theirReduced.numerator
                && ourReduced.denominator == theirReduced.denominator){
            return true;
        }
        // Doesn't look like we're equal at all.
        return false;
    }

    @Override
    public int hashCode(){
        Fraction myReduced = this.reduced();
        return myReduced.numerator + myReduced.denominator;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Fraction o) {
        return (numerator*o.denominator) - (o.numerator*denominator);
    }

    @Override
    public String toString(){
        return numerator + "/" + denominator;
    }


    /**
     * Get the greatest common divisor of these two numbers using Euclid's
     * algorithm.
     * 
     * @param a 
     * @param b 
     * @return the GCD of the two numbers.
     * 
     */
    public static int getGCD(int a, int b){
        int temp;
        int larger = Math.max(a, b);
        int smaller = Math.min(a, b);
        while (smaller != 0) {
            temp = smaller;
            smaller = larger % smaller;
            larger = temp;
        }
        return larger;      
    }



}
