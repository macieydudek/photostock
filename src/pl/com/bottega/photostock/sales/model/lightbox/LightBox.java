package pl.com.bottega.photostock.sales.model.lightbox;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.product.Product;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class LightBox implements Iterable<Product> {


    private Client client;
    private String name;
    private Collection<Product> items = new LinkedList<>();


    private static int lightBoxID = 0;

    public LightBox(Client client, String name) {
        this.name = name;
        this.client = client;
    }

    public void add(Product product) {
        if (items.contains(product)) {
            throw new IllegalArgumentException(String.format("product %s is already in this Lightbox", product.getNumber()));
        }
        product.ensureAvailable();
        items.add(product);
    }

    public void remove(Product product) {
        if (!items.contains(product)) {
            throw new IllegalArgumentException(String.format("This Lightbox does not contain product %s", product.getNumber()));
        }
        items.remove(product);
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public Client getOwner() {
        return client;
    }

    @Override
    public Iterator<Product> iterator() {
        return items.iterator();
    }

    public static LightBox joined(Client client, String name, LightBox... lightboxes) {
        LightBox output = new LightBox(client, name);
        output.join(lightboxes);

        return output;
    }

    private void join(LightBox[] lightboxes) {
        for (LightBox lb : lightboxes) {
            for (Product product : lb) {
                if (product.isAvailable() && !(items.contains(product))) {
                    items.add(product);
                }
            }
        }
    }
}
