package pl.com.bottega.photostock.sales.model;

public interface ProductRepository {
    Product get(String number);

    void put(Product product);
}
