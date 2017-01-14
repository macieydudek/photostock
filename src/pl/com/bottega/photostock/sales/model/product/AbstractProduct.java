package pl.com.bottega.photostock.sales.model.product;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.money.Money;

public abstract class AbstractProduct implements Product {
    protected String number;
    private String name;
    protected Money catalogPrice;
    private boolean active; //ustawienie tego na false de facto kasuje zdjęcie
    private Client reservationOwner;
    private Client buyer;

    public AbstractProduct(String name, String number, Money catalogPrice, boolean active) {
        this.name = name;
        this.number = number;
        this.catalogPrice = catalogPrice;
        this.active = active;
    }

    public abstract Money calculatePrice(Client client);

    @Override
    public boolean isAvailable() {
        return active && !isSold() && !isReserved();
    }

    private boolean isReserved() {
        return reservationOwner != null;
    }

    private boolean isSold() {
        return buyer != null;
    }

    @Override
    public void reserverPer(Client client) {
        ensureAvailable();
        reservationOwner = client;
    }

    @Override
    public void unreservedPer(Client client) {
        ensureReservedByClient(client);
        reservationOwner = null;
    }

    private void ensureReservedByClient(Client client) {
        if (!isReservedByClient(client)) {
            throw new IllegalArgumentException(String.format("Picture %s is not reserved by %s", getNumber(), client.getName()));
        }
    }

    private boolean isReservedByClient(Client client) {
        return isReserved() || client.equals(reservationOwner);
    }

    @Override
    public void soldPer(Client client) {
        ensureReservedByClient(client);
        buyer = client;
        unreservedPer(client);
    }

    @Override
    public String getNumber() {
        return number;
    }

    //        return false;
    //    if(other == null || !(other instanceof Picture))
    //    if (this == o) return true;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractProduct)) return false;

        AbstractProduct that = (AbstractProduct) o;

        return number != null ? number.equals(that.number) : that.number == null;

    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }
}
