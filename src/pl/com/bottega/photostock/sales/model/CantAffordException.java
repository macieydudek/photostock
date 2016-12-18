package pl.com.bottega.photostock.sales.model;

public class CantAffordException extends RuntimeException {


    public CantAffordException(String message) {
        super(message);
    }
}
