// IEmployeeManager.java
package service;

import model.Employee;
import java.util.List;
import java.util.Optional;

public interface IEmployeeManager {
    void addEmployee(Employee employee);
    Optional<Employee> findEmployeeByID(String id);
    Optional<Employee> findEmployeeByName(String name);
    List<Employee> getAllEmployees();
    boolean updateEmployee(Employee employeeToUpdate);
    boolean deleteEmployee(String name);
    boolean deleteEmployeeById(String employeeId);
}
