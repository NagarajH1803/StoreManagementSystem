<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - Shop Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-icon"><i class="fas fa-store"></i></div>
            <div>
                <h4>ShopMS</h4>
                <small>Management System</small>
            </div>
        </div>
        <ul class="sidebar-menu">
            <li class="menu-label">Navigation</li>

            <c:if test="${sessionScope.role == 'MANAGER'}">
                <li><a href="${pageContext.request.contextPath}/dashboard" id="nav-dashboard"><i class="fas fa-chart-pie"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/products" id="nav-products"><i class="fas fa-box"></i> Products</a></li>
                <li><a href="${pageContext.request.contextPath}/orders" id="nav-orders"><i class="fas fa-shopping-bag"></i> Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/users" id="nav-users"><i class="fas fa-users"></i> Users</a></li>
            </c:if>

            <c:if test="${sessionScope.role == 'SHOPKEEPER'}">
                <li><a href="${pageContext.request.contextPath}/products" id="nav-shop"><i class="fas fa-store"></i> Shop</a></li>
                <li>
                    <a href="${pageContext.request.contextPath}/cart" id="nav-cart">
                        <i class="fas fa-shopping-cart"></i> Cart
                        <c:if test="${sessionScope.cartCount != null && sessionScope.cartCount > 0}">
                            <span class="badge">${sessionScope.cartCount}</span>
                        </c:if>
                    </a>
                </li>
                <li><a href="${pageContext.request.contextPath}/orders" id="nav-orders"><i class="fas fa-receipt"></i> My Orders</a></li>
            </c:if>

            <li class="menu-label" style="margin-top: 20px;">Account</li>
            <li><a href="${pageContext.request.contextPath}/logout" id="nav-logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Navbar -->
        <div class="top-navbar">
            <div class="page-title">
                ${param.title}
                <small>${param.subtitle}</small>
            </div>
            <div class="user-menu">
                <div class="user-info">
                    <div class="name">${sessionScope.fullName}</div>
                    <div class="role">${sessionScope.role}</div>
                </div>
                <div class="user-avatar">
                    ${sessionScope.fullName.substring(0,1)}
                </div>
            </div>
        </div>

        <div class="content-wrapper">
