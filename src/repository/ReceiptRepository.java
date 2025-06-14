package repository;

import model.Receipt;
import model.PurchaseItem;
import model.Customer;
import model.Product;
import service.ProductManager;

import java.io.*;
import java.util.*;

public class ReceiptRepository {
    private List<Receipt> receipts = new ArrayList<>();
    private String filename = "../data/receipts.txt";
    private ProductManager productManager; // 상품ID로 Product를 찾기 위해 필요

    public ReceiptRepository(ProductManager productManager) {
        this.productManager = productManager;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    // 파일에서 영수증 목록 불러오기
    public void loadFromFile() {
        receipts.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 예시: 영수증ID|timestamp|결제수단|고객이름|고객연락처|상품1ID,수량;상품2ID,수량;...|총액
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    String receiptID = parts[0];
                    String timestamp = parts[1];
                    String paymentMethod = parts[2];
                    String customerName = parts[3];
                    String customerContact = parts[4];
                    String itemsStr = parts[5];
                    int totalAmount = Integer.parseInt(parts[6]);
                    // PurchaseItem 파싱
                    List<PurchaseItem> items = new ArrayList<>();
                    if (!itemsStr.trim().isEmpty()) {
                        String[] itemTokens = itemsStr.split(";");
                        for (String itemToken : itemTokens) {
                            String[] itemParts = itemToken.split(",");
                            if (itemParts.length == 2) {
                                String productId = itemParts[0];
                                int quantity = Integer.parseInt(itemParts[1]);
                                // ProductManager에서 Product를 찾아서 PurchaseItem 생성
                                Product product = productManager.findProductByID(productId).orElse(
                                        new Product(productId, "", "", 0, 0, 0)
                                );
                                items.add(new PurchaseItem(product, quantity));
                            }
                        }
                    }
                    Customer customer = new Customer(customerName, customerContact);
                    Receipt receipt = new Receipt(items, paymentMethod, customer);
                    // 필요하다면 receiptID, timestamp, totalAmount를 직접 세팅
                    receipts.add(receipt);
                }
            }
        } catch (FileNotFoundException e) {
            // 파일 없으면 새로 시작
        } catch (Exception e) {
            System.err.println("영수증 데이터 로드 오류: " + e.getMessage());
        }
    }

    // 파일에 영수증 목록 저장
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Receipt r : receipts) {
                // 상품목록: 상품ID,수량;상품ID,수량;...
                StringBuilder itemsStr = new StringBuilder();
                for (PurchaseItem item : r.getItems()) {
                    if (itemsStr.length() > 0) itemsStr.append(";");
                    itemsStr.append(item.getProduct().getId()).append(",").append(item.getQuantity());
                }
                bw.write(String.join("|",
                        r.getReceiptID(),
                        r.getTimestamp().toString(),
                        r.getPaymentMethod(),
                        r.getCustomer() != null ? r.getCustomer().getName() : "",
                        r.getCustomer() != null ? r.getCustomer().getContactNumber() : "",
                        itemsStr.toString(),
                        String.valueOf(r.getTotalAmount())
                ));
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("영수증 데이터 저장 오류: " + e.getMessage());
        }
    }

    public void addReceipt(Receipt receipt) {
        receipts.add(receipt);
        saveToFile();
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }
}
