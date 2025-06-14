package ui;

import model.Customer;
import model.Employee;
import model.Product;
import model.PurchaseItem;
import model.Receipt;
import service.*;
import util.ConsoleUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// 콘솔 기반 사용자 인터페이스
public class ConsoleUI {
    // 필요한 서비스(매니저)들을 멤버 변수로 가짐 (의존성 주입)
    private final ProductManager productManager;
    private final ReceiptManager receiptManager;
    private final EmployeeManager employeeManager;
    private final CustomerService customerService;
    private final ReportManager reportManager;

    public ConsoleUI(ProductManager productManager, ReceiptManager receiptManager,
                     EmployeeManager employeeManager, CustomerService customerService,
                     ReportManager reportManager) {
        this.productManager = productManager;
        this.receiptManager = receiptManager;
        this.employeeManager = employeeManager;
        this.customerService = customerService;
        this.reportManager = reportManager;
    }



    // 메인 메뉴 표시 및 실행 루프
    public void displayMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== 매출 관리 프로그램 =====");
            System.out.println("      [1]  판       매       ");
            System.out.println("      [2]  상 품 관 리       ");
            System.out.println("      [3]  직 원 관 리       ");
            System.out.println("      [4]  보고서 조회       ");
            System.out.println("      [5]  검       색       ");
            System.out.println("      [0]  종       료       ");
            System.out.println("==============================");

            int choice = ConsoleUtil.readInt("메뉴 선택");

