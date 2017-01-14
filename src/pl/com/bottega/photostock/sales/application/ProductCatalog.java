package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.List;

public class ProductCatalog {

    private ProductRepository productRepository;

    public ProductCatalog(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public List<Product> find(Client client, String nameQuery, String[] tags,
                              Money priceFrom, Money priceTo) {
        // aspekty techniczne: autentykacja, logowanie, tranzakcje
        // polaczenia z baza danych
        return productRepository.find(client, nameQuery, tags, priceFrom, priceTo, true);
    }
}
