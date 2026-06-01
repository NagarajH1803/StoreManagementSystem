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
                                <div class="d-flex gap-1 align-items-center">
                                    <a href="${pageContext.request.contextPath}/orders/pdf?id=${o.id}" class="btn btn-sm btn-primary" title="Download Invoice">
                                        <i class="fas fa-file-pdf"></i>
                                    </a>
                                    <button type="button" class="btn btn-sm btn-qr" title="Payment QR Code"
                                            onclick="showPaymentQR(${o.id}, '${o.customerName}', ${o.totalAmount})">
                                        <i class="fas fa-qrcode"></i>
                                    </button>
                                    <c:if test="${sessionScope.role == 'MANAGER' && o.status == 'PENDING'}">
                                        <form action="${pageContext.request.contextPath}/orders/status" method="post" style="display:inline;">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="orderId" value="${o.id}">
                                            <input type="hidden" name="status" value="COMPLETED">
                                            <button type="submit" class="btn btn-sm btn-success" title="Mark Complete"><i class="fas fa-check"></i></button>
                                        </form>
                                    </c:if>
                                </div>
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

<!-- Payment QR Code Modal -->
<div class="modal fade" id="qrModal" tabindex="-1" aria-labelledby="qrModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content qr-modal-content">
            <div class="modal-header qr-modal-header">
                <div class="d-flex align-items-center gap-2">
                    <div class="qr-header-icon">
                        <i class="fas fa-qrcode"></i>
                    </div>
                    <div>
                        <h5 class="modal-title mb-0" id="qrModalLabel">Payment QR Code</h5>
                        <small class="text-muted" id="qrOrderInfo"></small>
                    </div>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center qr-modal-body">
                <div class="qr-card">
                    <div class="qr-amount-badge" id="qrAmountBadge"></div>
                    <div class="qr-image-wrapper" id="qrImageWrapper">
                        <div class="qr-loading" id="qrLoading">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mt-2 text-muted">Generating QR Code...</p>
                        </div>
                        <img id="qrImage" src="" alt="Payment QR Code" class="qr-image" style="display:none;" 
                             onload="document.getElementById('qrLoading').style.display='none'; this.style.display='block';">
                    </div>
                    <div class="qr-scan-hint">
                        <i class="fas fa-mobile-alt me-1"></i>
                        Scan with any UPI app to pay
                    </div>
                    <div class="qr-supported-apps">
                        <span class="qr-app-badge"><i class="fas fa-wallet"></i> Google Pay</span>
                        <span class="qr-app-badge"><i class="fas fa-wallet"></i> PhonePe</span>
                        <span class="qr-app-badge"><i class="fas fa-wallet"></i> Paytm</span>
                        <span class="qr-app-badge"><i class="fas fa-wallet"></i> BHIM</span>
                    </div>
                </div>
            </div>
            <div class="modal-footer qr-modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times me-1"></i> Close
                </button>
                <button type="button" class="btn btn-primary" onclick="downloadQR()">
                    <i class="fas fa-download me-1"></i> Download QR
                </button>
            </div>
        </div>
    </div>
</div>

<style>
    /* QR Button Style */
    .btn-qr {
        background: linear-gradient(135deg, #6C63FF, #4834d4);
        color: #fff;
        border: none;
        transition: all 0.3s ease;
    }
    .btn-qr:hover {
        background: linear-gradient(135deg, #5A52E0, #3621b0);
        color: #fff;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(108, 99, 255, 0.4);
    }

    /* QR Modal Styling */
    .qr-modal-content {
        border: none;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    }
    .qr-modal-header {
        background: linear-gradient(135deg, #1A1A2E, #16213E);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        padding: 1.25rem 1.5rem;
        color: #fff;
    }
    .qr-modal-header .btn-close {
        filter: invert(1);
    }
    .qr-modal-header .text-muted {
        color: rgba(255, 255, 255, 0.6) !important;
    }
    .qr-header-icon {
        width: 42px;
        height: 42px;
        border-radius: 10px;
        background: linear-gradient(135deg, #6C63FF, #4834d4);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.2rem;
    }
    .qr-modal-body {
        background: #f8f9fa;
        padding: 2rem;
    }
    .qr-card {
        background: #fff;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        position: relative;
    }
    .qr-amount-badge {
        display: inline-block;
        background: linear-gradient(135deg, #00b894, #00cec9);
        color: #fff;
        font-size: 1.3rem;
        font-weight: 700;
        padding: 0.5rem 1.5rem;
        border-radius: 50px;
        margin-bottom: 1.25rem;
        letter-spacing: 0.5px;
        box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
    }
    .qr-image-wrapper {
        background: #fff;
        border: 3px dashed #e0e0e0;
        border-radius: 12px;
        padding: 1rem;
        margin: 0 auto 1.25rem;
        max-width: 320px;
        min-height: 280px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .qr-image {
        width: 280px;
        height: 280px;
        border-radius: 8px;
    }
    .qr-loading {
        text-align: center;
        padding: 2rem;
    }
    .qr-scan-hint {
        color: #6c757d;
        font-size: 0.95rem;
        margin-bottom: 1rem;
        padding: 0.5rem 1rem;
        background: #f0f0f0;
        border-radius: 8px;
        display: inline-block;
    }
    .qr-supported-apps {
        display: flex;
        gap: 0.5rem;
        justify-content: center;
        flex-wrap: wrap;
    }
    .qr-app-badge {
        font-size: 0.75rem;
        background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
        color: #555;
        padding: 0.3rem 0.7rem;
        border-radius: 20px;
        font-weight: 500;
    }
    .qr-modal-footer {
        border-top: 1px solid #e8e8e8;
        padding: 1rem 1.5rem;
        background: #fff;
    }
</style>

<script>
    var currentQROrderId = null;

    function showPaymentQR(orderId, customerName, amount) {
        currentQROrderId = orderId;

        // Update modal info
        document.getElementById('qrOrderInfo').textContent = 'Order #' + orderId + ' \u2022 ' + customerName;
        document.getElementById('qrAmountBadge').innerHTML = '\u20B9 ' + amount.toFixed(2);

        // Show loading, hide image
        document.getElementById('qrLoading').style.display = 'block';
        document.getElementById('qrImage').style.display = 'none';

        // Set QR image source
        var contextPath = '${pageContext.request.contextPath}';
        document.getElementById('qrImage').src = contextPath + '/orders/qr?id=' + orderId + '&t=' + Date.now();

        // Show modal
        var qrModal = new bootstrap.Modal(document.getElementById('qrModal'));
        qrModal.show();
    }

    function downloadQR() {
        if (currentQROrderId) {
            var link = document.createElement('a');
            link.href = '${pageContext.request.contextPath}/orders/qr?id=' + currentQROrderId;
            link.download = 'Payment_QR_Order_' + currentQROrderId + '.png';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }
</script>

<jsp:include page="../partials/footer.jsp" />
