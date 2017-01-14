package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.*;
import pl.com.bottega.photostock.sales.model.money.Money;

public class PurchaseProcess {

    public static final Money OFFER_TOLERANCE = Money.valueOf(1);

    private ClientRepository clientRepository;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private PurchaseRepository purchaseRepository;

    public PurchaseProcess(ClientRepository clientRepository,
                           ReservationRepository reservationRepository,
                           ProductRepository productRepository,
                           PurchaseRepository purchaseRepository) {

        this.clientRepository = clientRepository;
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public String getReservation(String clientNumber) {
        Client client = clientRepository.get(clientNumber);
        if(!client.isActive())
            throw new IllegalArgumentException(String.format("Client %s is not active",
                    clientNumber));
        Reservation reservation = reservationRepository.getActiveReservationForClient(clientNumber);
        if(reservation == null) {
            reservation = new Reservation(client);
            reservationRepository.put(reservation);
        }
        return reservation.getNumber();
    }

    public void add(String reservationNumber, String productNumber) {
        Reservation reservation = findReservation(reservationNumber);
        Product product = productRepository.get(productNumber);
        if(product == null) {
            throw new IllegalArgumentException(String.format("Product %s doesn't exist", productNumber));
        }
        reservation.add(product);
    }

    public Offer calculateOffer(String reservationNumber) {
        Reservation reservation = findReservation(reservationNumber);
        return reservation.generateOffer();
    }

    public void confirm(String reservationNumber, Offer customerOffer) {
        Reservation reservation = findReservation(reservationNumber);
        Offer actualOffer = reservation.generateOffer();
        if (actualOffer.sameAs(customerOffer, OFFER_TOLERANCE)) {
            createPurchase(reservationNumber, reservation, actualOffer);
        } else {
            throw new OfferMismatchException();
        }
    }

    private void createPurchase(String reservationNumber, Reservation reservation, Offer actualOffer) {
        Client client = reservation.getOwner();
        client.charge(actualOffer.getTotalcost(), String.format("Purchase for reservation: %s", reservationNumber));
        Purchase purchase = new Purchase(client, actualOffer.getItems());
        purchaseRepository.put(purchase);
        reservation.deactivate();
    }

    private Reservation findReservation(String reservationNumber) {
        Reservation reservation = reservationRepository.get(reservationNumber);
        if(reservation == null) {
            throw new IllegalArgumentException(String.format("Reservation %s doesn't exist", reservationNumber));
        }
        return reservation;
    }
}
