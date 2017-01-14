package pl.com.bottega.photostock.sales.infrastructure;

import pl.com.bottega.photostock.sales.model.purchase.Reservation;
import pl.com.bottega.photostock.sales.model.purchase.ReservationRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryReservationRepository implements ReservationRepository {

    private static Map<String, Reservation> REPOSITORY = new HashMap<>();

    @Override
    public void put(Reservation reservation) {
        REPOSITORY.put(reservation.getNumber(), reservation);
    }

    @Override
    public Reservation get(String reservationNumber) {
        return REPOSITORY.get(reservationNumber);
    }

    @Override
    public Reservation getActiveReservationForClient(String clientNumber) {
        for(Reservation reservation : REPOSITORY.values()) {
            if(reservation.isOwnedBy(clientNumber) && reservation.isActive())
                return reservation;
        }
        return null;
    }
}
