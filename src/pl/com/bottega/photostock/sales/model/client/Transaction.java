package pl.com.bottega.photostock.sales.model.client;


import pl.com.bottega.photostock.sales.model.money.Money;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Transaction {
    private String description;

    private LocalDateTime timestamp;
    public Transaction (Money value, String description) {
        this.value = value;
        this.description = description;
        timestamp = LocalDateTime.now();
    }

    public Money getValue() {
        return value;
    }

    private Money value;

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
