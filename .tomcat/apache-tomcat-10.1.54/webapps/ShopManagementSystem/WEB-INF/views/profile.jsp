<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="partials/header.jsp">
    <jsp:param name="title" value="Profile" />
    <jsp:param name="subtitle" value="Manage your account settings" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i>${successMessage}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-2"></i>${errorMessage}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <div class="card mb-4">
            <div class="card-header"><h5><i class="fas fa-user me-2"></i>Account Information</h5></div>
            <div class="card-body">
                <div class="mb-3">
                    <label class="form-label">Username</label>
                    <input type="text" class="form-control" value="${sessionScope.username}" disabled>
                </div>
                <div class="mb-3">
                    <label class="form-label">Full Name</label>
                    <input type="text" class="form-control" value="${sessionScope.fullName}" disabled>
                </div>
                <div class="mb-3">
                    <label class="form-label">Role</label>
                    <input type="text" class="form-control" value="${sessionScope.role}" disabled>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header"><h5><i class="fas fa-key me-2"></i>Change Password</h5></div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/profile/password" method="post">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <div class="mb-3">
                        <label class="form-label">Current Password</label>
                        <input type="password" name="currentPassword" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">New Password</label>
                        <input type="password" name="newPassword" class="form-control" required minlength="4">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Confirm New Password</label>
                        <input type="password" name="confirmPassword" class="form-control" required minlength="4">
                    </div>
                    <button type="submit" class="btn btn-primary w-100"><i class="fas fa-save me-1"></i>Update Password</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="partials/footer.jsp" />
