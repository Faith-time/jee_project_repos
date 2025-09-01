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
    <a href="${pageContext.request.contextPath}/mes_contrats">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <h2>📊 Dashboard Locataire</h2>
    <p>Bienvenue ${sessionScope.utilisateur.nom} ${sessionScope.utilisateur.prenom}.</p>

    <div class="row mt-4">
        <div class="col-md-3">
            <div class="card text-white bg-primary mb-3">
                <div class="card-body">
                    <h5 class="card-title">Mes Contrats</h5>
                    <p class="card-text">Total : <strong>${nbContrats}</strong></p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-success mb-3">
                <div class="card-body">
                    <h5 class="card-title">Paiements effectués</h5>
                    <p class="card-text">Total : <strong>${nbPaiementsPayes}</strong></p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-warning mb-3">
                <div class="card-body">
                    <h5 class="card-title">Paiements en attente</h5>
                    <p class="card-text">Total : <strong>${nbPaiementsAttente}</strong></p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-danger mb-3">
                <div class="card-body">
                    <h5 class="card-title">Solde restant</h5>
                    <p class="card-text"><strong>${soldeRestant} €</strong></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
