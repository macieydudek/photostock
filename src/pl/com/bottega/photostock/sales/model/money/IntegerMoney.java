package pl.com.bottega.photostock.sales.model.money;

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
        if(cents == integerMoney.cents)
            return 0;
        else if(cents < integerMoney.cents)
            return -1;
        else
            return 1;
    }

    private void ensureSameCurrency(IntegerMoney other) {
        if(currency != other.currency) {
            throw new IllegalArgumentException("Currency missmatch");
        }
    }

    private IntegerMoney safeConvert(Money other) {
        IntegerMoney integerMoney = other.convertToInteger();
        ensureSameCurrency(integerMoney);
        return integerMoney;
    }

    //napisać equalsa i hashcode
}
