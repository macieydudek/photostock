package pl.com.bottega.photostock.sales.presentation;

import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCClientRepository;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCLightBoxRepository;
import pl.com.bottega.photostock.sales.infrastructure.jdbc.JDBCProductRepository;
import pl.com.bottega.photostock.sales.infrastructure.memory.InMemoryProductRepository;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.client.Transaction;
import pl.com.bottega.photostock.sales.model.lightbox.LightBox;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;
import pl.com.bottega.photostock.sales.model.money.Money;
import pl.com.bottega.photostock.sales.model.product.Picture;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JDBCClientRepositoryTest {


    public static void main(String[] args) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/photostock");
        ClientRepository clientRepository = new JDBCClientRepository(c);
        ProductRepository productRepository = new JDBCProductRepository(c, clientRepository);
        Client client = clientRepository.get("200");
        String[] tags = {"furki"};
        List<Product> products = productRepository.find(client, "", tags, Money.valueOf(10), Money.valueOf(100000), false);
        for (Product p : products) {
            printProduct(p);
        }
    }

    private static void printProduct(Product product) {
        System.out.println(product.getNumber() + " " + product.getName());
    }
}
