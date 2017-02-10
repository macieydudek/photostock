package pl.com.bottega.photostock.sales.infrastructure.jdbc;

import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.money.IntegerMoney;
import pl.com.bottega.photostock.sales.model.money.Money;
import pl.com.bottega.photostock.sales.model.product.Clip;
import pl.com.bottega.photostock.sales.model.product.Picture;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.io.PipedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class JDBCProductRepository implements ProductRepository {

    private ClientRepository clientRepository;

    private Connection connection;

    private final String GET_PRODUCT_SQL = "SELECT * FROM products WHERE id=?";
    private final String GET_TAGS_FOR_PRODUCT_SQL = "SELECT tags.name FROM tags JOIN products_tags ON tags.id = products_tags.tag_id WHERE products_tags.product_id =?";
    private final String GET_OWNER_FOR_PRODUCT_SQL = "SELECT clients.number FROM clients JOIN purchases ON clients.id = purchases.clientid JOIN purchases_products ON purchases.id = purchases_products.purchase_id WHERE purchases_products.product_id =?";
    private final String GET_RESERVATION_OWNER_FOR_PRODUCT_SQL = "SELECT clients.number FROM clients JOIN reservations ON clients.id = reservations.clientid JOIN reservations_products ON reservations.id = reservations_products.reservation_id WHERE reservations_products.product_id =?";
    private final String INSERT_PRODUCT_SQL = "INSERT INTO products (name, available, pricecents, pricecurrency, type, length) VALUES (?,?,?,?,?,?)";
    private final String GET_PRODUCTS_SQL = "SELECT * FROM products WHERE name LIKE ?";
    private final String GET_ACTIVE_PRODUCTS_SQL = "SELECT * FROM products WHERE name LIKE ? AND available = true";

    public JDBCProductRepository(Connection connection, ClientRepository clientRepository) {

        this.connection = connection;
        this.clientRepository = clientRepository;
    }

    @Override
    public Product get(String number) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCT_SQL);
            int id = Integer.parseInt(number);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return parseProduct(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

    }

    @Override
    public void put(Product product) {
        checkIfAlreadyInDatebase(product);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_SQL);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setBoolean(2, product.isActive());
            IntegerMoney priceAsIntegerMoney = product.getCatalogPrice().convertToInteger();
            preparedStatement.setLong(3, priceAsIntegerMoney.toCents());
            preparedStatement.setString(4, parseCurrency(priceAsIntegerMoney));
            preparedStatement.setString(5, parseType(product));
            if (product instanceof Clip) {
                Clip c = (Clip) product;
                preparedStatement.setLong(6, c.getLength());
            } else {
                preparedStatement.setLong(6, 0);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<Product> find(Client client, String nameQuery, String[] tags, Money priceFrom, Money priceTo, boolean onlyActive) {
        List<Product> products = new LinkedList<>();
        try {
            PreparedStatement preparedStatement = determineStatement(onlyActive);
            preparedStatement.setString(1, nameQuery + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = parseProduct(resultSet);
                if (tagsMatch(product, tags) && matchesPriceFrom(client, product, priceFrom) && matchesPriceTo(client, product, priceTo)) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return products;
    }

    private boolean matchesPriceTo(Client client, Product product, Money priceTo) {
        return priceTo == null || product.calculatePrice(client).lte(priceTo);
    }

    private boolean matchesPriceFrom(Client client, Product product, Money priceFrom) {
        return priceFrom == null || product.calculatePrice(client).gte(priceFrom);
    }

    private boolean tagsMatch(Product product, String[] tags) {
        if(tags == null || tags.length == 0) return true;

        if(!(product instanceof Picture)) return false;

        Picture pic = (Picture) product;

        for(String str : tags) {
            if(!(pic.hasTag(str))){
                return false;
            }
        }
        return true;
    }

    private PreparedStatement determineStatement(boolean onlyActive) throws SQLException {
        if (onlyActive) {
            return connection.prepareStatement(GET_ACTIVE_PRODUCTS_SQL);
        }
        return connection.prepareStatement(GET_PRODUCTS_SQL);
    }

    private String parseType(Product product) {
        if (product instanceof Picture) {
            return "picture";
        }
        return "clip";
    }

    private String parseCurrency(IntegerMoney priceAsIntegerMoney) {
        return "CRD";
    }

    private void checkIfAlreadyInDatebase(Product product) {
        if (get(product.getNumber()) != null) {
            throw new IllegalArgumentException(String.format("Product %s is already in the datebase", product.getNumber()));
        }
    }

    private Product parseProduct(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        int id = resultSet.getInt("id");
        Money catalogPrice = Money.valueOf(resultSet.getLong("pricecents"), getCurrency(resultSet.getString("pricecurrency")));
        String type = resultSet.getString("type").toLowerCase();
        boolean active = resultSet.getBoolean("available");
        Client reservationOwner = checkClientFroProduct(id, GET_RESERVATION_OWNER_FOR_PRODUCT_SQL);
        Client buyer = checkClientFroProduct(id, GET_OWNER_FOR_PRODUCT_SQL);
        if (type.equals("picture")) {
            return new Picture(String.valueOf(id), name, getTags(id), catalogPrice, active, reservationOwner, buyer);
        } else if (type.equals("clip")) {
            long length = resultSet.getLong("length");
            return new Clip(String.valueOf(id), name, length, catalogPrice, active, reservationOwner, buyer);
        }
        return null;
    }

    private Client checkClientFroProduct(int product_id, String sqlQuery) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, product_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return clientRepository.get(resultSet.getString("number"));
        }
        return null;
    }

    private Collection<String> getTags(int id) throws SQLException {
        Collection<String> tags = new LinkedList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_TAGS_FOR_PRODUCT_SQL);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String tag = resultSet.getString("name");
            tags.add(tag);
        }
        return tags;
    }

    private Money.Currency getCurrency(String pricecurrency) {
        return Money.DEFAULT_CURRENCY;
    }
}
