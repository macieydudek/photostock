package pl.com.bottega.photostock.sales.model.client;

public class CantAffordException extends RuntimeException {


    public CantAffordException(String message) {
        super(message);
    }
}
