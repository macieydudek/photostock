package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.application.ProductCatalog;
import pl.com.bottega.photostock.sales.model.Client;
import pl.com.bottega.photostock.sales.model.Product;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class SearchScreen {

    private Scanner scanner;
    private final ProductCatalog productCatalog;
    private LoginScreen loginScreen;

    public SearchScreen(Scanner sc, ProductCatalog productCatalog,
                        LoginScreen loginScreen) {
        this.scanner = sc;
        this.productCatalog = productCatalog;
        this.loginScreen = loginScreen;
    }

    public void print() {
        String name = getQuery();
        String[] tags = getTags();
        Money priceFrom = getMoney("Cena od");
        Money priceTo = getMoney("Cena do");

        List<Product> products = productCatalog.find(loginScreen.getClient(), name, tags, priceFrom, priceTo
        );
        printProducts(loginScreen.getClient(), products);
    }

    private void printProducts(Client client, List<Product> products) {
        System.out.println("Wyszukane produkty: ");
        for (Product product : products) {
            System.out.println(
                    String.format("%s | %s | %s",
                            product.getNumber(),
                            product.getName(),
                            product.calculatePrice(client)
                    ));
        }
    }

    private Money getMoney(String prompt) {
        while (true) {
            try {
                System.out.println(prompt + ": ");
                float f = scanner.nextFloat();
                scanner.nextLine();
                return Money.valueOf(f);
            } catch (InputMismatchException ex) {
                scanner.nextLine();
                System.out.println("Wprowadź poprawną cenę np. 9.99");
            }
        }
    }

    private String[] getTags() {
        System.out.println("Tagi: ");
        String tagsRead = scanner.nextLine().trim();
        if (tagsRead.length() == 0) return null;
        return tagsRead.split(" ");
    }

    private String getQuery() {
        System.out.println("Nazwa: ");
        return scanner.nextLine();
    }
}
