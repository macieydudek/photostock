package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.application.AuthenticationProcess;
import pl.com.bottega.photostock.sales.application.LightBoxMenagment;
import pl.com.bottega.photostock.sales.application.ProductCatalog;
import pl.com.bottega.photostock.sales.application.PurchaseProcess;
import pl.com.bottega.photostock.sales.infrastructure.csv.CSVClientRepository;
import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCClientRepository;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCLightBoxRepository;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCProductRepository;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCReservationRepository;
import pl.com.bottega.photostock.sales.infrastructure.memory.*;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;
import pl.com.bottega.photostock.sales.model.purchase.PurchaseRepository;
import pl.com.bottega.photostock.sales.model.purchase.ReservationRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

        try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/photostock");
            ClientRepository clientRepository = new JDBCClientRepository(c);
            ProductRepository productRepository = new JDBCProductRepository(c, clientRepository);
            LightBoxRepository lightBoxRepository = new JDBCLightBoxRepository(c, clientRepository, productRepository);
            ReservationRepository reservationRepository = new JDBCReservationRepository(c, clientRepository, productRepository);
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
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }


    }

    public void start() {
        loginScreen.print();
        mainScreen.print();
    }

    public static void main(String[] args) {
        new LightBoxMain().start();
    }
}
