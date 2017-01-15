package pl.com.bottega.photostock.sales.model.client;

import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.Collection;
import java.util.LinkedList;

public class Client {

    private String name;
    private String number;
    private Address address; //do adresu dobrze wprowadzić nowa klasę
    private ClientStatus status;
    protected Money balance;
    private Collection<Transaction> transanctions;
    private boolean active;

    protected static int id;

    private Collection <Transaction> transactions;
    //konstruktor do wyciągania z repozytorium
    public Client(String number, String name, Address address, ClientStatus status, Money balance, boolean active, Collection<Transaction> transanctions) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.status = status;
        this.balance = balance;
        this.transactions = new LinkedList<>(transanctions);
        this.active = active;
        }
    //konstruktor do tworzenia nowego klienta
    public Client(String name, Address address, ClientStatus status, Money initialBalance) {
        this(nextNumber(), name, address, status, initialBalance, true, new LinkedList<>());
        if (!initialBalance.equals(Money.ZERO)) {
            this.transactions.add(new Transaction(initialBalance, "Opening account"));
        }
    }


    private static String nextNumber() {
        id += 100;
        return String.valueOf(id);
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

    public String getNumber() {return number;}

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }


    public ClientStatus getStatus() {
        return status;
    }

    public Money getBalance() {
        return balance;
    }

    public Collection<Transaction> getTransactions() {
        return new LinkedList<>(transactions);
    }
}
