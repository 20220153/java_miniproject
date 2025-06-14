package service;

import model.Customer;
import model.Product;
import model.PurchaseItem;
import model.Receipt;
import repository.ReceiptRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// 영수증(판매 기록) 관리 및 생성 로직 처리
public class ReceiptManager implements IReceiptManager {
    private List<Receipt> receipts;         // 완료된 영수증 목록 (메모리)
    private ProductManager productManager;
    private CustomerService customerService;
    private ReceiptRepository receiptRepository;

    // --- 현재 진행 중인 판매 상태 저장 변수 ---
    private Customer currentCustomer;
    private List<PurchaseItem> currentItems;
    private boolean saleInProgress;
    // ---------------------------------------

    // 생성자: Repository, Manager, Service를 외부에서 주입받음
    public ReceiptManager(ProductManager productManager, CustomerService customerService, ReceiptRepository receiptRepository) {
        this.productManager = Objects.requireNonNull(productManager, "ProductManager는 null일 수 없습니다.");
        this.customerService = Objects.requireNonNull(customerService, "CustomerService는 null일 수 없습니다.");
        this.receiptRepository = Objects.requireNonNull(receiptRepository, "ReceiptRepository는 null일 수 없습니다.");
        // 파일에서 영수증 목록 불러오기
        this.receipts = new ArrayList<>(receiptRepository.getReceipts());
        resetSaleState(); // 초기 상태 설정
    }

    // --- 상태 초기화 헬퍼 메서드 ---
    private void resetSaleState() {
        this.currentCustomer = null;
        this.currentItems = new ArrayList<>(); // 새 리스트로 초기화
        this.saleInProgress = false;
    }

    // ---판매 시작 및 관리 메서드---
    public boolean startSale(Customer customerInput) {
        if (saleInProgress) {
            System.err.println("오류: 이미 다른 판매가 진행 중입니다. 현재 판매를 완료하거나 취소해주세요.");
            return false;
        }
        this.currentCustomer = handleCustomer(customerInput);
        this.currentItems.clear();
        this.saleInProgress = true;
        System.out.println("정보: 새로운 판매 시작.\n고객: " + currentCustomer.getName());
        System.out.println("\n========= 상품 선택 ==========\n");
        return true;
    }

    // --- 상품 추가 메서드 ---
    public boolean addItemToSale(PurchaseItem item) {
        System.out.println("\n========= 상품 선택 ==========\n");
        if (!saleInProgress) {
            System.err.println("오류: 판매가 시작되지 않았습니다. startSale()을 먼저 호출해주세요.");
            return false;
        }
        if (item.getProduct() == null || item.getQuantity() <= 0) {
            System.err.println("오류: 상품 정보나 수량이 유효하지 않습니다.");
            return false;
        }
        if (item.getProduct().getStockQuantity() < item.getQuantity()) {
            System.err.println("오류: 상품 '" + item.getProduct().getName() + "' 재고 부족 (요청: " + item.getQuantity() + ", 현재: " + item.getProduct().getStockQuantity() + ")");
            return false;
        }
        PurchaseItem newItem = new PurchaseItem(item.getProduct(), item.getQuantity());
        this.currentItems.add(newItem);
        System.out.println("정보: 상품 '" + item.getProduct().getName() + "' " + item.getQuantity() + "개 추가됨.");
        return true;
    }

    public boolean setPaymentMethodForSale(String paymentMethod) {
        if (!saleInProgress) {
            System.err.println("오류: 판매가 시작되지 않았습니다.");
            return false;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            System.err.println("오류: 유효한 결제 수단을 입력해주세요.");
            return false;
        }
        System.out.println("정보: 결제 방법 '" + paymentMethod + "'");
        return true;
    }

    /**
     * 현재 진행 중인 판매를 최종 완료하고 영수증을 생성합니다.
     * 최종 재고 확인 및 차감을 수행합니다.
     * @return 생성된 영수증 객체 , 실패 시 null 반환
     */
    public Receipt finalizeSale(String paymentMethod, Customer customer) {
        if (!saleInProgress) {
            System.err.println("오류: 완료할 판매가 진행 중이지 않습니다.");
            return null;
        }
        if (currentItems.isEmpty()) {
            System.err.println("오류: 판매 항목이 없습니다. 판매를 취소합니다.");
            resetSaleState();
            return null;
        }
        if (paymentMethod == null) {
            System.err.println("오류: 결제 방법이 설정되지 않았습니다. 판매를 취소합니다.");
            resetSaleState();
            return null;
        }

        // 최종 재고 확인 및 차감
        if (!checkAndDeductStock(currentItems)) {
            System.err.println("오류: 재고 확인 또는 차감 실패! 판매를 취소합니다.");
            resetSaleState();
            return null;
        }

        // 영수증 생성 및 파일 저장
        Receipt newReceipt = new Receipt(currentItems, paymentMethod, customer);
        this.receipts.add(newReceipt);
        receiptRepository.addReceipt(newReceipt); // 파일에도 즉시 저장
        System.out.println("정보: 영수증 ID '" + newReceipt.getReceiptID() + "' 생성 및 저장 완료.");

        // 성공적으로 완료 후 상태 초기화
        resetSaleState();
        return newReceipt;
    }

    /**
     * 현재 진행 중인 판매를 취소하고 상태를 초기화합니다.
     */
    public void cancelSale() {
        if (saleInProgress) {
            System.out.println("정보: 진행 중인 판매를 취소합니다.");
            resetSaleState();
        } else {
            System.out.println("정보: 현재 진행 중인 판매가 없습니다.");
        }
    }

    // --- Private Helper Methods ---

    private Customer handleCustomer(Customer customerInput) {
        Optional<Customer> existingCustomer = customerService.findCustomer(customerInput.getName(), customerInput.getContactNumber());
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        } else {
            customerService.addCustomer(customerInput);
            return customerInput;
        }
    }

    private boolean checkAndDeductStock(List<PurchaseItem> items) {
        for (PurchaseItem item : items) {
            Optional<Product> productOpt = productManager.findProductByID(item.getProduct().getId());
            if (!productOpt.isPresent()) {
                System.err.println("오류: 상품 없음 - ID '" + item.getProduct().getId() + "' 상품을 찾을 수 없습니다.");
                return false;
            }
            Product product = productOpt.get();
            if (product.getStockQuantity() < item.getQuantity()) {
                System.err.println("오류: 재고 부족 - 상품 '" + product.getName() + "' (요청: " + item.getQuantity() + ", 현재: " + product.getStockQuantity() + ")");
                return false;
            }
        }
        for (PurchaseItem item : items) {
            productManager.updateStock(item.getProduct().getId(), -item.getQuantity());
        }
        return true;
    }

    // --- 영수증 조회/검색 기능 ---
    public List<Receipt> getAllReceipts() {
        return new ArrayList<>(receipts);
    }

    public List<Receipt> getReceiptsByDate(LocalDate date) {
        return receipts.stream()
                .filter(r -> r.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Receipt> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        return receipts.stream()
                .filter(r -> !r.getDate().isBefore(startDate) && !r.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    // --- 파일 동기화 기능 ---
    public void reloadFromFile() {
        receipts.clear();
        receipts.addAll(receiptRepository.getReceipts());
    }
}
