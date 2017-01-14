package pl.com.bottega.photostock.sales.model.product;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.money.Money;

public interface Product {
    Money calculatePrice(Client client);

    boolean isAvailable();

    void reserverPer(Client client);

    void unreservedPer(Client client);

    void soldPer(Client client);

    String getNumber();

    String getName();

    boolean isActive();

    void deactivate();

    default void ensureAvailable() { //nowy ficzer w JAvie 8, można robić takie metody, które odnoszą się do innych w tym samym interfejsie
        if(!isAvailable())
            throw new ProductNotAvailableException(this);
    };
}
