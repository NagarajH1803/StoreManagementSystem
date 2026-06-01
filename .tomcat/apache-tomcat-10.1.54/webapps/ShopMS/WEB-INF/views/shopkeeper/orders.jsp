<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Orders" />
    <jsp:param name="subtitle" value="View order history and download invoices" />
</jsp:include>

<c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i>${param.success}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
</c:if>

<div class="card">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table mb-0">
                <thead>
                    <tr>
                        <th>Order #</th>
                        <c:if test="${sessionScope.role == 'MANAGER'}"><th>Shopkeeper</th></c:if>
                        <th>Customer</th>
                        <th>Items</th>
                        <th>Total</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td><strong>#${o.id}</strong></td>
                            <c:if test="${sessionScope.role == 'MANAGER'}"><td>${o.shopkeeperName}</td></c:if>
                            <td>
                                ${o.customerName}
                                <c:if test="${not empty o.customerPhone}"><br><small class="text-muted">${o.customerPhone}</small></c:if>
                            </td>
                            <td>
                                <c:forEach var="item" items="${o.items}" varStatus="s">
                                    <small>${item.productName} x${item.quantity}</small><c:if test="${!s.last}"><br></c:if>
                                </c:forEach>
                            </td>
                            <td><strong>&#8377;<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></strong></td>
                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd-MMM-yyyy HH:mm"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 'COMPLETED'}"><span class="badge-status badge-completed">Completed</span></c:when>
                                    <c:when test="${o.status == 'PENDING'}"><span class="badge-status badge-pending">Pending</span></c:when>
                                    <c:when test="${o.status == 'CANCELLED'}"><span class="badge-status badge-cancelled">Cancelled</span></c:when>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/orders/pdf?id=${o.id}" class="btn btn-sm btn-primary" title="Download Invoice">
                                    <i class="fas fa-file-pdf"></i>
                                </a>
                                <c:if test="${sessionScope.role == 'MANAGER' && o.status == 'PENDING'}">
                                    <form action="${pageContext.request.contextPath}/orders/status" method="post" style="display:inline;">
                                        <input type="hidden" name="orderId" value="${o.id}">
                                        <input type="hidden" name="status" value="COMPLETED">
                                        <button type="submit" class="btn btn-sm btn-success" title="Mark Complete"><i class="fas fa-check"></i></button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}">
                        <tr><td colspan="${sessionScope.role == 'MANAGER' ? 8 : 7}" class="text-center text-muted py-4">No orders found</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../partials/footer.jsp" />
