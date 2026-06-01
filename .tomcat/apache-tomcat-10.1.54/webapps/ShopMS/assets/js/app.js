// Shop Management System - App JS
document.addEventListener('DOMContentLoaded', function() {
    // Auto-dismiss alerts after 4 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(function() { alert.remove(); }, 500);
        }, 4000);
    });

    // Sidebar active link highlighting
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
        if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href').split('?')[0])) {
            link.classList.add('active');
        }
    });
});

// Confirm delete actions
function confirmDelete(url) {
    if (confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
        window.location.href = url;
    }
}
