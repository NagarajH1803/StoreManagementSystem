// Shop Management System - Art Deco Noir - App JS
document.addEventListener('DOMContentLoaded', function() {

    // ═══ Auto-dismiss alerts ═══
    document.querySelectorAll('.alert-dismissible').forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s, transform 0.5s';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(function() { alert.remove(); }, 500);
        }, 4000);
    });

    // ═══ Sidebar active link ═══
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
        if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href').split('?')[0])) {
            link.classList.add('active');
        }
    });

    // ═══ Staggered entrance animations ═══
    const animateElements = document.querySelectorAll('.stat-card, .product-card, .card');
    animateElements.forEach(function(el, i) {
        el.classList.add('animate-in');
        el.style.animationDelay = (i * 0.08) + 's';
    });

    // ═══ Animate stat values (counter) ═══
    document.querySelectorAll('.stat-value').forEach(function(el) {
        const text = el.textContent.trim();
        const match = text.match(/[\d,]+\.?\d*/);
        if (!match) return;
        const target = parseFloat(match[0].replace(/,/g, ''));
        if (isNaN(target) || target === 0) return;
        const prefix = text.substring(0, text.indexOf(match[0]));
        const suffix = text.substring(text.indexOf(match[0]) + match[0].length);
        const hasDecimal = match[0].includes('.');
        const duration = 1200;
        const start = performance.now();
        el.textContent = prefix + '0' + suffix;
        function step(now) {
            const progress = Math.min((now - start) / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3);
            const current = target * eased;
            if (hasDecimal) {
                el.textContent = prefix + current.toLocaleString('en-IN', {minimumFractionDigits:2, maximumFractionDigits:2}) + suffix;
            } else {
                el.textContent = prefix + Math.floor(current).toLocaleString('en-IN') + suffix;
            }
            if (progress < 1) requestAnimationFrame(step);
        }
        requestAnimationFrame(step);
    });

    // ═══ Table row animations ═══
    document.querySelectorAll('.table tbody tr').forEach(function(row, i) {
        row.style.opacity = '0';
        row.style.animation = 'fadeSlideUp 0.4s ease-out forwards';
        row.style.animationDelay = (i * 0.04) + 's';
    });
});

// ═══ Confirm delete with styled approach ═══
function confirmDelete(url) {
    if (confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
        window.location.href = url;
    }
}
