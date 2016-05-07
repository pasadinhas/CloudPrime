import java.math.BigInteger;
import java.util.ArrayList;

public class IntFactorization {

    private BigInteger zero = new BigInteger("0");
    private BigInteger one = new BigInteger("1");
    private BigInteger divisor = new BigInteger("2");
    private ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
    private BigInteger initialArg = null;

    public IntFactorization (BigInteger num) {
        initialArg = num;
    }

    ArrayList<BigInteger> calcPrimeFactors() {
        return calcPrimeFactors(initialArg);
    }

    ArrayList<BigInteger>  calcPrimeFactors(BigInteger num) {

        if (num.compareTo(one)==0) {
            MyICount.registerInput(initialArg);
            return factors;
        }

        while(num.remainder(divisor).compareTo(zero)!=0) {
            divisor = divisor.add(one);
        }

        factors.add(divisor);
        return calcPrimeFactors(num.divide(divisor));
    }
}

