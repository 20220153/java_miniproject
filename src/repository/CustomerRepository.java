package repository;

import model.Customer;
import java.io.*;
import java.util.*;

public class CustomerRepository {
    private List<Customer> customers = new ArrayList<>();
    private String filename = "../data/customers.txt";

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void loadFromFile() {
        customers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 2) {
                    customers.add(new Customer(tokens[0], tokens[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("고객 데이터 로드 오류: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Customer c : customers) {
                bw.write(String.join(",", c.getName(), c.getContactNumber()));
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("고객 데이터 저장 오류: " + e.getMessage());
        }
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveToFile();
    }

    public void removeCustomer(String name) {
        customers.removeIf(c -> c.getName().equals(name));
        saveToFile();
    }

    public void updateCustomer(Customer updated) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getName().equals(updated.getName())) {
                customers.set(i, updated);
                break;
            }
        }
        saveToFile();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void removeCustomerByID(String id) {
    customers.removeIf(c -> c.getID().equals(id));
    saveToFile();
}

}
