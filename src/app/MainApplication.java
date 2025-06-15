package app;

import model.*;
import service.*;
import repository.*;
import ui.ConsoleUI;
import util.ConsoleUtil;

public class MainApplication {

    public static void main(String[] args) {
        System.out.println("매출 관리 프로그램을 시작합니다...");

        // --- Repository 생성 및 파일 로드 ---
        ProductRepository productRepository = new ProductRepository();
        productRepository.setFilename("src/data/products.txt");
        productRepository.loadFromFile();

        CustomerRepository customerRepository = new CustomerRepository();
        customerRepository.setFilename("src/data/customers.txt");
        customerRepository.loadFromFile();

        EmployeeRepository employeeRepository = new EmployeeRepository();
        employeeRepository.setFilename("src/data/employees.txt");
        employeeRepository.loadFromFile();

        // --- Manager/Service에 Repository 주입 ---
        ProductManager productManager = new ProductManager(productRepository);
        CustomerService customerService = new CustomerService(customerRepository);
        EmployeeManager employeeManager = new EmployeeManager(employeeRepository);

        // --- ReceiptRepository는 ProductManager를 주입받음 ---
        ReceiptRepository receiptRepository = new ReceiptRepository(productManager);
        receiptRepository.setFilename("src/data/receipts.txt");
        receiptRepository.loadFromFile();

        ReceiptManager receiptManager = new ReceiptManager(productManager, customerService, receiptRepository);
        ReportManager reportManager = new ReportManager(receiptManager);

        // --- UI 생성 및 시작 ---
        ConsoleUI consoleUI = new ConsoleUI(
                productManager, receiptManager, employeeManager, customerService, reportManager
        );

        consoleUI.displayMainMenu();

        // --- 프로그램 종료 처리 ---
        ConsoleUtil.closeScanner();
        System.out.println("매출 관리 프로그램을 종료합니다.");
    }
}
