package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.*;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.Collection;

public class LightBoxRepositoryTester {

    public static void main(String[] args) {
        ProductRepository productRepository = new InMemoryProductRepository();
        Product picture1 = productRepository.get("1");
        Product picture2 = productRepository.get("2");
        Product picture3 = productRepository.get("3");

        Client client = new Client("Johny X", new Address(), Money.valueOf(100));
        Client danny = new Client("Danny X", new Address(), Money.valueOf(100));

        LightBox lb1 = new LightBox(danny, "Samochody");
        LightBox lb2 = new LightBox(client, "Bmw");
        LightBox lb3 = new LightBox(danny, "Wyścigowe samochody");
        LightBox lb4 = new LightBox(danny, "OldSchool");

        InMemoryLightBoxRepository myRepository = new InMemoryLightBoxRepository();

        lb1.add(picture1);
        lb1.add(picture2);
        lb1.add(picture3);
        lb2.add(picture1);
        lb3.add(picture3);

        myRepository.put(lb2);
        myRepository.put(lb3);
        myRepository.put(lb4);
        myRepository.put(lb4);
        myRepository.put(lb1);

        printRepository(myRepository.getFor(client));
        printRepository(myRepository.getFor(danny));

    }

    public static void printRepository(Collection<LightBox> lightboxes) {
        int nr = 1;
        for(LightBox lightbox : lightboxes) {
            System.out.println(String.format("%d. %s - %s", nr, lightbox.getName(), lightbox.getOwner().getName()));
            LightBoxTest.printlLightBox(lightbox);
            System.out.println("===================================");
            nr++;
        }
    }
}
