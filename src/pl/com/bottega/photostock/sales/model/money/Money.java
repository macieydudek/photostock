package pl.com.bottega.photostock.sales.model.money;


public interface Money extends Comparable<Money> {

    enum Currency {CREDIT;}

    Currency DEFAULT_CURRENCY = Currency.CREDIT;

    Money ZERO = valueOf(0);

    Money add(Money money);

    Money subtract(Money money);

    Money multiply(long factor);

    default boolean gte(Money money) {
        return compareTo(money) >= 0;
    }

    default boolean gt(Money money) {
        return compareTo(money) > 0;
    }

    default boolean lte(Money money) {
        return compareTo(money) <= 0;
    }

    default boolean lt(Money money) {
        return compareTo(money) < 0;
    }

    Money opposite();

    IntegerMoney convertToInteger();

    RationalMoney convertToRational();

    static Money valueOf(Rational value, Currency currency) {
        return new RationalMoney(value, currency);
    }

    static Money valueOf(long value, Currency currency) {
        return new IntegerMoney(value, currency);
    }

    static Money valueOf(long value) {return new IntegerMoney(value, DEFAULT_CURRENCY);
    }
}
