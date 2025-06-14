package model;

public class PurchaseItem {
    private Product product;
    private int quantity;

    public PurchaseItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int calculateSubtotal() {
        return product.getSellingPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductID() {
        return product.getId(); // 상품 ID 반환
    }
}
