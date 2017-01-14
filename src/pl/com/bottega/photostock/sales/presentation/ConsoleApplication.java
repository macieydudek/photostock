package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.infrastructure.InMemoryProductRepository;
import pl.com.bottega.photostock.sales.model.client.Address;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.VIPClient;
import pl.com.bottega.photostock.sales.model.money.Money;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;
import pl.com.bottega.photostock.sales.model.purchase.Offer;
import pl.com.bottega.photostock.sales.model.purchase.Purchase;
import pl.com.bottega.photostock.sales.model.purchase.Reservation;

public class ConsoleApplication {
    public static void main(String[] args) {
        ProductRepository productRepository = new InMemoryProductRepository();
        Product picture1 = productRepository.get("1");
        Product picture2 = productRepository.get("2");
        Product picture3 = productRepository.get("3");
        Product clip1 = productRepository.get("4");
        Product product5 = productRepository.get("5");
        Client client = new Client("Johny X", new Address(), Money.valueOf(10000));
        Client vipClient = new VIPClient("Johny V", new Address(),
                Money.ZERO, Money.valueOf(10000));
        System.out.println(vipClient.introduce());
        Reservation reservation = new Reservation(vipClient);

        reservation.add(picture1);
        reservation.add(picture2);
        reservation.add(picture3);
        reservation.add(clip1);
        reservation.add(product5);
        System.out.println("After adding items count: " + reservation.getItemsCount());


        Offer offer = reservation.generateOffer();

        boolean canAfford = vipClient.canAfford(offer.getTotalcost());
        System.out.println("Client can afford: " + canAfford);
        if (canAfford) {
            vipClient.charge(offer.getTotalcost(), "Test purchase");
            Purchase purchase = new Purchase(vipClient, picture1, picture2, picture3, clip1, product5);
            System.out.println("client purchased: " + purchase.getItemsCount() + " products");
            System.out.println("Total cost: " + offer.getTotalcost());
        }
        else {
            System.out.println("Client cannot afford");
        }
    }
}
