package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.lightbox.LightBox;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import java.util.Collection;

public class LightBoxMenagment {

    private ClientRepository clientRepository;
    private LightBoxRepository lightBoxRepository;
    private ProductRepository productRepository;
    private PurchaseProcess purchaseProcess;


    public LightBoxMenagment(PurchaseProcess purchaseProcess, //klasa z tej samej warstwy ma pierwszeństwo
                             ClientRepository clientRepositry,
                             LightBoxRepository lightBoxRepository,
                             ProductRepository productRepository
                             ) {
        this.clientRepository = clientRepositry;
        this.lightBoxRepository = lightBoxRepository;
        this.productRepository = productRepository;
        this.purchaseProcess = purchaseProcess;
    }

    public Collection<String> getLightBoxNames(String customerNumber) {
        Client client = getClient(customerNumber);
        return lightBoxRepository.getLightBoxNames(client);
    }

    public LightBox getLightBox(String customerNumber, String lightBoxName) {
        Client client = getClient(customerNumber);
        LightBox lightBox = lightBoxRepository.findLightBox(client, lightBoxName);
        ensureLightBoxFound(lightBoxName, lightBox);
        return lightBox;
    }

    public void addProduct(String customerNumber, String lightBoxName, String productNumber) {
        Client client = getClient(customerNumber);
        Product product = productRepository.get(productNumber);
        if (product == null) {
            throw new IllegalArgumentException(String.format("No product with number %s", customerNumber));
        }
        LightBox lightBox = getOrCreateLightBox(lightBoxName, client);
        lightBox.add(product);
        lightBoxRepository.updateLightBox(lightBox);
    }

    public void reserve(String clientNumber, String lightBoxName) {
        LightBox lightBox = getLightBox(clientNumber, lightBoxName);
        String reservationNumber = purchaseProcess.getReservation(clientNumber);
        for(Product product : lightBox) {
            if(product.isAvailable()){
                purchaseProcess.add(reservationNumber, product.getNumber());
            }
        }
    }

    private Client getClient(String customerNumber) {
        Client client = clientRepository.get(customerNumber);
        if (client == null)
            throw new IllegalArgumentException(String.format("No client with number %s", customerNumber));
        return client;
    }

    private LightBox getOrCreateLightBox(String lightBoxName, Client client) {
        try {
            LightBox lightBox = lightBoxRepository.findLightBox(client, lightBoxName);
            if (lightBox == null) {
                lightBox = new LightBox(client, lightBoxName);
                lightBoxRepository.put(lightBox);
            }
            return lightBox;
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
    }

    private void ensureLightBoxFound(String lightBoxName, LightBox lightBox) {
        if (lightBox == null)
            throw new IllegalArgumentException(String.format("No LightBox with the given name %s", lightBoxName));
    }
}
