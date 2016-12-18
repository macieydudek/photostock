package pl.com.bottega.photostock.sales.model;


import java.time.LocalDateTime;

public class Transaction {
    private Money value;
    private String description;
    private LocalDateTime timestamp;

    public Transaction (Money value, String description) {
        this.value = value;
        this.description = description;
        timestamp = LocalDateTime.now();
    }
}
