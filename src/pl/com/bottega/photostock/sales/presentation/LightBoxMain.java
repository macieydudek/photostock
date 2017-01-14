package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.application.AuthenticationProcess;
import pl.com.bottega.photostock.sales.application.LightBoxMenagment;
import pl.com.bottega.photostock.sales.application.ProductCatalog;
import pl.com.bottega.photostock.sales.application.PurchaseProcess;
import pl.com.bottega.photostock.sales.infrastructure.*;
import pl.com.bottega.photostock.sales.model.*;

import java.util.Scanner;

public class LightBoxMain {

    private MainScreen mainScreen;
    private SearchScreen searchScreen;
    private ReservationScreen reservationScreen;
    private OfferScreen offerScreen;
    private LoginScreen loginScreen;
    private LightBoxScreen lightBoxScreen;

    public LightBoxMain() {
        Scanner sc = new Scanner(System.in);

        ProductRepository productRepository = new InMemoryProductRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();
        LightBoxRepository lightBoxRepository = new InMemoryLightBoxRepository();
        ClientRepository clientRepository = new InMemoryClientRepository();
        PurchaseRepository purchaseRepository = new InMemoryPurchaseRepository();

        ProductCatalog productCatalog = new ProductCatalog(productRepository);
        AuthenticationProcess authenticationProcess = new AuthenticationProcess(clientRepository);
        PurchaseProcess purchaseProcess = new PurchaseProcess(clientRepository, reservationRepository, productRepository, purchaseRepository);
        LightBoxMenagment lightBoxMenagment = new LightBoxMenagment(purchaseProcess, clientRepository, lightBoxRepository, productRepository);

        loginScreen = new LoginScreen(sc, authenticationProcess);
        lightBoxScreen = new LightBoxScreen(sc, lightBoxMenagment, loginScreen);
        searchScreen = new SearchScreen(sc, productCatalog, loginScreen);
        reservationScreen = new ReservationScreen(sc, purchaseProcess, loginScreen);
        offerScreen = new OfferScreen(sc, loginScreen, purchaseProcess);
        mainScreen = new MainScreen(sc, searchScreen, reservationScreen, offerScreen, lightBoxScreen);
    }

    public void start() {
        loginScreen.print();
        mainScreen.print();
    }

    public static void main(String[] args) {
        new LightBoxMain().start();
    }
}
