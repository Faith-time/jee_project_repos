<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : 'Gestion Immeubles'}</title>
    <!-- Bootstrap CSS (local) -->
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .feature-card {
            transition: transform 0.3s ease;
            border: none;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
        }

        .navbar-brand {
            font-weight: bold;
        }

        /* Styles personnalisés additionnels */
        ${param.extraCSS}
    </style>

    <!-- CSS personnalisé de la page -->
    <c:if test="${not empty param.customCSS}">
        <link href="${pageContext.request.contextPath}${param.customCSS}" rel="stylesheet">
    </c:if>
</head>
<body>

<!-- Inclusion du navbar -->
<jsp:include page="/WEB-INF/jsp/shared/navbar.jsp" />

<!-- Messages d'alerte globaux -->
<c:if test="${not empty requestScope.success}">
    <div class="container mt-3">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i>
                ${requestScope.success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</c:if>

<c:if test="${not empty requestScope.error}">
    <div class="container mt-3">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i>
                ${requestScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</c:if>

<c:if test="${not empty requestScope.info}">
    <div class="container mt-3">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="fas fa-info-circle"></i>
                ${requestScope.info}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</c:if>

<!-- Contenu de la page -->
<main class="container-fluid">
    <jsp:include page="${param.contentPage}" />
</main>

<!-- Footer -->
<footer class="bg-dark text-white py-4 mt-5">
    <div class="container text-center">
        <p class="mb-0">&copy; 2024 Gestion Immeubles - Tous droits réservés</p>
        <p class="mb-0">
            <small class="text-muted">Version 1.0 - Système de gestion locative</small>
        </p>
    </div>
</footer>

<!-- Bootstrap JS (local) -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>

<!-- Scripts personnalisés de la page -->
<c:if test="${not empty param.customJS}">
    <script src="${pageContext.request.contextPath}${param.customJS}"></script>
</c:if>

<!-- Script inline personnalisé -->
<c:if test="${not empty param.inlineJS}">
    <script>
        ${param.inlineJS}
    </script>
</c:if>

</body>
</html>