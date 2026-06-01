package com.shopmanagement.dao;

import com.shopmanagement.model.Category;
import java.util.List;

public interface CategoryDAO {
    List<Category> findAll();
    Category findById(int id);
    boolean save(Category category);
    boolean update(Category category);
    boolean delete(int id);
}
