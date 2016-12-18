package pl.com.bottega.photostock.sales.model;

import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.*;

public class Offer {

    List<Product> items;
    Client client;

    public Offer(Client client, Collection<Product> items) {
        this.client = client;
        this.items = new LinkedList<Product>(items);
        sortProductByPriceDesc();
    }

    public boolean sameAs(Offer other, Money money) { //kwota tolerancji, jeśli jest mniej to jest OK.
        return false;
    }

    public int getItemsCount() {
        return items.size();
    }

    public Money getTotalcost() {
        Money totalCost = Money.ZERO;
        for (Product product : items) {
            Money ProductCost = product.calculatePrice(client);
            totalCost = totalCost.add(ProductCost);
        }
        return totalCost;
    }

    private void sortProductByPriceDesc() {
        this.items.sort(new Comparator<Product>() {
            @Override
            public int compare(Product product1, Product product2) {
                Money price1 = product1.calculatePrice(client);
                Money price2 = product2.calculatePrice(client);
                return price2.compareTo(price1); //sortowanie malejace, zamieniamy kolejność
                //return -price1.compareTo(price2); // to jest to samo.
            }
        });
    }

}
