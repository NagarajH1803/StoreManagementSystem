<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#0A0A0A">
    <title>Shop Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="landing-page">
        <div class="landing-content">
            <div class="brand-icon-lg"><i class="fas fa-store"></i></div>
            <h1>Shop <span>Management</span> System</h1>
            <p>Manage your inventory, sales, billing, and analytics effortlessly with our modern management platform.</p>
            <div class="deco-line"></div>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                <i class="fas fa-sign-in-alt"></i> Login to System
            </a>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
