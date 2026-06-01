package com.shopmanagement.service;

import com.shopmanagement.model.User;
import java.util.List;

public interface UserService {
    User authenticate(String username, String password);
    List<User> findAll();
    boolean register(User user, String password);
    boolean update(User user);
    boolean delete(int id);
    boolean updatePassword(int userId, String hashedPassword);
    User findById(int id);
}
