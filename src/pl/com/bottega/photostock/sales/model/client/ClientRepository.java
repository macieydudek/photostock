package pl.com.bottega.photostock.sales.model.client;

public interface ClientRepository {

    Client get(String clientNumber);

    void update(Client client);
}
