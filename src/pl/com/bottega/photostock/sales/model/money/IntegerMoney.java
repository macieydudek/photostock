package pl.com.bottega.photostock.sales.model.money;

import java.util.Objects;

class IntegerMoney implements Money {
    private long cents;
    private Currency currency;

    IntegerMoney(long cents, Currency currency) {
        this.cents = cents;
        this.currency = currency;
    }

    @Override
    public Money add(Money money) {
        IntegerMoney integerMoney = safeConvert(money);
        return new IntegerMoney(cents + integerMoney.cents, currency);
    }

    @Override
    public Money subtract(Money money) {
        IntegerMoney integerMoney = safeConvert(money);
        return new IntegerMoney(cents - integerMoney.cents, currency);
    }

    @Override
    public Money multiply(long factor) {
        return new IntegerMoney(cents * factor, currency);
    }

    @Override
    public Money opposite() {
        return new IntegerMoney(-cents, currency);
    }

    @Override
    public IntegerMoney convertToInteger() {
        return this;
    }

    @Override
    public RationalMoney convertToRational() {
        return new RationalMoney(Rational.valueOf(cents, 100), currency);
    }

    @Override
    public int compareTo(Money o) {
        IntegerMoney integerMoney = safeConvert(o);
        if (cents == integerMoney.cents)
            return 0;
        else if (cents < integerMoney.cents)
            return -1;
        else
            return 1;
    }

    private void ensureSameCurrency(IntegerMoney other) {
        if (currency != other.currency) {
            throw new IllegalArgumentException("Currency missmatch");
        }
    }

    private IntegerMoney safeConvert(Money other) {
        IntegerMoney integerMoney = other.convertToInteger();
        ensureSameCurrency(integerMoney);
        return integerMoney;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if ((other == null) || !(other instanceof Money)) return false;

        IntegerMoney o;
        if(other instanceof RationalMoney) {
            RationalMoney otherAsRational = (RationalMoney) other;
            o = otherAsRational.convertToInteger();
        }
        else {
            o = (IntegerMoney) other;
        }

        if(this.currency != o.currency) return false;

        return this.cents == o.cents;

    }

    @Override
    public int hashCode() {
        Long l = cents;
        return l.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%d.%02d %s", cents / 100, + cents%100, currency);
    }
}
