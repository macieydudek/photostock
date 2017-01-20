package pl.com.bottega.photostock.sales.infrastructure.csv;

import com.sun.deploy.util.StringUtils;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.Transaction;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

class CSVTransactionsRepository {

    private String folderPath;

    CSVTransactionsRepository(String folderPath) {
        this.folderPath = folderPath;
    }

    void saveTransactions(Client client, Collection<Transaction> transactions) {
        String path = getRepositoryPath(client.getNumber());
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(path, true))) {
            for (Transaction transaction : transactions) {
                String[] components = {
                        transaction.getValue().toString(),
                        transaction.getDescription(),
                        transaction.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)
                };
                printWriter.println(StringUtils.join(Arrays.asList(components), ","));
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    Collection<Transaction> getTransactions(String clientNumber) {
        Collection<Transaction> transactions = new LinkedList<>();
        String path = getRepositoryPath(clientNumber);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] components = line.split(",");
                Transaction transaction = new Transaction(Money.valueOf(components[0]), components[1], components[2]);
                transactions.add(transaction);
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
        return transactions;
    }

    private String getRepositoryPath(String clientNumber) {
        return folderPath + File.separator + "clients" + clientNumber + "-transations.csv";
    }
}
