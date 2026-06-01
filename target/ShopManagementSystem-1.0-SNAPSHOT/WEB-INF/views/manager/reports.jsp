<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../partials/header.jsp">
    <jsp:param name="title" value="Sales Reports" />
    <jsp:param name="subtitle" value="View detailed sales analytics" />
</jsp:include>

<div class="card mb-4">
    <div class="card-header">
        <h5><i class="fas fa-filter me-2"></i>Filter by Date Range</h5>
    </div>
    <div class="card-body">
        <form id="reportForm" class="d-flex gap-3 align-items-end">
            <div>
                <label class="form-label">Start Date</label>
                <input type="date" id="startDate" class="form-control" required>
            </div>
            <div>
                <label class="form-label">End Date</label>
                <input type="date" id="endDate" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary"><i class="fas fa-chart-bar me-1"></i>Generate Report</button>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h5><i class="fas fa-chart-area me-2"></i>Daily Sales Report</h5>
    </div>
    <div class="card-body">
        <div class="chart-container" style="height:400px;">
            <canvas id="reportChart"></canvas>
        </div>
    </div>
</div>

<div class="card mt-4" id="reportTableCard" style="display:none;">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table mb-0">
                <thead><tr><th>Date</th><th>Orders</th><th>Total Sales</th></tr></thead>
                <tbody id="reportTableBody"></tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
Chart.defaults.color = '#9B9484';
Chart.defaults.borderColor = 'rgba(212,175,55,0.1)';

let reportChart = null;
document.getElementById('reportForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    fetch('${pageContext.request.contextPath}/api/sales?start=' + start + '&end=' + end)
        .then(r => r.json())
        .then(data => {
            const labels = data.map(d => d.date);
            const totals = data.map(d => d.total);
            const counts = data.map(d => d.orderCount);
            if (reportChart) reportChart.destroy();
            reportChart = new Chart(document.getElementById('reportChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        { label: 'Sales (Rs.)', data: totals, backgroundColor: 'rgba(212,175,55,0.7)', borderRadius: 6, borderSkipped: false },
                        { label: 'Orders', data: counts, backgroundColor: 'rgba(193,127,89,0.7)', borderRadius: 6, borderSkipped: false }
                    ]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    scales: {
                        y: { beginAtZero: true, grid: { color: 'rgba(212,175,55,0.06)' }, ticks: { color: '#9B9484' } },
                        x: { grid: { display: false }, ticks: { color: '#9B9484' } }
                    },
                    plugins: { legend: { labels: { color: '#9B9484', font: { family: 'DM Sans' } } } }
                }
            });
            let tbody = '';
            data.forEach(d => {
                tbody += '<tr><td>' + d.date + '</td><td>' + d.orderCount + '</td><td><strong>Rs. ' + parseFloat(d.total).toFixed(2) + '</strong></td></tr>';
            });
            document.getElementById('reportTableBody').innerHTML = tbody || '<tr><td colspan="3" class="text-center py-3">No data for selected range</td></tr>';
            document.getElementById('reportTableCard').style.display = 'block';
        });
});
</script>

<jsp:include page="../partials/footer.jsp" />
