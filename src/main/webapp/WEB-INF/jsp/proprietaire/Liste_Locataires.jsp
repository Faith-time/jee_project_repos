<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Utilisateur" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%
    List<Utilisateur> locataires = (List<Utilisateur>) request.getAttribute("locataires");
    Boolean isProprietaireView = (Boolean) request.getAttribute("isProprietaireView");
    Boolean isAdminView = (Boolean) request.getAttribute("isAdminView");
    Long proprietaireId = (Long) request.getAttribute("proprietaireId");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Locataires</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .sidebar {
            width: 250px;
            background: #343a40;
            color: white;
            flex-shrink: 0;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
        }

        .sidebar a {
            color: #ddd;
            text-decoration: none;
            display: block;
            padding: 12px 20px;
            transition: all 0.3s ease;
        }

        .sidebar a:hover {
            background: #495057;
            color: #fff;
            padding-left: 25px;
        }

        .sidebar a.active {
            background: #495057;
            color: #fff;
        }

        .content {
            flex-grow: 1;
            padding: 20px;
            background: rgba(255,255,255,0.95);
            backdrop-filter: blur(10px);
            margin: 20px;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        }

        .page-header {
            background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
            color: white;
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }

        .page-header h2 {
            margin: 0;
            font-size: 2.2rem;
            font-weight: 600;
        }

        .page-subtitle {
            margin-top: 10px;
            opacity: 0.9;
            font-size: 1.1rem;
        }

        .locataire-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            border-left: 5px solid #3498db;
        }

        .locataire-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.15);
        }

        .locataire-info h5 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 1.3rem;
            font-weight: 600;
        }

        .locataire-details {
            color: #6c757d;
            font-size: 0.95em;
        }

        .locataire-details .info-row {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }

        .locataire-details .info-row i {
            width: 20px;
            margin-right: 10px;
            color: #3498db;
        }

        .badge-custom {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            color: white;
            padding: 8px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .stats-card {
            background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
            color: white;
            border-radius: 15px;
            padding: 30px;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        .stats-card h4 {
            font-size: 3rem;
            font-weight: 700;
            margin: 0;
        }

        .stats-card p {
            margin: 10px 0 0 0;
            font-size: 1.1rem;
            opacity: 0.9;
        }

        .empty-state {
            text-align: center;
            color: #6c757d;
            padding: 60px 40px;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
            color: #3498db;
        }

        .empty-state h4 {
            color: #2c3e50;
            margin-bottom: 15px;
        }

        .btn-action {
            padding: 8px 16px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-size: 0.9rem;
            margin: 2px;
        }

        .btn-primary-action {
            background: linear-gradient(135deg, #3498db, #5dade2);
            color: white;
        }

        .btn-secondary-action {
            background: linear-gradient(135deg, #95a5a6, #bdc3c7);
            color: white;
        }

        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            color: white;
        }

        @media (max-width: 768px) {
            body {
                flex-direction: column;
            }
            .sidebar {
                width: 100%;
                order: 2;
            }
            .content {
                margin: 10px;
                order: 1;
            }
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom text-center">
        <c:choose>
            <c:when test="${isAdminView}">Espace Admin</c:when>
            <c:otherwise>Espace Propriétaire</c:otherwise>
        </c:choose>
    </h3>

    <c:choose>
        <c:when test="${isAdminView}">
            <a href="${pageContext.request.contextPath}/admin/dashboard">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/locataires" class="active">👥 Tous les Locataires</a>
            <a href="${pageContext.request.contextPath}/immeubles">🏢 Tous les Immeubles</a>
            <a href="${pageContext.request.contextPath}/unites">🏠 Toutes les Unités</a>
            <a href="${pageContext.request.contextPath}/admin/rapports">📈 Rapports</a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/dashboard">🏠 Dashboard</a>
            <a href="${pageContext.request.contextPath}/locataires" class="active">👥 Locataires</a>
            <a href="${pageContext.request.contextPath}/immeubles">🏢 Immeubles</a>
            <a href="${pageContext.request.contextPath}/unites">🏢 Unités</a>
            <a href="${pageContext.request.contextPath}/contrats">📋 Contrats</a>
        </c:otherwise>
    </c:choose>

    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu principal -->
<div class="content">
    <!-- En-tête avec statistiques -->
    <div class="row mb-4">
        <div class="col-md-8">
            <div class="page-header">
                <h2>
                    👥
                    <c:choose>
                        <c:when test="${isAdminView}">Tous les Locataires</c:when>
                        <c:otherwise>Mes Locataires</c:otherwise>
                    </c:choose>
                </h2>
                <div class="page-subtitle">
                    <c:choose>
                        <c:when test="${isAdminView}">Vue d'ensemble de tous les locataires du système</c:when>
                        <c:otherwise>Gérez vos locataires et leurs informations</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stats-card">
                <h4>${locataires != null ? locataires.size() : 0}</h4>
                <p>
                    <c:choose>
                        <c:when test="${isAdminView}">Locataires au total</c:when>
                        <c:otherwise>Locataires actifs</c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>
    </div>

    <!-- Liste des locataires -->
    <c:choose>
        <c:when test="${locataires != null && locataires.size() > 0}">
            <div class="row">
                <c:forEach var="locataire" items="${locataires}">
                    <div class="col-md-6 col-lg-4">
                        <div class="locataire-card">
                            <div class="locataire-info">
                                <h5>
                                    <i class="fas fa-user"></i>
                                        ${locataire.prenom} ${locataire.nom}
                                </h5>
                                <div class="locataire-details">
                                    <div class="info-row">
                                        <i class="fas fa-envelope"></i>
                                        <a href="mailto:${locataire.email}" class="text-decoration-none">
                                                ${locataire.email}
                                        </a>
                                    </div>
                                    <div class="info-row">
                                        <i class="fas fa-id-card"></i>
                                        <span>ID: #${locataire.id}</span>
                                    </div>
                                    <div class="info-row">
                                        <i class="fas fa-tag"></i>
                                        <span class="badge-custom">
                                                ${locataire.role.displayName}
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="mt-3 text-center">
                                <button type="button" class="btn-action btn-primary-action"
                                        onclick="voirDetails(${locataire.id})" disabled>
                                    <i class="fas fa-eye"></i> Détails
                                </button>
                                <c:if test="${isAdminView}">
                                    <button type="button" class="btn-action btn-secondary-action"
                                            onclick="voirContrats(${locataire.id})" disabled>
                                        <i class="fas fa-file-contract"></i> Contrats
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <i class="fas fa-users"></i>
                <h4>Aucun locataire trouvé</h4>
                <p>
                    <c:choose>
                        <c:when test="${isAdminView}">
                            Il n'y a actuellement aucun locataire enregistré dans le système.
                        </c:when>
                        <c:otherwise>
                            Vous n'avez actuellement aucun locataire dans vos propriétés.<br>
                            Les locataires apparaîtront ici une fois que vous aurez des contrats actifs.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script>
    // Fonctions pour futures fonctionnalités
    function voirDetails(locataireId) {
        alert('Fonctionnalité à implémenter : voir détails du locataire ' + locataireId);
    }

    function voirContrats(locataireId) {
        alert('Fonctionnalité à implémenter : voir contrats du locataire ' + locataireId);
    }

    // Animation d'apparition des cartes
    document.addEventListener('DOMContentLoaded', function() {
        const cards = document.querySelectorAll('.locataire-card');
        cards.forEach((card, index) => {
            setTimeout(() => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'opacity 0.5s, transform 0.5s';

                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, 50);
            }, index * 100);
        });
    });
</script>
</body>
</html>