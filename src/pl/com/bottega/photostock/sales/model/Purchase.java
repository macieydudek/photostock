package pl.com.bottega.photostock.sales.model;

import java.util.*;

public class Purchase {

    private Client client;

    private Date purchaseDate = new Date();

    private List<Product> items;

    private String number;

    public Purchase(Client client, Collection<Product> items) {
        this.client = client;
        this.items = new LinkedList<Product>(items);
        this.number = UUID.randomUUID().toString();
        sortProductByNumberAsc();
        markProductsAsSold();
    }

    private void markProductsAsSold() {
        for(Product product : items) {
            product.soldPer(client);
        }
    }

    private void sortProductByNumberAsc() {
        this.items.sort(new Comparator<Product>() {
            @Override
            public int compare(Product pic1, Product pic2) {
                String string1 = pic1.getNumber();
                String string2 = pic2.getNumber();
                return string1.compareTo(string2);
            }
        });
    }

    public Purchase(Client client, Product... products) {
        this(client, Arrays.asList(products));
    }

    public int getItemsCount() {
        return items.size();
    }

    public String getNumber() {
        return number;
    }
}
