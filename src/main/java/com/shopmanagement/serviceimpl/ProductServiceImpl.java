package com.shopmanagement.serviceimpl;

import com.shopmanagement.dao.ProductDAO;
import com.shopmanagement.daoimpl.ProductDAOImpl;
import com.shopmanagement.model.Product;
import com.shopmanagement.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public List<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public Product findById(int id) {
        return productDAO.findById(id);
    }

    @Override
    public List<Product> search(String keyword, int categoryId) {
        return productDAO.search(keyword, categoryId);
    }

    @Override
    public boolean save(Product product) {
        return productDAO.save(product);
    }

    @Override
    public boolean update(Product product) {
        return productDAO.update(product);
    }

    @Override
    public boolean delete(int id) {
        return productDAO.delete(id);
    }

    @Override
    public boolean updateStock(int productId, int quantity) {
        return productDAO.updateStock(productId, quantity);
    }
}
