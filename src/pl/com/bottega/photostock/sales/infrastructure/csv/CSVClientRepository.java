package pl.com.bottega.photostock.sales.infrastructure.csv;

import com.sun.deploy.util.StringUtils;
import com.sun.tools.javac.code.Attribute;
import pl.com.bottega.photostock.sales.model.client.*;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

public class CSVClientRepository implements ClientRepository {

    private String path, tmpPath, folderPath;

    public CSVClientRepository(String folderPath) {
        this.folderPath = folderPath;
        this.path = folderPath + File.separator + "clients.csv";
        this.tmpPath = path + ".tmp";
    }

    @Override
    // number, name, active, status, balance, creditLimit
    public Client get(String clientNumber) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] attributes = line.trim().split(",");
                String number = attributes[0];
                if (number.equals(clientNumber)) {
                    return createClient(attributes, number);
                }
            }
            return null;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void update(Client client) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
             PrintWriter printWriter = new PrintWriter(new FileWriter(tmpPath))
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] attributes = line.trim().split(",");
                String number = attributes[0];
                if (number.equals(client.getNumber())) {
                    writeClient(client, printWriter);
                } else {
                    printWriter.println(line);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
        replaceFiles();
        updateTransactions(client);
    }

    private void updateTransactions(Client client) {
        CSVTransactionsRepository transactionRepository = new CSVTransactionsRepository(folderPath);
        transactionRepository.saveTransactions(client, client.getTransactions());
    }

    private void replaceFiles() {
        File file = new File(tmpPath);
        file.renameTo(new File(path));
    }

    private void writeClient(Client client, PrintWriter printWriter) {
        String[] attributes = {
                client.getNumber(),
                client.getName(),
                String.valueOf(client.isActive()),
                String.valueOf(client.getStatus()),
                String.valueOf(client.getBalance()),
                ""
        };
        if (client instanceof VIPClient) {
            VIPClient vipClient = (VIPClient) client;
            attributes[5] = String.valueOf(vipClient.getCreditLimit());
        }
        printWriter.println(StringUtils.join(Arrays.asList(attributes), ","));
    }

    private Client createClient(String[] attributes, String number) {
        String name = attributes[1];
        boolean active = Boolean.valueOf(attributes[2]);
        ClientStatus status = ClientStatus.valueOf(attributes[3]);
        Money balance = Money.valueOf(attributes[4]);
        if (status.equals(ClientStatus.VIP)) {
            Money creditLimit = Money.valueOf(attributes[5]);
            return new VIPClient(number, name, new Address(), balance, creditLimit, active, new LinkedList<>());
        } else {
            return new Client(number, name, new Address(), status, balance, active, new LinkedList<>());
        }
    }
}
