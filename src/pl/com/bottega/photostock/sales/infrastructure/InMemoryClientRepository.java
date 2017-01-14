package pl.com.bottega.photostock.sales.infrastructure;

import pl.com.bottega.photostock.sales.model.client.Address;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.client.VIPClient;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClientRepository implements ClientRepository {

    private static final Map<String, Client> REPOSITORY = new HashMap<>();

    static {
        Client client = new Client("Johny X", new Address(), Money.valueOf(10000));
        Client vipClient = new VIPClient("Johny V", new Address(),
                Money.ZERO, Money.valueOf(10000));
        REPOSITORY.put(client.getNumber(), client);
        REPOSITORY.put(vipClient.getNumber(), vipClient);
    }

    @Override
    public Client get(String clientNumber) {
        return REPOSITORY.get(clientNumber);
    }
}
