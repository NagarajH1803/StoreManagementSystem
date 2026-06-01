<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#0A0A0A">
    <title>500 - Server Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="login-page">
        <div class="login-card" style="text-align:center;">
            <div style="font-size:64px; color:var(--danger); margin-bottom:16px;">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <h3 style="color:var(--danger);">500</h3>
            <p class="subtitle">Something went wrong on our end. Please try again later.</p>
            <div style="text-align: left; background: #fff3f3; padding: 10px; border: 1px solid #ffcccc; margin-top: 10px; font-family: monospace; font-size: 12px; overflow: auto; max-height: 200px;">
                <strong>Error Detail:</strong> <br>
                ${pageContext.exception}
                <% 
                    if (exception != null) {
                        out.println("<br><br><strong>Stack Trace:</strong><br>");
                        exception.printStackTrace(new java.io.PrintWriter(out));
                    }
                %>
            </div>
            <br>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                <i class="fas fa-home me-1"></i> Go Home
            </a>
        </div>
    </div>
</body>
</html>
