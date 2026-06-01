package com.shopmanagement.dao;

import com.shopmanagement.model.User;
import java.util.List;

public interface UserDAO {
    User findByUsername(String username);
    User findById(int id);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean delete(int id);
    boolean updatePassword(int userId, String hashedPassword);
}
