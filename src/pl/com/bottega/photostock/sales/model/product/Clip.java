package pl.com.bottega.photostock.sales.model.product;


import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.money.Money;

public class Clip extends AbstractProduct {

    public static final long FIVE_MINUTES = 1000l * 60 * 5;

    public Long getLength() {
        return length;
    }

    private Long length;

    public Clip(String number, String name, Long length, Money catalogPrice, boolean active, Client reservationOwner, Client buyer) {
        super(name, number, catalogPrice, active, reservationOwner, buyer);
        this.length = length;
    }

    public Clip(String number, String name, Long length, Money catalogPrice, boolean active) {
        this(number, name, length, catalogPrice, active, null, null);
    }

    public Clip(String number, String name, Long length, Money catalogPrice) {
        this(number, name, length, catalogPrice, true);
    }

    @Override
    public Money calculatePrice(Client client) {
        if(length > FIVE_MINUTES)
            return catalogPrice.multiply(2);
        return catalogPrice;
    }
}
