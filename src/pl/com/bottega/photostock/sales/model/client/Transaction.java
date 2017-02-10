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

    public Transaction(Money value, String description, String time) {
        this(value, description);
        this.timestamp = LocalDateTime.parse(time);
    }

    public Transaction(Money value, String description, LocalDateTime time) {
        this(value, description);
        this.timestamp = time;
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
