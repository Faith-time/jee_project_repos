<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Utilisateur" %>
<%@ page import="java.time.LocalDate" %>

<%
    Utilisateur locataireConnecte = (Utilisateur) session.getAttribute("utilisateur");
    request.setAttribute("now", LocalDate.now());
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mes Contrats - Espace Locataire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            margin: 0;
        }
        .sidebar {
            width: 250px;
            background: #343a40;
            color: white;
            flex-shrink: 0;
        }
        .sidebar a {
            color: #ddd;
            text-decoration: none;
            display: block;
            padding: 12px 20px;
        }
        .sidebar a:hover {
            background: #495057;
            color: #fff;
        }
        .sidebar a.active {
            background: #007bff;
            color: #fff;
        }
        .content {
            flex-grow: 1;
            padding: 20px;
            background: #f8f9fa;
        }
        .contract-card {
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border: none;
            background: white;
            margin-bottom: 20px;
        }
        .contract-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
        }
        .contract-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
            border-radius: 10px 10px 0 0;
        }
        .contract-body {
            padding: 1.5rem;
        }
        .status-active {
            background: linear-gradient(45deg, #28a745, #20c997);
            color: white;
            padding: 0.3rem 0.8rem;
            border-radius: 15px;
            font-size: 0.85rem;
            font-weight: bold;
        }
        .status-expired {
            background: linear-gradient(45deg, #dc3545, #e83e8c);
            color: white;
            padding: 0.3rem 0.8rem;
            border-radius: 15px;
            font-size: 0.85rem;
            font-weight: bold;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.8rem;
            padding: 0.5rem 0;
            border-bottom: 1px solid #f1f3f5;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
            display: flex;
            align-items: center;
        }
        .info-label i {
            margin-right: 8px;
            color: #667eea;
        }
        .info-value {
            color: #212529;
            font-weight: 500;
        }
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            color: #6c757d;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
            color: #dee2e6;
        }
        .unit-details {
            background: #f8f9fa;
            border-left: 4px solid #667eea;
            padding: 1rem;
            margin: 1rem 0;
            border-radius: 0 8px 8px 0;
        }
        .actions-section {
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 2px solid #f1f3f5;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom">Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/unites">🏢 Unités Disponibles</a>
    <a href="${pageContext.request.contextPath}/contrats" class="active">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2><i class="fas fa-file-contract me-2 text-primary"></i>Mes Contrats de Location</h2>
            <p class="text-muted mb-0">Bienvenue <%= locataireConnecte.getPrenom() %> <%= locataireConnecte.getNom() %></p>
        </div>
        <div class="text-end">
            <div class="bg-primary text-white rounded p-3">
                <h4 class="mb-0">${fn:length(contrats)}</h4>
                <small>Contrat(s)</small>
            </div>
        </div>
    </div>

    <!-- Liste des contrats -->
    <c:choose>
        <c:when test="${empty contrats}">
            <div class="empty-state">
                <i class="fas fa-file-contract"></i>
                <h4>Vous n'avez aucun contrat pour le moment</h4>
                <p>Lorsque vous signerez un contrat de location, il apparaîtra ici.</p>
                <a href="${pageContext.request.contextPath}/unites" class="btn btn-primary mt-3">
                    <i class="fas fa-building me-2"></i>Voir les unités disponibles
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="contrat" items="${contrats}">
                    <div class="col-md-6 mb-4">
                        <div class="card contract-card">
                            <div class="contract-header">
                                <div class="d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">
                                        <i class="fas fa-file-contract me-2"></i>
                                        Contrat #${contrat.id}
                                    </h5>
                                    <!-- Statut du contrat -->
                                    <c:choose>
                                        <c:when test="${empty contrat.dateFin or contrat.dateFin.after(now)}">
                                            <span class="status-active">
                                                <i class="fas fa-check-circle me-1"></i>Actif
                                            </span>
                                        </c:when>
                                        <c:when test="${contrat.dateFin.before(now)}">
                                            <span class="status-expired">
                                                <i class="fas fa-times-circle me-1"></i>Expiré
                                            </span>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="contract-body">
                                <!-- Informations sur l'unité -->
                                <div class="unit-details">
                                    <h6 class="mb-2 text-primary">
                                        <i class="fas fa-building me-2"></i>Informations de l'unité
                                    </h6>
                                    <div class="info-row">
                                        <span class="info-label">
                                            <i class="fas fa-home"></i>Immeuble
                                        </span>
                                        <span class="info-value">${contrat.unite.immeuble.nom}</span>
                                    </div>
                                    <div class="info-row">
                                        <span class="info-label">
                                            <i class="fas fa-door-closed"></i>Unité N°
                                        </span>
                                        <span class="info-value">${contrat.unite.numero}</span>
                                    </div>
                                    <div class="info-row">
                                        <span class="info-label">
                                            <i class="fas fa-th-large"></i>Pièces
                                        </span>
                                        <span class="info-value">${contrat.unite.nbPieces}</span>
                                    </div>
                                    <div class="info-row">
                                        <span class="info-label">
                                            <i class="fas fa-expand-arrows-alt"></i>Superficie
                                        </span>
                                        <span class="info-value">${contrat.unite.superficie} m²</span>
                                    </div>
                                </div>

                                <!-- Détails du contrat -->
                                <div class="info-row">
                                    <span class="info-label">
                                        <i class="fas fa-calendar-alt"></i>Date début
                                    </span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${contrat.dateDebut}" pattern="dd/MM/yyyy"/>
                                    </span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">
                                        <i class="fas fa-calendar-check"></i>Date fin
                                    </span>
                                    <span class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty contrat.dateFin}">
                                                <fmt:formatDate value="${contrat.dateFin}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Non spécifiée</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">
                                        <i class="fas fa-money-bill-wave"></i>Loyer mensuel
                                    </span>
                                    <span class="info-value">
                                        <strong class="text-success">
                                            <fmt:formatNumber value="${contrat.montant}" type="number" maxFractionDigits="0"/> FCFA
                                        </strong>
                                    </span>
                                </div>

                                <!-- Actions -->
                                <div class="actions-section">
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/paiements?contrat=${contrat.id}"
                                           class="btn btn-outline-primary btn-sm">
                                            <i class="fas fa-eye me-1"></i>Voir paiements
                                        </a>
                                        <a href="${pageContext.request.contextPath}/contrat/details/${contrat.id}"
                                           class="btn btn-outline-info btn-sm">
                                            <i class="fas fa-info-circle me-1"></i>Détails
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>