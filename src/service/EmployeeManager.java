package service;

import model.Employee;
import repository.EmployeeRepository;
import java.util.*;

public class EmployeeManager implements IEmployeeManager {
    private Map<String, Employee> employeesMap = new HashMap<>();
    private final EmployeeRepository employeeRepository;

    public EmployeeManager(EmployeeRepository repository) {
        this.employeeRepository = repository;
        // 파일에서 로드된 리스트를 Map으로 변환
        List<Employee> initialEmployees = repository.getEmployees();
        if (initialEmployees != null) {
            for (Employee employee : initialEmployees) {
                if (employee != null && employee.getID() != null) {
                    this.employeesMap.put(employee.getID(), employee);
                }
            }
        }
    }

    @Override
    public void addEmployee(Employee employee) {
        if (employee != null && employee.getID() != null) {
            employeesMap.put(employee.getID(), employee);
            employeeRepository.addEmployee(employee); // 파일에도 추가
        }
    }

    @Override
    public Optional<Employee> findEmployeeByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(employeesMap.get(id));
    }

    @Override
    public Optional<Employee> findEmployeeByName(String name) {
        if (name == null) return Optional.empty();
        return employeesMap.values().stream()
                .filter(e -> name.equals(e.getName()))
                .findFirst();
    }

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeesMap.values());
    }

    @Override
    public boolean updateEmployee(Employee employeeToUpdate) {
        if (employeeToUpdate == null || employeeToUpdate.getID() == null) return false;
        Employee existingEmployee = employeesMap.get(employeeToUpdate.getID());
        if (existingEmployee != null) {
            existingEmployee.setName(employeeToUpdate.getName());
            existingEmployee.setContactNumber(employeeToUpdate.getContactNumber());
            existingEmployee.setWageInfo(employeeToUpdate.getWageInfo());
            employeeRepository.updateEmployee(employeeToUpdate); // 파일에도 반영
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEmployee(String name) {
        if (name == null) return false;
        Optional<Employee> employeeOpt = findEmployeeByName(name);
        if (employeeOpt.isPresent()) {
            employeesMap.remove(employeeOpt.get().getID());
            employeeRepository.removeEmployee(name); // 파일에서도 삭제
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEmployeeById(String employeeId) {
        if (employeeId == null) return false;
        Employee removed = employeesMap.remove(employeeId);
        if (removed != null) {
            employeeRepository.removeEmployee(removed.getName()); // 파일에서도 삭제
            return true;
        }
        return false;
    }
}
