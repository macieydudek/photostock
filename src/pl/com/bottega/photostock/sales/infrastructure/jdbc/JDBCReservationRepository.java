package pl.com.bottega.photostock.sales.infrastructure.jdbc;

import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;
import pl.com.bottega.photostock.sales.model.purchase.Reservation;
import pl.com.bottega.photostock.sales.model.purchase.ReservationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class JDBCReservationRepository implements ReservationRepository {


    private Connection connection;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    private static final String INSERT_RESERVATION_SQL = "INSERT INTO reservations(number, clientid, active) VALUES (?,?,?)";
    private static final String GET_RESERVATION_SQL = "SELECT * FROM reservations WHERE number =?";
    private static final String GET_RESERVATION_OWNER_SQL = "SELECT number FROM clients WHERE id =?";
    private static final String GET_PRODUCTS_FOR_RESERVATION = "SELECT product_id FROM reservations_products WHERE reservation_id =?";
    private static final String GET_CLIENT_ID_SQL = "SELECT id FROM clients WHERE number=?";
    private static final String INSERT_PRODUCT_TO_RESERVATION = "INSERT INTO reservations_produts VALUES(?,?)";
    private static final String GET_RESERVATION_ID_SQL = "SELECT id FROM reservations WHERE number =?";
    private static final String GET_RESERVATIONS_FOR_CLIENT = "SELECT * FROM reservations WHERE clientid =?";


    public JDBCReservationRepository(Connection connection, ClientRepository clientRepository, ProductRepository productRepository) {

        this.connection = connection;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Reservation get(String reservationNumber) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_RESERVATION_SQL);
            preparedStatement.setString(1, reservationNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return parseReservation(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void put(Reservation reservation) {
        checkIfReservationInDB(reservation);
        try {
            int clientId = getReservationOwnerId(reservation.getOwner().getNumber());
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RESERVATION_SQL);
            preparedStatement.setString(1, reservation.getNumber());
            preparedStatement.setInt(2, clientId);
            preparedStatement.setBoolean(3, reservation.isActive());
            preparedStatement.executeUpdate();
            insertProductsToDb(reservation);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

    }

    private void insertProductsToDb(Reservation reservation) throws SQLException {
        Collection<Product> items = reservation.getItems();
        for (Product product : items) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_TO_RESERVATION);
            preparedStatement.setInt(1, getReservationId(reservation));
            preparedStatement.setInt(1, Integer.parseInt(product.getNumber()));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Reservation getActiveReservationForClient(String clientNumber) {
        try {
            int clientId = getReservationOwnerId(clientNumber);
            PreparedStatement preparedStatement = connection.prepareStatement(GET_RESERVATIONS_FOR_CLIENT);
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBoolean("active")) {
                    return parseReservation(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private int getReservationId(Reservation reservation) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_RESERVATION_ID_SQL);
        preparedStatement.setString(1, reservation.getNumber());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            throw new IllegalArgumentException(String.format("Reservation %s not in database", reservation.getNumber()));
        }
    }

    private void checkIfReservationInDB(Reservation reservation) {
        if (get(reservation.getNumber()) != null) {
            throw new IllegalArgumentException(String.format("Datebase already contains reservation %s.", reservation.getNumber()));
        }
    }

    private Reservation parseReservation(ResultSet resultSet) throws SQLException {
        int reservationId = resultSet.getInt("id");
        String number = resultSet.getString("number");
        int clientId = resultSet.getInt("clientid");
        boolean active = resultSet.getBoolean("active");
        return new Reservation(getClient(clientId), number, getItems(reservationId), active);
    }

    private Collection<Product> getItems(int reservationId) throws SQLException {
        Collection<Product> products = new LinkedList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS_FOR_RESERVATION);
        preparedStatement.setInt(1, reservationId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String productNumber = String.valueOf(resultSet.getInt("id"));
            products.add(productRepository.get(productNumber));
        }
        return products;
    }

    private Client getClient(int clientId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_RESERVATION_OWNER_SQL);
        preparedStatement.setInt(1, clientId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            throw new IllegalArgumentException(String.format("Client with ID: %d not in datebase", clientId));
        }
        String clientNumber = resultSet.getString("number");
        return clientRepository.get(clientNumber);
    }

    private int getReservationOwnerId(String clientNumber) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_ID_SQL);
        preparedStatement.setString(1, clientNumber);
        ResultSet rs1 = preparedStatement.executeQuery();
        rs1.next();
        return rs1.getInt("id");
    }
}
