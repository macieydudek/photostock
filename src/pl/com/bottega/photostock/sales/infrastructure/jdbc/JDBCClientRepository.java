package pl.com.bottega.photostock.sales.infrastructure.jdbc;

import pl.com.bottega.photostock.sales.infrastructure.csv.DataAccessException;
import pl.com.bottega.photostock.sales.model.client.*;
import pl.com.bottega.photostock.sales.model.money.IntegerMoney;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class JDBCClientRepository implements ClientRepository {

    private static final String GET_CLIENT_SQL = "SELECT * FROM clients WHERE number = ?";
    private static final String GET_TRANSACTIONS_SQL = "SELECT * FROM transactions WHERE client_id = ?";
    private static final String UPDATE_CLIENT_SQL = "UPDATE clients SET name=?, active=?, status=?, balancecents=?, creditLimit=? WHERE number=?";
    private static final String GET_TRANSACTIONS_COUNT_SQL = "SELECT COUNT(*) AS total FROM transactions WHERE client_id = ?";
    private static final String INSERT_TRANSACTION_SQL = "INSERT INTO transactions (client_id, value, date, description) VALUES(?, ?, ?, ?)";
    private static final String GET_CLIENT_ID_SQL = "SELECT id FROM clients WHERE number=?";
    private Connection connection;

    public JDBCClientRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Client get(String clientNumber) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_SQL);
            preparedStatement.setString(1, clientNumber);
            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next())
                return null;
            return parseClient(rs);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void update(Client client) {
        try {
            //name=?, active=?, status=?, balance=?, creditLimit=? WHERE number=?
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLIENT_SQL);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setBoolean(2, client.isActive());
            preparedStatement.setString(3, client.getStatus().toString());
            preparedStatement.setLong(4, client.getBalance().convertToInteger().toCents());
            if (client instanceof VIPClient) {
                VIPClient vipClient = (VIPClient) client;
                preparedStatement.setLong(5, 0);
            } else {
                preparedStatement.setLong(5, 0);
            }
            preparedStatement.setString(6, client.getNumber());
            preparedStatement.executeUpdate();
            updateTransactions(client);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Transaction praseTransaction(ResultSet resultSet) throws SQLException {
        Money value = new IntegerMoney(resultSet.getLong("value"), Money.DEFAULT_CURRENCY);
        String description = resultSet.getString("description");
        LocalDateTime localDateTime = resultSet.getTimestamp("date").toLocalDateTime();
        return new Transaction(value, description, localDateTime);
    }

    private void updateTransactions(Client client) throws SQLException {
        int client_id = getClientId(client);
        int transactionsCount = getTransactionsCount(client_id);
        if (client.getTransactions().size() == transactionsCount) {
            return;
        }
        List<Transaction> transactions = new ArrayList<>(client.getTransactions());
        transactions.sort(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
        transactions = transactions.subList(transactionsCount, transactions.size());
        for (Transaction tr : transactions) {
            insertTransaction(client_id, tr);
        }
    }

    private void insertTransaction(int client_id, Transaction tr) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_SQL);
        preparedStatement.setInt(1, client_id);
        preparedStatement.setLong(2, tr.getValue().convertToInteger().toCents());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(tr.getTimestamp()));
        preparedStatement.setString(4, tr.getDescription());
        preparedStatement.executeUpdate();
    }

    private int getTransactionsCount(int client_id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_TRANSACTIONS_COUNT_SQL);
        preparedStatement.setInt(1, client_id);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt("total");
    }

    private int getClientId(Client client) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_ID_SQL);
        preparedStatement.setString(1, client.getNumber());
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private Collection<Transaction> getTransaction(Integer clientId) throws SQLException {
        Collection<Transaction> transactions = new LinkedList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_TRANSACTIONS_SQL);
        preparedStatement.setInt(1, clientId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Transaction transaction = praseTransaction(resultSet);
            transactions.add(transaction);
        }
        return transactions;
    }

    private Client parseClient(ResultSet rs) throws SQLException {
        String number = rs.getString("number");
        String name = rs.getString("name");
        boolean active = rs.getBoolean("active");
        ClientStatus clientStatus = ClientStatus.valueOf(rs.getString("status").trim());
        Money balance = new IntegerMoney(rs.getLong("balancecents"), Money.DEFAULT_CURRENCY);
        Integer clientsId = rs.getInt("id");
        if (clientStatus == ClientStatus.VIP) {
            Money creditLimit = new IntegerMoney(rs.getLong("creditLimit"), Money.DEFAULT_CURRENCY);
            return new VIPClient(number, name, new Address(), balance, creditLimit, active, getTransaction(clientsId));
        }
        return new Client(number, name, new Address(), clientStatus, balance, active, getTransaction(clientsId));
    }
}
