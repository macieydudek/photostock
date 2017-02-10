package pl.com.bottega.photostock.sales.infrastructure.csv;

public class DataAccessException extends RuntimeException{

    public DataAccessException(Exception ex) {
        super(ex);
    }
}
