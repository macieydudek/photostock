package pl.com.bottega.photostock.sales.infrastructure.csv;

import com.sun.deploy.util.StringUtils;
import pl.com.bottega.photostock.sales.infrastructure.memory.InMemoryProductRepository;
import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.client.ClientRepository;
import pl.com.bottega.photostock.sales.model.lightbox.LightBox;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;
import pl.com.bottega.photostock.sales.model.product.Product;
import pl.com.bottega.photostock.sales.model.product.ProductRepository;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CSVLightBoxRepository implements LightBoxRepository{

    private String folderPath, path, tmpPath;

    private ProductRepository productRepository;

    public CSVLightBoxRepository(String folderPath, ProductRepository productRepository) {
        this.folderPath = folderPath;
        this.path = folderPath + File.separator + "lighboxes.csv";
        this.productRepository = productRepository;
        this.tmpPath = path + ".tmp";
    }

    @Override
    public void put(LightBox l) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
            List<String> listOfProductNumbers = getListOfProductNumbers(l);
            String listOfProducts = StringUtils.join(listOfProductNumbers, "|");
            String [] components = {
                    l.getOwner().getNumber(),
                    l.getName(),
                    listOfProducts
            };
            pw.println(StringUtils.join(Arrays.asList(components), ","));
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Collection<LightBox> getFor(Client client) {
        Collection<LightBox> lightBoxexOfClient = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String clientNumber = client.getNumber();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(clientNumber)) {
                    String [] components = line.split(",");
                    LightBox lightBox = new LightBox(client, components[1], getListOfProducts(components[2]));
                    lightBoxexOfClient.add(lightBox);
                }
            }
            return lightBoxexOfClient;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }


    @Override
    public LightBox findLightBox(Client client, String lightBoxName) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            String key = client.getNumber() + "," + lightBoxName;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key)) {
                    String[] components = line.split(",");
                    if (components.length == 2) {
                        return new LightBox(client, lightBoxName);
                    }
                    return new LightBox(client, lightBoxName, getListOfProducts(components[2]));
                }
            }
            return null;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Collection<String> getLightBoxNames(Client client) {
        Collection<String> lightBoxNames = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String clientNumber = client.getNumber();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(clientNumber)) {
                    String[]  components = line.split(",");
                    lightBoxNames.add(components[1]);
                }
            }
            return lightBoxNames;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    public static void main(String[] args) {
        LightBoxRepository csvLightBox = new CSVLightBoxRepository("/Users/maciekdudek/Desktop/photostockData", new InMemoryProductRepository());
        ClientRepository csvClient = new CSVClientRepository("/Users/maciekdudek/Desktop/photostockData");
        ProductRepository productRepository = new InMemoryProductRepository();
        LightBox l1 = new LightBox(csvClient.get("100"), "test1");
        LightBox l2 = new LightBox(csvClient.get("100"), "test2");
        l1.add(productRepository.get("1"));
        l1.add(productRepository.get("2"));
        l1.add(productRepository.get("3"));
        l1.add(productRepository.get("4"));
        csvLightBox.put(l1);
        csvLightBox.put(l2);
        l1 = null;
        System.out.println((l1 == null));
        Client c = csvClient.get("100");
        l1 = csvLightBox.findLightBox(c, "test1");
        System.out.println(l1 == null);
        for (Product p : l1) {
            System.out.println(p.getNumber() + "|" + p.getName() + "|" + p.calculatePrice(l1.getOwner()));
        }



    }

    private List<String> getListOfProductNumbers(LightBox l) {
        List<String> listOfProductNumbers = new LinkedList<>();
        for (Product product : l) {
            listOfProductNumbers.add(product.getNumber());
        }
        return listOfProductNumbers;
    }

    private Collection<Product> getListOfProducts(String productNumbers) {
        List<Product> listOfProducts = new LinkedList<>();
        String[] elements = productNumbers.split("\\|");
        for (String number : elements) {
            Product product = productRepository.get(number);
            listOfProducts.add(product);
        }
        return listOfProducts;
    }
}
