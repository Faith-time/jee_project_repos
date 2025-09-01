<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion Locative</title>
    <!-- Bootstrap CSS (local) -->
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<!-- ✅ NAVBAR -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">🏠 Gestion Immeubles</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/utilisateurs">👤 Utilisateurs</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/immeubles">🏢 Immeubles</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/unites">🏠 Unités</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contrats">📑 Contrats</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/paiements">💰 Paiements</a></li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">⚙️ Compte</a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/login">Connexion</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/register">Inscription</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">Déconnexion</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <h1 class="text-center">Bienvenue dans la Gestion des Immeubles</h1>
    <p class="text-muted text-center">Utilise le menu ci-dessus pour naviguer</p>
</div>

<!-- Bootstrap JS (local) -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
