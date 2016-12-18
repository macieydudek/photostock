package pl.com.bottega.photostock.sales.model;

public final class Rational implements Comparable<Rational> {
    public static Rational ZERO = new Rational(0, 1);
    public static Rational ONE = new Rational(1, 1);

    public static Rational valueOf(long numerator, long denominator) {

        if (denominator == 0)
            throw new IllegalArgumentException("Denominator value must not equal zero");

        if (numerator == 0)
            return Rational.ZERO;

        if (numerator == denominator)
            return Rational.ONE;

        boolean isPositive = ((numerator > 0) == (denominator > 0));
        numerator = Math.abs(numerator);
        denominator = Math.abs(denominator);

        double cap = Math.min(numerator, denominator);

        //TODO zaimplementować na liczbach pierwszych
        for (long factor = 2; factor < cap; factor ++) {
            while (numerator % factor == 0 && denominator % factor == 0) {
                numerator /= factor;
                denominator /= factor;
            }
        }

        return isPositive ? new Rational(numerator, denominator) : new Rational(-numerator, denominator);
    }

    public static Rational valueOf(long integer) {
        return valueOf(integer, 1);
    }

    public static Rational valueOf(double value) {
        String stringValue = String.valueOf(value);
        int magnitude = stringValue.length() - stringValue.indexOf(".") - 1;

        long numerator = Math.round(value * Math.pow(10, magnitude));
        long denominator = (long) Math.pow(10, magnitude);

        return valueOf(numerator, denominator);
    }

    public Rational negative() {
        return valueOf(-numerator, denominator);
    }

    public Rational invert() {
        return valueOf(denominator, numerator);
    }

    public Rational add(Rational addend) {
        long newNumerator = numerator * addend.denominator + addend.numerator * denominator;
        long newDenominator = denominator * addend.denominator;
        return valueOf(newNumerator, newDenominator);
    }

    public Rational subtract(Rational subtrahend) {
        return add(subtrahend.negative());
    }

    public Rational multiply(Rational factor) {
        return valueOf(numerator * factor.numerator, denominator * factor.denominator);
    }

    public Rational multiply(long factor) {
        return valueOf(numerator * factor, denominator);
    }

    public Rational divide(Rational divisor) {
        return multiply(divisor.invert());
    }

    public Rational divide(long divisor) {
        return valueOf(numerator, denominator * divisor);
    }

    private final long numerator;
    private final long denominator;

    private Rational(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public double toDouble() {
        return numerator / denominator;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public int compareTo(Rational number) {
        if (numerator == number.numerator && denominator == number.denominator)
            return 0;

        return (numerator * number.denominator) > (number.numerator * denominator) ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rational)) return false;

        Rational number = (Rational) o;

        return (numerator * number.denominator) == (number.numerator * denominator);
    }

    @Override
    public int hashCode() {
        int result = (int) (numerator ^ (numerator >>> 32));
        result = 31 * result + (int) (denominator ^ (denominator >>> 32));
        return result;
    }
}
