<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Shop" />
    <jsp:param name="subtitle" value="Browse products and add to cart" />
</jsp:include>

<!-- Search Bar -->
<div class="search-bar">
    <form action="${pageContext.request.contextPath}/products" method="get" class="d-flex gap-2">
        <input type="text" name="keyword" class="form-control" placeholder="Search products by name..." value="${keyword}" style="max-width:400px;">
        <select name="category" class="form-select" style="max-width:200px;">
            <option value="">All Categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.id}" ${selectedCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
        </select>
        <button type="submit" class="btn btn-primary"><i class="fas fa-search me-1"></i>Search</button>
        <c:if test="${not empty keyword || selectedCategory > 0}">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary"><i class="fas fa-times"></i></a>
        </c:if>
    </form>
</div>

<!-- Product Grid -->
<div class="row g-4">
    <c:forEach var="p" items="${products}">
        <div class="col-md-4 col-lg-3">
            <div class="product-card">
                <div class="product-img">
                    <c:choose>
                        <c:when test="${not empty p.imageUrl}">
                            <img src="${pageContext.request.contextPath}/${p.imageUrl}" alt="${p.name}">
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-box-open no-image"></i>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="product-body">
                    <div class="product-category">${p.categoryName != null ? p.categoryName : 'Uncategorized'}</div>
                    <div class="product-name">${p.name}</div>
                    <div class="product-desc">${p.description}</div>
                    <div class="product-footer mb-3">
                        <div class="product-price">&#8377;<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></div>
                        <c:choose>
                            <c:when test="${p.stock == 0}"><span class="stock-badge stock-out">Out of Stock</span></c:when>
                            <c:when test="${p.stock < 10}"><span class="stock-badge stock-low">${p.stock} Left</span></c:when>
                            <c:otherwise><span class="stock-badge stock-in">In Stock</span></c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${p.stock > 0}">
                        <form action="${pageContext.request.contextPath}/cart/add" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="productId" value="${p.id}">
                            <button type="submit" class="btn btn-primary btn-sm w-100">
                                <i class="fas fa-cart-plus me-1"></i> Add to Cart
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${p.stock == 0}">
                        <button class="btn btn-secondary btn-sm w-100" disabled>Out of Stock</button>
                    </c:if>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty products}">
        <div class="col-12 text-center py-5">
            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
            <h5 class="text-muted">No products found</h5>
            <p class="text-muted">Try adjusting your search or filters</p>
        </div>
    </c:if>
</div>

<jsp:include page="../partials/footer.jsp" />
