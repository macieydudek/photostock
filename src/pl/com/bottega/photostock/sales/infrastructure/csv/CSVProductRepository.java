package pl.com.bottega.photostock.sales.infrastructure.csv;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.money.Money;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import java.util.List;

public class CSVProductRepository implements ProductRepository{


    private String folderPath, path, tmpPath;

    public CSVProductRepository(String folderPath) {

        this.folderPath = folderPath;
        this.path = path + "products.csv";
        this.tmpPath = path + ".tmp";
    }

    @Override
    public Product get(String number) {
        return null;
    }

    @Override
    public void put(Product product) {

    }

    @Override
    public List<Product> find(Client client, String nameQuery, String[] tags, Money priceFrom, Money priceTo, boolean onlyActive) {
        return null;
    }
}
