package repository;

import model.Employee;
import java.io.*;
import java.util.*;

public class EmployeeRepository {
    private List<Employee> employees = new ArrayList<>();
    private String filename = "../data/employees.txt";

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void loadFromFile() {
        employees.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 3) {
                    employees.add(new Employee(tokens[0], tokens[1], tokens[2]));
                }
            }
        } catch (Exception e) {
            System.err.println("직원 데이터 로드 오류: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Employee e : employees) {
                bw.write(String.join(",", e.getName(), e.getContactNumber(), e.getWageInfo()));
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("직원 데이터 저장 오류: " + e.getMessage());
        }
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        saveToFile();
    }

    public void removeEmployee(String name) {
        employees.removeIf(e -> e.getName().equals(name));
        saveToFile();
    }

    public void updateEmployee(Employee updated) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getName().equals(updated.getName())) {
                employees.set(i, updated);
                break;
            }
        }
        saveToFile();
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
