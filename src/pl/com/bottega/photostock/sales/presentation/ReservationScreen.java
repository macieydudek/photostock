package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.application.PurchaseProcess;
import pl.com.bottega.photostock.sales.model.ProductNotAvailableException;

import java.util.Scanner;

public class ReservationScreen {

    private Scanner sc;
    private PurchaseProcess purchaseProcess;
    private LoginScreen loginScreen;

    public ReservationScreen(Scanner sc, PurchaseProcess purchaseProcess, LoginScreen loginScreen) {
        this.sc = sc;
        this.purchaseProcess = purchaseProcess;
        this.loginScreen = loginScreen;
    }

    public void print() {
        while(true){
            System.out.println("Podaj numer produktu do rezerwacji: ");
            String productNumber = sc.nextLine();
            try {
                String clientNumber = loginScreen.getAuthenticatedClientNumber();
                String reservationNumber = purchaseProcess.getReservation(clientNumber);
                purchaseProcess.add(reservationNumber, productNumber);
                System.out.println(String.format("Product %s został dodany do rezerwacji %s", productNumber, reservationNumber));
                return;
            }
            catch (ProductNotAvailableException ex) {
                System.out.println(String.format("Przepraszamy, produkt %s jest niedostępny", productNumber));
            }
            catch (IllegalArgumentException ex) {
                System.out.println("Nieprawidłowy numer produktu");
            }

        }
    }
}
