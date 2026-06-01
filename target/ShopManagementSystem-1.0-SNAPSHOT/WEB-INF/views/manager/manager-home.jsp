<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Product Management" />
    <jsp:param name="subtitle" value="Manage your product inventory" />
</jsp:include>

<!-- Success/Error Messages -->
<c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i>${param.success}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
</c:if>

<!-- Search & Add -->
<div class="search-bar d-flex justify-content-between align-items-center flex-wrap gap-3">
    <form action="${pageContext.request.contextPath}/products" method="get" class="d-flex gap-2 flex-grow-1">
        <input type="text" name="keyword" class="form-control" placeholder="Search products..." value="${keyword}" style="max-width:300px;">
        <select name="category" class="form-select" style="max-width:200px;">
            <option value="">All Categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.id}" ${selectedCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
        </select>
        <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i></button>
    </form>
    <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addProductModal">
        <i class="fas fa-plus"></i> Add Product
    </button>
</div>

<!-- Products Table -->
<div class="card">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table mb-0">
                <thead>
                    <tr>
                        <th>Image</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty p.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${p.imageUrl}" alt="${p.name}" style="width:50px;height:50px;object-fit:cover;border-radius:8px;">
                                    </c:when>
                                    <c:otherwise>
                                        <div style="width:50px;height:50px;border-radius:8px;background:#f1f5f9;display:flex;align-items:center;justify-content:center;">
                                            <i class="fas fa-image" style="color:#94a3b8;"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${p.name}</strong>
                                <c:if test="${not empty p.description}">
                                    <br><small class="text-muted">${p.description.length() > 50 ? p.description.substring(0,50).concat('...') : p.description}</small>
                                </c:if>
                            </td>
                            <td>${p.categoryName != null ? p.categoryName : '-'}</td>
                            <td><strong>&#8377;<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.stock == 0}"><span class="stock-badge stock-out">Out of Stock</span></c:when>
                                    <c:when test="${p.stock < 10}"><span class="stock-badge stock-low">${p.stock} Left</span></c:when>
                                    <c:otherwise><span class="stock-badge stock-in">${p.stock} In Stock</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary" onclick="editProduct(${p.id}, '${p.name}', '${p.description}', ${p.price}, ${p.stock}, ${p.categoryId})" title="Edit">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <form action="${pageContext.request.contextPath}/products/delete" method="post" style="display:inline;" onsubmit="return confirm('Delete this product?')">
                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-danger" title="Delete"><i class="fas fa-trash"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty products}">
                        <tr><td colspan="6" class="text-center text-muted py-4">No products found</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Add Product Modal -->
<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-plus-circle me-2"></i>Add New Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/products/add" method="post" enctype="multipart/form-data">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Product Name</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-6 mb-3">
                            <label class="form-label">Price (Rs.)</label>
                            <input type="number" name="price" class="form-control" step="0.01" min="0" required>
                        </div>
                        <div class="col-6 mb-3">
                            <label class="form-label">Stock</label>
                            <input type="number" name="stock" class="form-control" min="0" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Category</label>
                        <select name="categoryId" class="form-select">
                            <option value="">Select Category</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Product Image</label>
                        <input type="file" name="image" class="form-control" accept="image/*">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-save me-1"></i>Save Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Product Modal -->
<div class="modal fade" id="editProductModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-edit me-2"></i>Edit Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/products/update" method="post" enctype="multipart/form-data">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="id" id="editId">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Product Name</label>
                        <input type="text" name="name" id="editName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description" id="editDesc" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-6 mb-3">
                            <label class="form-label">Price (Rs.)</label>
                            <input type="number" name="price" id="editPrice" class="form-control" step="0.01" min="0" required>
                        </div>
                        <div class="col-6 mb-3">
                            <label class="form-label">Stock</label>
                            <input type="number" name="stock" id="editStock" class="form-control" min="0" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Category</label>
                        <select name="categoryId" id="editCategory" class="form-select">
                            <option value="">Select Category</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">New Image (optional)</label>
                        <input type="file" name="image" class="form-control" accept="image/*">
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
function editProduct(id, name, desc, price, stock, catId) {
    document.getElementById('editId').value = id;
    document.getElementById('editName').value = name;
    document.getElementById('editDesc').value = desc || '';
    document.getElementById('editPrice').value = price;
    document.getElementById('editStock').value = stock;
    document.getElementById('editCategory').value = catId || '';
    new bootstrap.Modal(document.getElementById('editProductModal')).show();
}
</script>

<jsp:include page="../partials/footer.jsp" />
