package pl.com.bottega.photostock.sales.infrastructure.jdbc;

import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.lightbox.LightBox;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

public class JDBCLightBoxRepository implements LightBoxRepository {

    private static final String INSERT_LIGHTBOX_SQL = "INSERT INTO lightboxes (client_number, name) VALUES (?, ?)";
    private static final String GET_LIGHTBOX_SQL = "SELECT * FROM lightboxes WHERE name =? AND client_number =?";
    private static final String GET_LIGHTBOXES_SQL = "SELECT * FROM lightboxes WHERE client_number =?";
    private static final String INSERT_PRODUCTS_SQL = "INSERT INTO lightboxes_products VALUES (?,?)";
    private static final String GET_PRODUCTS_SQL = "SELECT * FROM lightboxes_products WHERE lightbox_id =?";

    private Connection connection;
    private ClientRepository clientRepository;
    private ProductRepository productRepository;

    public JDBCLightBoxRepository(Connection connection, ClientRepository clientRepository, ProductRepository productRepository) {

        this.connection = connection;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void put(LightBox l) {
        if (findLightBox(l.getOwner(), l.getName()) != null) {
            throw new IllegalArgumentException(String.format("Lightbox %s is already in the datebase.", l.getName()));
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LIGHTBOX_SQL);
            preparedStatement.setString(1, l.getOwner().getNumber());
            preparedStatement.setString(2, l.getName());
            preparedStatement.executeUpdate();
            updateProducts(l);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public Collection<LightBox> getFor(Client client) {
        Collection<LightBox> lightBoxes = new LinkedList<>();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(GET_LIGHTBOXES_SQL);
            preparedStatement.setString(1, client.getNumber());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                LightBox lightBox = parseLightBox(rs);
                lightBoxes.add(lightBox);
            }
            return lightBoxes;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Collection<String> getLightBoxNames(Client client) {
        Collection<String> lightBoxNames = new LinkedList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_LIGHTBOXES_SQL);
            preparedStatement.setString(1, client.getNumber());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String lightBoxName = resultSet.getString("name");
                lightBoxNames.add(lightBoxName);
            }
            return lightBoxNames;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public void updateLightBox(LightBox lightBox) {
        try {
            int id = getId(lightBox);
            Collection<Product> productsInDatabase = retrieveProducts(id);
            for (Product product : lightBox) {
                if (!productsInDatabase.contains(product)) {
                    insertProduct(id, product);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

    }

    @Override
    public LightBox findLightBox(Client client, String lightBoxName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_LIGHTBOX_SQL);
            preparedStatement.setString(1, lightBoxName);
            preparedStatement.setString(2, client.getNumber());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return parseLightBox(resultSet);
            }
            return null;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private LightBox parseLightBox(ResultSet rs) throws SQLException {
        int lightBoxId = rs.getInt("id");
        String lightBoxName = rs.getString("name");
        String clientNumber = rs.getString("client_number");
        Collection<Product> items = retrieveProducts(lightBoxId);
        return new LightBox(clientRepository.get(clientNumber), lightBoxName, items);
    }

    private Collection<Product> retrieveProducts(int lightBoxId) {
        Collection<Product> products = new LinkedList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS_SQL);
            preparedStatement.setInt(1, lightBoxId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String productNumber = resultSet.getString("product_id");
                Product product = productRepository.get(productNumber);
                products.add(product);
            }
            return products;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private int getId(LightBox l) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_LIGHTBOX_SQL);
        preparedStatement.setString(1, l.getName());
        preparedStatement.setString(2, l.getOwner().getNumber());
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private void insertProduct(int lightBoxId, Product product) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCTS_SQL);
        preparedStatement.setInt(1, lightBoxId);
        preparedStatement.setString(2, product.getNumber());
        preparedStatement.executeUpdate();
    }

    private void updateProducts(LightBox l) throws SQLException {
        int lightBoxId = getId(l);
        for (Product product : l) {
            insertProduct(lightBoxId, product);
        }
    }
}
