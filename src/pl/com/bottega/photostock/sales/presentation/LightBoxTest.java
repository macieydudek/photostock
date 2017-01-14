package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.infrastructure.InMemoryProductRepository;
import pl.com.bottega.photostock.sales.model.*;
import pl.com.bottega.photostock.sales.model.money.Money;

public class LightBoxTest {
    public static void main(String[] args) {
        ProductRepository productRepository = new InMemoryProductRepository();
        Product Product1 = productRepository.get("1");
        Product Product2 = productRepository.get("2");
        Product Product3 = productRepository.get("3");



        Client client = new Client("Johny X", new Address(), Money.valueOf(100));
        Client danny = new Client("Danny X", new Address(), Money.valueOf(100));

        LightBox lb1 = new LightBox(client, "Samochody");
        LightBox lb2 = new LightBox(client, "Bmw");
        LightBox lb3 = new LightBox(danny, "Wyścigowe samochody");
        LightBox lb4 = new LightBox(danny, "OldSchool");

        lb1.add(Product1);
        lb1.add(Product2);
        lb1.add(Product3);
        lb2.add(Product1);
        Product1.deactivate();
        lb3.add(Product3);

        printLightBoxes(lb1, lb2, lb3, lb4);

        LightBox joinedLb = LightBox.joined(client, "Joined lightbox", lb1, lb2, lb3);
        System.out.println("Joined lightBox");

        printlLightBox(joinedLb);

    }

    public static void printLightBoxes(LightBox ... lightboxes) {
        int nr = 1;
        for(LightBox lightbox : lightboxes) {
            System.out.println(String.format("%d. %s - %s", nr, lightbox.getName(), lightbox.getOwner().getName()));
            printlLightBox(lightbox);
            System.out.println("===================================");
            nr++;
        }
    }

    public static void printlLightBox(LightBox lightbox) {
         for(Product Product : lightbox){
             System.out.println(
                     String.format("%s%s | %s",
                     (Product.isActive() ? "" : "X "),
                     Product.getNumber(),
                     Product.calculatePrice(lightbox.getOwner())
             ));
         }
    }


}
