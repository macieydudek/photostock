package pl.com.bottega.photostock.sales.model.purchase;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.product.Product;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class Reservation {

    private Client client;

    private String number;

    private boolean active = true;

    Collection<Product> items;

    public Reservation(Client client) {
        this.client = client;
        this.number = UUID.randomUUID().toString(); //!!!!
        this.items = new LinkedList<>();
    }

    public void add(Product product) {
        if (items.contains(product)) {
            throw new IllegalArgumentException(String.format
                    ("product %s is already in this reservation", product.getNumber()));
        }
        product.ensureAvailable();

        items.add(product);
        product.reserverPer(client);
    }

    public void remove(Product product) {
        if(!items.contains(product)) {
            throw new IllegalArgumentException(String.format
                    ("product %s is now added to this Reservation", product.getNumber()));
        }
        items.remove(product);
        product.unreservedPer(client);
    }

    public Offer generateOffer() {
        Collection<Product> products = getActiveItems();
        if(products.isEmpty()){
            throw new IllegalStateException("No active items in the reservation");
        }
        return new Offer(client, products);
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

    public String getNumber() {
        return number;
    }

    public Client getOwner() {
        return client;
    }

    public boolean isOwnedBy(String clientNumber) {
        return client.getNumber().equals(clientNumber);
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}
