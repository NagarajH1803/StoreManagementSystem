package com.shopmanagement.dao;

import com.shopmanagement.model.Product;
import java.util.List;

public interface ProductDAO {
    List<Product> findAll();
    Product findById(int id);
    List<Product> search(String keyword, int categoryId);
    List<Product> findByCategory(int categoryId);
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(int id);
    boolean updateStock(int productId, int quantity);
}
