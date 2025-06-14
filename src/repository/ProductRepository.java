package repository;

import model.Product;
import java.io.*;
import java.util.*;

public class ProductRepository {
    private List<Product> products = new ArrayList<>();
    private String filename = "../data/products.txt";

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void loadFromFile() {
        products.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 6) {
                    products.add(new Product(
                        tokens[0], tokens[1], tokens[2],
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Integer.parseInt(tokens[5])
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("상품 데이터 로드 오류: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Product p : products) {
                bw.write(String.join(",", p.getId(), p.getName(), p.getCategory(),
                        String.valueOf(p.getCostPrice()), String.valueOf(p.getSellingPrice()), String.valueOf(p.getStockQuantity())));
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("상품 데이터 저장 오류: " + e.getMessage());
        }
    }

    public void addProduct(Product product) {
        products.add(product);
        saveToFile();
    }

    public void removeProduct(String id) {
        products.removeIf(p -> p.getId().equals(id));
        saveToFile();
    }

    public void updateProduct(Product updated) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updated.getId())) {
                products.set(i, updated);
                break;
            }
        }
        saveToFile();
    }

    public List<Product> getProducts() {
        return products;
    }
}
