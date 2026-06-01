<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Shopping Cart" />
    <jsp:param name="subtitle" value="Review your items and checkout" />
</jsp:include>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-2"></i>${errorMessage}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
</c:if>

<c:set var="cart" value="${sessionScope.cart}" />

<c:choose>
    <c:when test="${empty cart}">
        <div class="card">
            <div class="card-body text-center py-5">
                <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">Your cart is empty</h4>
                <p class="text-muted mb-4">Start adding products to your cart</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                    <i class="fas fa-store me-1"></i> Browse Products
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header">
                        <h5><i class="fas fa-shopping-cart me-2"></i>Cart Items (${sessionScope.cartCount})</h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table mb-0 cart-table">
                                <thead><tr><th>Product</th><th>Price</th><th>Quantity</th><th>Subtotal</th><th></th></tr></thead>
                                <tbody>
                                    <c:forEach var="item" items="${cart}">
                                        <tr>
                                            <td><strong>${item.productName}</strong></td>
                                            <td>&#8377;<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/cart/update" method="post" class="d-flex align-items-center gap-1">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="productId" value="${item.productId}">
                                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="999" class="form-control form-control-sm" style="width:70px;" onchange="this.form.submit()">
                                                </form>
                                            </td>
                                            <td><strong>&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></strong></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="productId" value="${item.productId}">
                                                    <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="cart-summary">
                    <h5 class="mb-3"><i class="fas fa-receipt me-2"></i>Order Summary</h5>
                    <c:set var="grandTotal" value="0" />
                    <c:forEach var="item" items="${cart}">
                        <div class="summary-row">
                            <span>${item.productName} x${item.quantity}</span>
                            <span>&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></span>
                        </div>
                        <c:set var="grandTotal" value="${grandTotal + item.subtotal}" />
                    </c:forEach>
                    <div class="summary-total">
                        <span>Total</span>
                        <span>&#8377;<fmt:formatNumber value="${grandTotal}" pattern="#,##0.00"/></span>
                    </div>
                </div>

                <div class="card mt-3">
                    <div class="card-header"><h5>Customer Details</h5></div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/orders/place" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <div class="mb-3">
                                <label class="form-label">Customer Name</label>
                                <input type="text" name="customerName" class="form-control" required placeholder="Enter customer name">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Phone (Optional)</label>
                                <input type="text" name="customerPhone" class="form-control" placeholder="Enter phone number">
                            </div>
                            <button type="submit" class="btn btn-success w-100">
                                <i class="fas fa-check-circle me-1"></i> Place Order
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../partials/footer.jsp" />
