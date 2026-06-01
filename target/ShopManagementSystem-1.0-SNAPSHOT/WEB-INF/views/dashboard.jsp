<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="partials/header.jsp">
    <jsp:param name="title" value="Dashboard" />
    <jsp:param name="subtitle" value="Sales overview and analytics" />
</jsp:include>

<!-- Stat Cards -->
<div class="row g-4 mb-4">
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon primary"><i class="fas fa-indian-rupee-sign"></i></div>
            <div class="stat-value">&#8377;<fmt:formatNumber value="${totalSales}" pattern="#,##0.00"/></div>
            <div class="stat-label">Total Sales</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon success"><i class="fas fa-shopping-bag"></i></div>
            <div class="stat-value">${totalOrders}</div>
            <div class="stat-label">Total Orders</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon warning"><i class="fas fa-box"></i></div>
            <div class="stat-value">${totalProducts}</div>
            <div class="stat-label">Total Products</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon danger"><i class="fas fa-exclamation-triangle"></i></div>
            <div class="stat-value">${lowStockCount}</div>
            <div class="stat-label">Low Stock Items</div>
        </div>
    </div>
</div>

<!-- Charts Row -->
<div class="row g-4 mb-4">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5><i class="fas fa-chart-line me-2"></i>Monthly Sales</h5>
            </div>
            <div class="card-body">
                <div class="chart-container">
                    <canvas id="salesChart"></canvas>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h5><i class="fas fa-trophy me-2"></i>Top Products</h5>
            </div>
            <div class="card-body">
                <div class="chart-container">
                    <canvas id="topProductsChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Top Products Table -->
<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <h5><i class="fas fa-star me-2"></i>Best Selling Products</h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table mb-0">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Product Name</th>
                                <th>Qty Sold</th>
                                <th>Revenue</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="tp" items="${topProducts}" varStatus="idx">
                                <tr>
                                    <td>${idx.index + 1}</td>
                                    <td><strong>${tp.name}</strong></td>
                                    <td>${tp.totalQty}</td>
                                    <td><strong>&#8377;<fmt:formatNumber value="${tp.totalRevenue}" pattern="#,##0.00"/></strong></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty topProducts}">
                                <tr><td colspan="4" class="text-center text-muted py-4">No sales data yet</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
// Art Deco Noir Chart Theme
Chart.defaults.color = '#9B9484';
Chart.defaults.borderColor = 'rgba(212,175,55,0.1)';

const monthlyLabels = [<c:forEach var="ms" items="${monthlySales}" varStatus="s">'${ms.month}'<c:if test="${!s.last}">,</c:if></c:forEach>].reverse();
const monthlyData = [<c:forEach var="ms" items="${monthlySales}" varStatus="s">${ms.total}<c:if test="${!s.last}">,</c:if></c:forEach>].reverse();

new Chart(document.getElementById('salesChart'), {
    type: 'line',
    data: {
        labels: monthlyLabels.length > 0 ? monthlyLabels : ['No Data'],
        datasets: [{
            label: 'Sales (Rs.)',
            data: monthlyData.length > 0 ? monthlyData : [0],
            borderColor: '#D4AF37',
            backgroundColor: 'rgba(212,175,55,0.08)',
            fill: true, tension: 0.4, borderWidth: 2.5,
            pointBackgroundColor: '#D4AF37', pointBorderColor: '#0A0A0A',
            pointBorderWidth: 2, pointRadius: 5
        }]
    },
    options: {
        responsive: true, maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
            y: { beginAtZero: true, grid: { color: 'rgba(212,175,55,0.06)' }, ticks: { color: '#9B9484' } },
            x: { grid: { display: false }, ticks: { color: '#9B9484' } }
        }
    }
});

const tpLabels = [<c:forEach var="tp" items="${topProducts}" varStatus="s">'${tp.name}'<c:if test="${!s.last}">,</c:if></c:forEach>];
const tpData = [<c:forEach var="tp" items="${topProducts}" varStatus="s">${tp.totalQty}<c:if test="${!s.last}">,</c:if></c:forEach>];
const bgColors = ['#D4AF37','#C17F59','#4CAF7D','#E8A838','#D94F4F'];

new Chart(document.getElementById('topProductsChart'), {
    type: 'doughnut',
    data: {
        labels: tpLabels.length > 0 ? tpLabels : ['No Data'],
        datasets: [{
            data: tpData.length > 0 ? tpData : [1],
            backgroundColor: bgColors, borderWidth: 0,
            borderColor: '#181818'
        }]
    },
    options: {
        responsive: true, maintainAspectRatio: false,
        plugins: {
            legend: { position: 'bottom', labels: { padding: 14, usePointStyle: true, font: { size: 11, family: 'DM Sans' }, color: '#9B9484' } }
        }
    }
});
</script>

<jsp:include page="partials/footer.jsp" />
