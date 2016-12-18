package pl.com.bottega.photostock.sales.model;

import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.Collection;
import java.util.LinkedList;

public class Client {

    private String name;
    private Address address; //do adresu dobrze wprowadzić nowa klasę
    private ClientStatus status;
    protected Money balance;

    private Collection <Transaction> transactions;

    public Client(String name, Address address, ClientStatus status, Money balance) {
        this.name = name;
        this.address = address;
        this.status = status;
        this.balance = balance;
        this.transactions = new LinkedList<>();
        if(!balance.equals(Money.ZERO)) {
            this.transactions.add(new Transaction(balance, "Opening account"));
        }

    }

    public Client(String name, Address address, Money balance) {
        this(name, address, ClientStatus.STANDARD, balance);
    }

    public boolean canAfford(Money money) {
        return balance.gte(money);
    }

    public void charge(Money money, String reason) { //przyczyna pobrania pieniędzy
        if(money.lte(Money.ZERO))
            throw new IllegalArgumentException("Negative charge amount prohibited");
        if(canAfford(money)) {
            Transaction chargeTransaction = new Transaction(money.opposite(), reason);
            transactions.add(chargeTransaction);
            balance = balance.subtract(money);
        } else {
            String template = "Client balance is %s and requested amount was %s";
            String message = String.format(template, balance, money);
            throw new CantAffordException(message);
        }
    }

    public void recharge(Money money) {
        if(money.lte(Money.ZERO))
            throw new IllegalArgumentException("Negative charge amount prohibited");
        Transaction transaction = new Transaction(money, "Recharge account");
        transactions.add(transaction);
        balance = balance.add(money);
    }

    public String introduce() {
        String statusName = status.getStatusName();
        return String.format("%s - %s", name, statusName);
    }

    public String getName() {
        return name;
    }
}
