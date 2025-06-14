package service;

import model.Product;
import repository.ProductRepository;
import java.util.*;

public class ProductManager implements IProductManager {
    private Map<String, Product> productsMap = new HashMap<>();
    private final ProductRepository productRepository;

    public ProductManager(ProductRepository repository) {
        this.productRepository = repository;
        // 파일에서 로드된 리스트를 Map으로 변환
        List<Product> initialProducts = repository.getProducts();
        if (initialProducts != null) {
            for (Product product : initialProducts) {
                if (product != null && product.getId() != null) {
                    this.productsMap.put(product.getId(), product);
                }
            }
        }
    }

    @Override
    public void addProduct(Product product) {
        if (product != null && product.getId() != null) {
            productsMap.put(product.getId(), product);
            productRepository.addProduct(product); // 파일에도 추가
        }
    }

    @Override
    public Optional<Product> findProductByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(productsMap.get(id));
    }

    @Override
    public Optional<Product> findProductByName(String name) {
        if (name == null) return Optional.empty();
        return productsMap.values().stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst();
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(productsMap.values());
    }

    @Override
    public boolean updateProduct(Product productToUpdate) {
        if (productToUpdate == null || productToUpdate.getId() == null) return false;
        Product existingProduct = productsMap.get(productToUpdate.getId());
        if (existingProduct != null) {
            existingProduct.setName(productToUpdate.getName());
            existingProduct.setCategory(productToUpdate.getCategory());
            existingProduct.setCostPrice(productToUpdate.getCostPrice());
            existingProduct.setSellingPrice(productToUpdate.getSellingPrice());
            existingProduct.setStockQuantity(productToUpdate.getStockQuantity());
            productRepository.updateProduct(productToUpdate); // 파일에도 반영
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProduct(Product product) {
        if (product == null || product.getId() == null) return false;
        productsMap.remove(product.getId());
        productRepository.removeProduct(product.getId()); // 파일에서도 삭제
        return true;
    }

    @Override
    public boolean deleteProductById(String productId) {
        if (productId == null) return false;
        productsMap.remove(productId);
        productRepository.removeProduct(productId); // 파일에서도 삭제
        return true;
    }

    @Override
    public boolean updateStock(String id, int changeInQuantity) {
        Optional<Product> productOpt = findProductByID(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.updateStock(changeInQuantity);
            productRepository.updateProduct(product); // 파일에도 반영
            return true;
        }
        return false;
    }

    @Override
    public List<Product> checkLowStock(int threshold) {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : productsMap.values()) {
            if (p.getStockQuantity() < threshold) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }
}
