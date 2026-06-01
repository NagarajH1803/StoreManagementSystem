<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Category Management" />
    <jsp:param name="subtitle" value="Manage product categories" />
</jsp:include>

<c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i>${param.success}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
</c:if>

<div class="row g-4">
    <div class="col-md-4">
        <div class="card">
            <div class="card-header"><h5><i class="fas fa-plus-circle me-2"></i>Add Category</h5></div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/categories/add" method="post">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <div class="mb-3">
                        <label class="form-label">Category Name</label>
                        <input type="text" name="name" class="form-control" required maxlength="100">
                    </div>
                    <button type="submit" class="btn btn-success w-100"><i class="fas fa-save me-1"></i>Add Category</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-md-8">
        <div class="card">
            <div class="card-header"><h5><i class="fas fa-tags me-2"></i>All Categories</h5></div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table mb-0">
                        <thead><tr><th>#</th><th>Name</th><th>Actions</th></tr></thead>
                        <tbody>
                            <c:forEach var="cat" items="${categories}" varStatus="idx">
                                <tr>
                                    <td>${idx.index + 1}</td>
                                    <td><strong>${cat.name}</strong></td>
                                    <td>
                                        <button class="btn btn-sm btn-primary" onclick="editCategory(${cat.id}, '${cat.name}')"><i class="fas fa-edit"></i></button>
                                        <form action="${pageContext.request.contextPath}/categories/delete" method="get" style="display:inline;" onsubmit="return confirm('Delete this category?')">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty categories}">
                                <tr><td colspan="3" class="text-center text-muted py-4">No categories found</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Edit Category Modal -->
<div class="modal fade" id="editCategoryModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-edit me-2"></i>Edit Category</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/categories/update" method="post">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="id" id="editCatId">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Category Name</label>
                        <input type="text" name="name" id="editCatName" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i>Update</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function editCategory(id, name) {
    document.getElementById('editCatId').value = id;
    document.getElementById('editCatName').value = name;
    new bootstrap.Modal(document.getElementById('editCategoryModal')).show();
}
</script>

<jsp:include page="../partials/footer.jsp" />
