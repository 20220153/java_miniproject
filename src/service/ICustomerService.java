// ICustomerService.java
package service;

import model.Customer;
import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    void addCustomer(Customer customer);
    Optional<Customer> findCustomer(String name, String contactNumber);
    Optional<Customer> findCustomerByID(String customerId);
    List<Customer> getAllCustomers();
    boolean updateCustomer(Customer customerToUpdate);
}
