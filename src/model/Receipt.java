package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import util.ConsoleUtil;

import java.util.ArrayList;

// 판매 거래(영수증) 정보를 나타내는 클래스
public class Receipt {
    static int count = 0; // 영수증 ID 생성용 카운터
    private String receiptID;
    private LocalDateTime timestamp;
    private List<PurchaseItem> items;
    private int totalAmount;
    private String paymentMethod; // 결제 방법
    private Customer customer;

    public Receipt(List<PurchaseItem> items, String paymentMethod, Customer customer) {
        this.receiptID = "R" + (++count); // 영수증 ID 생성 (R1, R2, ...)
        this.timestamp = LocalDateTime.now();
        this.items = items;
        this.totalAmount = calculateTotalAmount();
        this.paymentMethod = paymentMethod;
        this.customer = customer;
    }

    public Receipt(String timeStamp, String payMentMethod, String customerID) {
        this.receiptID = "R" + (++count); // 영수증 ID 생성 (R1, R2, ...)
        this.timestamp = LocalDateTime.parse(timeStamp);
        this.items = new ArrayList<>(); // 구매 항목은 나중에 추가
        this.totalAmount = 0; // 초기 총액은 0
        this.paymentMethod = payMentMethod;
        this.customer = new Customer(customerID, ""); // 고객 정보는 나중에 설정
    }

    public void printReceipt() {
        // 날짜/시간 포맷 정의
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n========================================");
        System.out.println("             영수증 (RECEIPT)");
        System.out.println("========================================");
        System.out.println("영수증 번호: " + this.receiptID);
        System.out.println("발행 시각: " + this.timestamp.format(dtf));

        // 고객 정보 출력 (null 체크)
        if (this.customer != null) {
            System.out.println("고객명: " + this.customer.getName() + " (" + this.customer.getContactNumber() + ")");
        } else {
            System.out.println("고객명: 비회원");
        }
        if (this.paymentMethod == "1")
            this.paymentMethod = "카드";
        else if (this.paymentMethod == "2")
            this.paymentMethod = "현금";
        
        System.out.println("결제 수단: " + this.paymentMethod);
        System.out.println("----------------------------------------");
        // 컬럼 헤더 출력 (정렬 맞춤)
        System.out.printf("%-18s %5s %12s\n", "상품명", "수량", "금액"); // 간격 조정
        System.out.println("----------------------------------------");

        // 구매 항목 출력 (null 및 empty 체크)
        if (this.items != null && !this.items.isEmpty()) {
            for (PurchaseItem item : this.items) {
                // 각 항목 정보 출력 (정렬 및 포맷팅 적용)
                System.out.printf("%-18s %5d %12s\n",
                                  item.getProduct().getName(), // PurchaseItem이 Product를 가지고 있다고 가정
                                  item.getQuantity(),
                                  // ConsoleUtil 사용하여 금액 포맷팅 (int -> long 또는 double로 자동 변환됨)
                                  ConsoleUtil.formatCurrency(item.calculateSubtotal()));
            }
        } else {
            System.out.println(" 구매 내역 없음");
        }
        System.out.println("----------------------------------------");
        // 총 합계 출력 (정렬 및 포맷팅 적용)
        System.out.printf("%-23s %12s\n", "총 합계:", ConsoleUtil.formatCurrency(this.totalAmount));
        System.out.println("========================================");
    }

    public int calculateTotalAmount() {
        int sum = 0;
        for (PurchaseItem item : items) {
            sum += item.calculateSubtotal(); // 각 항목의 소계 계산
        }
        return sum; // 총액 반환
    }

    public String getID() {
        return receiptID; // 영수증 ID 반환
    }

    public LocalDate getDate() {
        return timestamp.toLocalDate(); // 영수증 날짜 반환
    }

    public String getReceiptID() {
        return receiptID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<PurchaseItem> getItems() { 
        return new ArrayList<>(items);
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Customer getCustomer() {
        return customer;
    }
}