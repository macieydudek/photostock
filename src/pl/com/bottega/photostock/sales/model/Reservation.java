package pl.com.bottega.photostock.sales.model;

import java.util.Collection;
import java.util.LinkedList;

public class Reservation {

    private Client client;

    Collection<Product> items;

    public Reservation(Client client) {
        this.client = client;
        this.items = new LinkedList<>();
    }

    public void add(Product product) {
        if (items.contains(product)) {
            throw new IllegalArgumentException(String.format("product %s is already in this reservation", product.getNumber()));
        }
        product.ensureAvailable();

        items.add(product);
    }

    public void remove(Product product) {
        if(!items.contains(product)) {
            throw new IllegalArgumentException(String.format("product %s is now added to this Reservation", product.getNumber()));
        }
        items.remove(product);
    }

    public Offer generateOffer() {
        return new Offer(client, getActiveItems());
    }

    private Collection<Product> getActiveItems() {
        Collection<Product> filteredItems = new LinkedList<>();
        for (Product product : items) {
            if (product.isActive()) {
                filteredItems.add(product);
            }
        }
        return filteredItems;
    }

    public int getItemsCount() {
        return items.size();
    }
}
