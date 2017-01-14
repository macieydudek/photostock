package pl.com.bottega.photostock.sales.model.product;

import pl.com.bottega.photostock.sales.model.product.Product;

public class ProductNotAvailableException extends RuntimeException {

    public ProductNotAvailableException(Product product) {
        super(String.format("product %s is unavailable", product.getNumber()));
    }

}
