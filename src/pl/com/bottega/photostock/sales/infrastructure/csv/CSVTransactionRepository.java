package pl.com.bottega.photostock.sales.infrastructure.csv;

import com.sun.deploy.util.StringUtils;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.Transaction;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

class CSVTransactionsRepository {

    private String folderPath;

    CSVTransactionsRepository(String folderPath) {
        this.folderPath = folderPath;
    }

    void saveTransactions(Client client, Collection<Transaction> transactions) {
        File file = new File(getRepositoryPath(client.getNumber()));
        try(PrintWriter printWriter = new PrintWriter(file)) {
            for(Transaction transaction : transactions){
                String[] components = {
                    transaction.getValue().toString(),
                    transaction.getDescription(),
                        transaction.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)
                };
                printWriter.println(StringUtils.join(Arrays.asList(components),","));
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    Collection<Transaction> getTransactions(String clientNumber) {
        return null;
    }

    private String getRepositoryPath(String clientNumber) {
        return folderPath + File.separator + "clients" + clientNumber + "-transations.csv";
    }
}
