package pl.com.bottega.photostock.sales.model.money;


import pl.com.bottega.photostock.sales.model.Rational;

public interface Money extends Comparable<Money> {

    enum Currency {CREDIT;}

    Currency DEFAULT_CURRENCY = Currency.CREDIT;

    Money ZERO = valueOf(0);

    Money add(Money money);

    Money subtract(Money money);

    Money multiply(long factor);

    boolean gte(Money money);

    boolean lte(Money money);

    boolean gt(Money money);

    boolean lt(Money money);

    Money opposite();

    RationalMoney convertToRational();

    static Money valueOf(Rational value, Currency currency) {
        return new RationalMoney(value, currency);
    }

    static Money valueOf(long value, Currency currency) {
        return new RationalMoney(Rational.valueOf(value), currency);
    }

    static Money valueOf(long value) {
        return new RationalMoney(Rational.valueOf(value), DEFAULT_CURRENCY);
    }
}
