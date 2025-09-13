<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Locataire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <style>
        body { display: flex; min-height: 100vh; }
        .sidebar { width: 250px; background: #343a40; color: white; flex-shrink: 0; }
        .sidebar a { color: #ddd; text-decoration: none; display: block; padding: 12px 20px; }
        .sidebar a:hover { background: #495057; color: #fff; }
        .content { flex-grow: 1; padding: 20px; background: #f8f9fa; }
    </style>
</head>
<body>
<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom">Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/locataire/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/unites">🏢Unités Disponibles</a>
    <a href="${pageContext.request.contextPath}/contrats">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/locataire/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <h2>📊 Dashboard Locataire</h2>
    <p>Bienvenue ${sessionScope.utilisateur.nom} ${sessionScope.utilisateur.prenom}.</p>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
