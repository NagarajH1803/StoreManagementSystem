package com.shopmanagement.serviceimpl;

import com.shopmanagement.dao.UserDAO;
import com.shopmanagement.daoimpl.UserDAOImpl;
import com.shopmanagement.model.User;
import com.shopmanagement.service.UserService;
import com.shopmanagement.util.PasswordUtil;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User findById(int id) {
        return userDAO.findById(id);
    }

    @Override
    public boolean register(User user, String password) {
        // Check if username already exists
        if (userDAO.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(PasswordUtil.hashPassword(password));
        return userDAO.save(user);
    }

    @Override
    public boolean update(User user) {
        return userDAO.update(user);
    }

    @Override
    public boolean delete(int id) {
        return userDAO.delete(id);
    }

    @Override
    public boolean updatePassword(int userId, String hashedPassword) {
        return userDAO.updatePassword(userId, hashedPassword);
    }
}