            switch (choice) {
                case 1:
                    handlePOS();
                    break;
                case 2:
                    handleProductMenu();
                    break;
                case 3:
                    handleEmployeeMenu();
                    break;
                case 4:
                    handleReportsMenu();
                    break;
                 case 5:
                    handleSearchMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("프로그램을 종료합니다.");
                    break;
                default:
                    System.out.println("잘못된 메뉴 번호입니다.");
                    break;
            }
        }
    }

    // 1. 판매 처리 핸들러
    private void handlePOS() {
        System.out.println("\n========== 판    매 ==========");
        Customer customer = handleCustomerSelection(); // 고객 선택 및 등록

        // 판매 시작
        boolean selectProduct = true;
        receiptManager.startSale(customer);

        //상품 고르기
        while (selectProduct) {
            System.out.println("상품 선택 (종료 시 0 입력)");
            String productName = ConsoleUtil.readLine("상품 이름");
            if (productName.equals("0")) {
                selectProduct = false; // 상품 선택 종료
                break;
            }
            Optional<Product> productOpt = productManager.findProductByName(productName); // 상품 
            // 상품이 존재하는지 확인
            if (productOpt.isPresent()) { // 상품이 존재하는 경우
                Product product = productOpt.get();
                int quantity = ConsoleUtil.readInt("수량"); // 수량 입력
                if (quantity > 0 && quantity <= product.getStockQuantity()) { // 수량 유효성 검사
                    // 상품 추가
                    PurchaseItem item = new PurchaseItem(product, quantity);
                    receiptManager.addItemToSale(item); // 영수증에 추가
                    System.out.println("상품 '" + product.getName() + "' (" + quantity + "개)가 영수증에 추가되었습니다.");
                } else if (quantity > product.getStockQuantity()) { // 재고 부족
                    System.out.println("재고가 부족합니다. 현재 재고: " + product.getStockQuantity() + "개. " + "부족한 수량: " + (quantity - product.getStockQuantity()) + "개");
                } else { // 수량이 1 이상이어야 함
                    System.out.println("수량은 1 이상이어야 합니다.");
                }
                System.out.println("\n========= 상품 선택 ==========\n");
            } else {
                System.out.println("상품 '" + productName + "'을(를) 찾을 수 없습니다.");
                System.out.println("\n========= 상품 선택 ==========\n");
            }
        }

        // 결제 처리
        System.out.println("\n========= 결제 방법 ==========\n");
        System.out.println("[1] 카드 결제");
        System.out.println("[2] 현금 결제");
        System.out.println("[3] 취소");
        String paymentMethod = ConsoleUtil.readLine("결제 방법 선택");

        boolean paymentDone = false;
        while (!paymentDone) {
            if ("1".equals(paymentMethod)) {
            receiptManager.setPaymentMethodForSale("카드"); // 카드 결제
            Receipt receipt = receiptManager.finalizeSale(paymentMethod, customer); // 판매 완료
            receipt.printReceipt(); // 영수증 출력
            paymentDone = true;
            break;
            } else if ("2".equals(paymentMethod)) {
            receiptManager.setPaymentMethodForSale("현금"); // 현금 결제
            Receipt receipt1 = receiptManager.finalizeSale(paymentMethod, customer); // 판매 완료
            receipt1.printReceipt(); // 영수증 출력
            paymentDone = true;
            break;
            } else if ("3".equals(paymentMethod)) {
            receiptManager.cancelSale(); // 판매 취소
            System.out.println("판매가 취소되었습니다.");
            paymentDone = true;
            break;
            } else {
            System.out.println("잘못된 선택입니다.");
            System.out.println("\n========= 결제 방법 ==========\n");
            paymentMethod = ConsoleUtil.readLine("결제 방법 선택");
            }
        }
    }
    
    // 2. 상품 관리 메뉴 핸들러
    private void handleProductMenu() {
        System.out.println("\n========= 결제 방법 ==========\n");
        System.out.println("\n========= 상품 관리 ==========\n");
        boolean running = true;
        while (running) {
            System.out.println("[1] 상품 목록 조회");
            System.out.println("[2] 상품 추가");
            System.out.println("[3] 상품 수정");
            System.out.println("[4] 상품 삭제");
            System.out.println("[5] 낮은 재고 확인");
            System.out.println("[0] 뒤로 가기");
            System.out.println("==============================");

            int choice = ConsoleUtil.readInt("메뉴 선택");

            switch (choice) {
                case 1:
                    displayProductList(); // 상품 목록 조회
                    break;
                case 2:
                    addProduct(); // 상품 추가
                    break;
                case 3:
                    updateProduct(); // 상품 수정
                    break;
                case 4:
                    deleteProduct(); // 상품 삭제
                    break;
                case 5:
                    checkLowStock(); // 낮은 재고 확인
                    break;
            case 0:
                running = false; // 뒤로 가기
                break;
            default:
                System.out.println("잘못된 메뉴 번호입니다.");
                break;
            }
        }
    }
     // 2.1. 상품 목록 조회
    private void displayProductList() {
        System.out.println("\n===== 상품 목록 =====");
        List<Product> products = productManager.getAllProducts(); // 모든 상품 조회
        if (products.isEmpty()) {
            System.out.println("등록된 상품이 없습니다.");
        } else {
            System.out.printf("%-10s %-20s %-10s %-10s %-10s\n", "ID", "상품명", "카테고리", "원가", "판매가");
            for (Product product : products) {
                System.out.printf("%-10s %-20s %-10s %-10d %-10d\n",
                        product.getId(), product.getName(), product.getCategory(),
                        product.getCostPrice(), product.getSellingPrice());
            }
        }
        System.out.println("==============================");
    }

    
    
     // 2.2. 상품 추가
    private void addProduct() {
        System.out.println("\n===== 상품 추가 =====");
        String id = countProduct(); // 상품 ID 생성
        String name = ConsoleUtil.readLine("상품명");
        String category = ConsoleUtil.readLine("카테고리");
        int costPrice = ConsoleUtil.readInt("원가");
        int sellingPrice = ConsoleUtil.readInt("판매가");
        int stockQuantity = ConsoleUtil.readInt("재고 수량");

        Product newProduct = new Product(id, name, category, costPrice, sellingPrice, stockQuantity); // 신규 상품 객체 생성
        productManager.addProduct(newProduct); // 상품 추가
        System.out.println("상품 '" + name + "'이(가) 추가되었습니다.");
        System.out.println("==============================");
    }

    // 2.3. 상품 수정
    private void updateProduct() {
        System.out.println("\n===== 상품 수정 =====");
        String Pname = ConsoleUtil.readLine("수정할 상품 이름");
        Optional<Product> existingProduct = productManager.findProductByName(Pname); // 상품 조회
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            System.out.println("현재 상품 정보: " + product.getName() + ", " + product.getCategory() + ", 원가 : " + product.getCostPrice() + ", 판매가 : " + product.getSellingPrice() + ", 재고 : " + product.getStockQuantity());
            String name = ConsoleUtil.readLine("새로운 상품명 (공백 시 기존 이름 유지)");
            String category = ConsoleUtil.readLine("새로운 카테고리 (공백 시 기존 카테고리 유지)");
            int costPrice = ConsoleUtil.readInt("새로운 원가 (필수입력)");
            int sellingPrice = ConsoleUtil.readInt("새로운 판매가 (필수입력)");
            int stockQuantity = ConsoleUtil.readInt("새로운 재고 수량 (필수입력)");

            if (name.isEmpty()) {
                name = product.getName(); // 기존 이름 유지
            }
            if (category.isEmpty()) {
                category = product.getCategory(); // 기존 카테고리 유지
            }

            // 상품 정보 수정
            product.setName(name);
            product.setCategory(category);
            product.setCostPrice(costPrice);
            product.setSellingPrice(sellingPrice);
            product.setStockQuantity(stockQuantity);

            System.out.println("상품 '" + name + "'이(가) 수정되었습니다.");
        } else {
            System.out.println("상품 '" + Pname + "'을(를) 찾을 수 없습니다.");
        }
        System.out.println("==============================");
    }

     // 2.4. 상품 삭제
    private void deleteProduct() {
        System.out.println("\n===== 상품 삭제 =====");
        String Pname = ConsoleUtil.readLine("삭제할 상품 이름");
        Optional<Product> existingProduct = productManager.findProductByName(Pname); // 상품 조회
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            productManager.deleteProduct(product); // 상품 삭제
            System.out.println("상품 '" + Pname + "'이(가) 삭제되었습니다.");
        } else {
            System.out.println("상품 '" + Pname + "'을(를) 찾을 수 없습니다.");
        }
        System.out.println("==============================");
    }
    
     // 2.5 낮은 재고 확인
    private void checkLowStock() {
        System.out.println("\n===== 낮은 재고 확인 =====");
        int threshold = ConsoleUtil.readInt("재고 수량 기준"); // 기준 재고 수량 입력
        List<Product> lowStockProducts = productManager.checkLowStock(threshold); // 낮은 재고 상품 조회
        if (lowStockProducts.isEmpty()) {
            System.out.println("낮은 재고 상품이 없습니다.");
        } else {
            System.out.printf("%-10s %-20s %-10s %-10s\n", "ID", "상품명", "카테고리", "재고 수량");
            for (Product product : lowStockProducts) {
                System.out.printf("%-10s %-20s %-10s %-10d\n",
                        product.getId(), product.getName(), product.getCategory(), product.getStockQuantity());
            }
        }
        System.out.println("==============================");
    }

     // 2.6 상품 개수 확인
     private String countProduct() {
        System.out.println("\n===== 새 상품 추가 =====");

        // 1. 현재 상품 개수를 가져와 다음 ID 번호 결정
        int currentProductCount = productManager.getAllProducts().size(); // ProductManager에서 현재 목록 크기 확인
        int nextIdNumber = currentProductCount + 1;

        // 2. ID를 "PXXX" 형식으로 포맷팅
        //    String.format("%03d", 숫자)는 숫자를 최소 3자리로 만들고, 빈 자리는 0으로 채웁니다.
        String productId = String.format("P%03d", nextIdNumber);
        System.out.println("생성될 상품 ID: " + productId); // 생성될 ID 미리 보여주기 (선택 사항)
        return productId; // 생성될 ID 반환
    }
    
    // 3. 직원 관리 메뉴 핸들러
    private void handleEmployeeMenu() {
        System.out.println("\n===== 직원관리 =====");
        boolean running = true;
        while (running) {
            System.out.println("\n[1] 직원 목록 조회");
            System.out.println("[2] 직원 추가");
            System.out.println("[3] 직원 수정");
            System.out.println("[4] 직원 삭제");
            System.out.println("[0] 뒤로 가기");

            int choice = ConsoleUtil.readInt("메뉴 선택");

            switch (choice) {
                case 1:
                    displayEmployeeList(); // 직원 목록 조회
                    break;
                case 2:
                    addEmployee(); // 직원 추가
                    break;
                case 3:
                    updateEmployee(); // 직원 수정
                    break;
                case 4:
                    deleteEmployee(); // 직원 삭제
                    break;
                case 0:
                    running = false; // 뒤로 가기
                    break;
                default:
                    System.out.println("잘못된 메뉴 번호입니다.");
                    break;
            }
        }
    }
    
     // 3.1 직원 목록 조회
    private void displayEmployeeList() {
        System.out.println("\n===== 직원 목록 =====");
        List<Employee> employees = employeeManager.getAllEmployees(); // 모든 직원 조회
        if (employees.isEmpty()) {
            System.out.println("등록된 직원이 없습니다.");
        } else {
            System.out.printf("%-10s %-20s\n", "ID", "이름");
            for (Employee employee : employees) {
                System.out.printf("%-10s %-20s\n",
                        employee.getID(), employee.getName());
            }
        }
    }
    
     // 3.2 직원 추가
    private void addEmployee() {
        System.out.println("\n===== 직원 추가 =====");
        String name = ConsoleUtil.readLine("이름");
        String contactNumber = ConsoleUtil.readLine("전화번호");
        String wageInfo = ConsoleUtil.readLine("급여 정보");

        Employee newEmployee = new Employee(name, contactNumber, wageInfo); // 신규 직원 객체 생성
        employeeManager.addEmployee(newEmployee); // 직원 추가
        System.out.println("직원 '" + name + "'이(가) 추가되었습니다.");
    }
    
     // 3.3 직원 수정
    private void updateEmployee() {
        System.out.println("\n===== 직원 수정 =====");
        String Ename = ConsoleUtil.readLine("수정할 직원 이름");
        Optional<Employee> existingEmployee = employeeManager.findEmployeeByName(Ename); // 직원 조회
        if (existingEmployee.isPresent()) {
            System.out.println("현재 직원 정보: " + existingEmployee.get().getName() + ", " + existingEmployee.get().getContactNumber() + ", " + existingEmployee.get().getWageInfo());
            String name = ConsoleUtil.readLine("새로운 이름 (현재: " + existingEmployee.get().getName() + ")");
            String contactNumber = ConsoleUtil.readLine("새로운 전화번호 (현재: " + existingEmployee.get().getContactNumber() + ")");
            String wageInfo = ConsoleUtil.readLine("새로운 급여 정보 (현재: " + existingEmployee.get().getWageInfo() + ")");

            // 직원 정보 수정
            existingEmployee.get().setName(name);
            existingEmployee.get().setContactNumber(contactNumber);
            existingEmployee.get().setWageInfo(wageInfo);

            System.out.println("직원 '" + name + "'이(가) 수정되었습니다.");
        } else {
            System.out.println("해당 직원을 찾을 수 없습니다.");
        }
    }

     // 3.4 직원 삭제
    private void deleteEmployee() {
        System.out.println("\n===== 직원 삭제 =====");
        String name = ConsoleUtil.readLine("삭제할 직원 이름");
        Optional<Employee> existingEmployee = employeeManager.findEmployeeByName(name); // 직원 조회
        if (existingEmployee.isPresent()) {
            employeeManager.deleteEmployee(name); // 직원 삭제
            System.out.println("직원 '" + name + "'이(가) 삭제되었습니다.");
        } else {
            System.out.println("해당 직원을 찾을 수 없습니다.");
        }
    }
    
    // 4. 보고서 조회 메뉴 핸들러
    private void handleReportsMenu() {
        System.out.println("\n===== 보고서 조회 =====");
        boolean running = true;
        while (running) {
            System.out.println("\n[1] 일일 매출 조회");
            System.out.println("[2] 주간 매출 조회");
            System.out.println("[0] 뒤로 가기");

            int choice = ConsoleUtil.readInt("메뉴 선택");

            switch (choice) {
                case 1:
                    viewDailySales(); // 일일 매출 조회
                    break;
                case 2:
                    viewWeeklySales(); // 주간 매출 조회
                    break;
                case 0:
                    running = false; // 뒤로 가기
                    break;
                default:
                    System.out.println("잘못된 메뉴 번호입니다.");
                    break;
            }
        }
    }

     // 4.1 일일 매출 조회
    private void viewDailySales() {
        System.out.println("\n===== 일일 매출 조회 =====");
        LocalDate date = ConsoleUtil.readDate("조회할 날짜"); // 날짜 입력
        reportManager.generateDailyReport(date); // 해당 날짜의 영수증 조회

    }
     
     // 4.2 주간 매출 조회
    private void viewWeeklySales() {
        System.out.println("\n===== 주간 매출 조회 =====");
        LocalDate startDate = ConsoleUtil.readDate("조회할 주의 시작 날짜"); // 시작 날짜 입력
        reportManager.generateWeeklyReport(startDate); // 해당 주의 영수증 조회
    }

    // 5. 검색 메뉴 핸들러
    private void handleSearchMenu() {
        System.out.println("\n===== 검 색 =====");
        boolean running = true;
        while (running) {
            System.out.println("\n[1] 상품 검색");
            System.out.println("[2] 고객 검색");
            System.out.println("[0] 뒤로 가기");

            int choice = ConsoleUtil.readInt("메뉴 선택");

            switch (choice) {
                case 1:
                    searchProduct(); // 상품 검색
                    break;
                case 2:
                    handleCustomerSelection(); // 고객 검색
                    break;
                case 0:
                    running = false; // 뒤로 가기
                    break;
                default:
                    System.out.println("잘못된 메뉴 번호입니다.");
                    break;
            }
        }
    }
    
     // 5.1 상품 검색
    private void searchProduct() {
        System.out.println("\n===== 상품 검색 =====");
        String productName = ConsoleUtil.readLine("상품 이름"); // 상품 이름 입력
        Optional<Product> productOpt = productManager.findProductByName(productName); // 상품 조회
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            System.out.println("상품 정보: " + product.getName() + ", " + product.getCategory() + ", " + product.getCostPrice() + ", " + product.getSellingPrice() + ", " + product.getStockQuantity());
        } else {
            System.out.println("상품 '" + productName + "'을(를) 찾을 수 없습니다.");
        }
    }
    
     // 5.2 고객 검색
     private Customer handleCustomerSelection() {
        System.out.println("\n이름과 전화번호 입력");
        String name = ConsoleUtil.readLine("이름");
        String contactNumber = ConsoleUtil.readLine("전화번호");
        Optional<Customer> customerOpt = customerService.findCustomer(name, contactNumber);
        if (customerOpt.isPresent()) {
            System.out.println(customerOpt.get().getName() + "님 안녕하세요.\n");
            System.out.println("========= 고객 정보 ==========\n");
            return customerOpt.get(); // 기존 고객 반환
        } else {
            System.out.println("일치하는 정보가 없습니다.\n신규 고객에 등록되었습니다.\n");
            System.out.println("========= 고객 정보 ==========\n");
            Customer newCustomer = new Customer(name, contactNumber); // 신규 고객 객체 생성
            customerService.addCustomer(newCustomer); // 신규 고객 추가
            System.out.println("신규 고객 등록 완료: " + newCustomer.getName() + ", " + newCustomer.getContactNumber());
            return newCustomer; // 신규 고객 반환
        }
     }
} 