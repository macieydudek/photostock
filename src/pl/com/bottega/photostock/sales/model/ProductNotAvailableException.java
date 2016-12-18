package pl.com.bottega.photostock.sales.model;

public class ProductNotAvailableException extends RuntimeException {

    public ProductNotAvailableException(Product product) {
        super(String.format("product %s is unavailable", product.getNumber()));
    }

}
