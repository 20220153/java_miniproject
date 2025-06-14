package service;

import model.Customer;
import repository.CustomerRepository;
import java.util.*;

public class CustomerService implements ICustomerService {
    private Map<String, Customer> customersMap = new HashMap<>();
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository repository) {
        this.customerRepository = repository;
        // 파일에서 로드된 리스트를 Map으로 변환
        List<Customer> initialCustomers = repository.getCustomers();
        if (initialCustomers != null) {
            for (Customer customer : initialCustomers) {
                if (customer != null && customer.getID() != null) {
                    this.customersMap.put(customer.getID(), customer);
                }
            }
        }
    }

    @Override
    public void addCustomer(Customer customer) {
        if (customer != null && customer.getID() != null) {
            customersMap.put(customer.getID(), customer);
            // 파일에도 추가
            customerRepository.addCustomer(customer);
        }
    }

    @Override
    public Optional<Customer> findCustomer(String name, String contactNumber) {
        if (name == null || contactNumber == null) return Optional.empty();
        return customersMap.values().stream()
                .filter(c -> name.equals(c.getName()) && contactNumber.equals(c.getContactNumber()))
                .findFirst();
    }

    @Override
    public Optional<Customer> findCustomerByID(String customerId) {
        if (customerId == null) return Optional.empty();
        return Optional.ofNullable(customersMap.get(customerId));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customersMap.values());
    }

    @Override
    public boolean updateCustomer(Customer customerToUpdate) {
        if (customerToUpdate == null || customerToUpdate.getID() == null) return false;
        Customer existingCustomer = customersMap.get(customerToUpdate.getID());
        if (existingCustomer != null) {
            existingCustomer.setName(customerToUpdate.getName());
            existingCustomer.setContactNumber(customerToUpdate.getContactNumber());
            // 파일에도 반영
            customerRepository.updateCustomer(customerToUpdate);
            return true;
        }
        return false;
    }

    public void removeCustomer(String customerId) {
        customersMap.remove(customerId);
        customerRepository.removeCustomerByID(customerId);
    }
}
