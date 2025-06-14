package model;


public class Product {
    private String id;
    private String name;
    private String category;
    private int costPrice;
    private int sellingPrice;
    private int stockQuantity;

    public Product(String id, String name, String category, int costPrice, int sellingPrice, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    public void updateStock(int change) {
        this.stockQuantity += change;
    }

    public boolean isEmpty() {
        if (this.stockQuantity <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCostPrice(int costPrice) {
        this.costPrice = costPrice;
    }

    public void setSellingPrice(int sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

}