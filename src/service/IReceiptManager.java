// IReceiptManager.java
package service;

import model.Customer;
import model.PurchaseItem;
import model.Receipt;
import java.time.LocalDate;
import java.util.List;

public interface IReceiptManager {
    boolean startSale(Customer customerInput);
    boolean addItemToSale(PurchaseItem item);
    boolean setPaymentMethodForSale(String paymentMethod);
    Receipt finalizeSale(String paymentMethod, Customer customer);
    void cancelSale();
    List<Receipt> getAllReceipts();
    List<Receipt> getReceiptsByDate(LocalDate date);
    List<Receipt> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate);
}
