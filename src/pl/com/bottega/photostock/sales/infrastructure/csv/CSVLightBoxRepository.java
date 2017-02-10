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
        Client owner = l.getOwner();
        boolean isNewLightBoxFroClient = true;
        ensureCSVExist();
        try (   BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] attributes = line.split(",");
                String numberofClient = attributes[0];
                if(numberofClient.equals(owner.getNumber()) && l.getName().equals(attributes[1])){
                    writeLightBox(l, pw);
                }
            }
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

    private void writeLightBox(LightBox l, PrintWriter pw) {
        Collection<String> productNumbers = new LinkedList<>();
        for (Product p : l) {

        }
    }

    private void ensureCSVExist() {
        try {
            new File(path).createNewFile();
        } catch (IOException e) {
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

    @Override
    public void updateLightBox(LightBox lightBox) {

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
