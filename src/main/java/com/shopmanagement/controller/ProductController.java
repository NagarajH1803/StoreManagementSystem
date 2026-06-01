package com.shopmanagement.controller;

import com.shopmanagement.model.Category;
import com.shopmanagement.model.Product;
import com.shopmanagement.dao.CategoryDAO;
import com.shopmanagement.daoimpl.CategoryDAOImpl;
import com.shopmanagement.service.ProductService;
import com.shopmanagement.serviceimpl.ProductServiceImpl;
import com.shopmanagement.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/products", "/products/add", "/products/update", "/products/delete"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize       = 1024 * 1024 * 5, // 5 MB
    maxRequestSize    = 1024 * 1024 * 10  // 10 MB
)
public class ProductController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Search and filter
        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("category");
        int categoryId = ValidationUtil.parseIntSafe(categoryParam, 0);

        List<Product> products;
        if ((keyword != null && !keyword.isEmpty()) || categoryId > 0) {
            products = productService.search(keyword, categoryId);
        } else {
            products = productService.findAll();
        }

        List<Category> categories = categoryDAO.findAll();

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategory", categoryId);

        // Determine which view to show based on role
        String role = (String) request.getSession().getAttribute("role");
        if ("MANAGER".equals(role)) {
            request.getRequestDispatcher("/WEB-INF/views/manager/manager-home.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/shopkeeper/shop-home.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/products/add".equals(path)) {
            handleAdd(request, response);
        } else if ("/products/update".equals(path)) {
            handleUpdate(request, response);
        } else if ("/products/delete".equals(path)) {
            handleDelete(request, response);
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = ValidationUtil.sanitize(request.getParameter("name"));
        String description = ValidationUtil.sanitize(request.getParameter("description"));
        double price = ValidationUtil.parseDoubleSafe(request.getParameter("price"), 0);
        int stock = ValidationUtil.parseIntSafe(request.getParameter("stock"), 0);
        int catId = ValidationUtil.parseIntSafe(request.getParameter("categoryId"), 0);

        if (!ValidationUtil.isNotBlank(name)) {
            response.sendRedirect(request.getContextPath() + "/products?error=Product+name+is+required");
            return;
        }
        if (price <= 0) {
            response.sendRedirect(request.getContextPath() + "/products?error=Price+must+be+greater+than+zero");
            return;
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        if (catId > 0) product.setCategoryId(catId);

        // Handle image upload
        String imageUrl = handleImageUpload(request);
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }

        productService.save(product);
        response.sendRedirect(request.getContextPath() + "/products?success=Product+added+successfully");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        Product product = productService.findById(id);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        String name = ValidationUtil.sanitize(request.getParameter("name"));
        if (!ValidationUtil.isNotBlank(name)) {
            response.sendRedirect(request.getContextPath() + "/products?error=Product+name+is+required");
            return;
        }

        product.setName(name);
        product.setDescription(ValidationUtil.sanitize(request.getParameter("description")));
        product.setPrice(ValidationUtil.parseDoubleSafe(request.getParameter("price"), product.getPrice()));
        product.setStock(ValidationUtil.parseIntSafe(request.getParameter("stock"), product.getStock()));

        int catId = ValidationUtil.parseIntSafe(request.getParameter("categoryId"), 0);
        if (catId > 0) product.setCategoryId(catId);

        // Handle image upload (keep old if no new image)
        String imageUrl = handleImageUpload(request);
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }

        productService.update(product);
        response.sendRedirect(request.getContextPath() + "/products?success=Product+updated+successfully");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
        if (id > 0) {
            productService.delete(id);
        }
        response.sendRedirect(request.getContextPath() + "/products?success=Product+deleted");
    }

    private String handleImageUpload(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("image");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uniqueName = UUID.randomUUID().toString() + "_" + fileName;
            String uploadPath = getServletContext().getRealPath("/assets/images/products");

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            filePart.write(uploadPath + File.separator + uniqueName);
            return "assets/images/products/" + uniqueName;
        }
        return null;
    }
}
