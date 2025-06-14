// IProductManager.java
package service;

import model.Product;
import java.util.List;
import java.util.Optional;

public interface IProductManager {
    void addProduct(Product product);
    Optional<Product> findProductByID(String id);
    Optional<Product> findProductByName(String name);
    List<Product> getAllProducts();
    boolean updateProduct(Product productToUpdate);
    boolean deleteProduct(Product product);
    boolean deleteProductById(String productId);
    boolean updateStock(String id, int changeInQuantity);
    List<Product> checkLowStock(int threshold);
}
