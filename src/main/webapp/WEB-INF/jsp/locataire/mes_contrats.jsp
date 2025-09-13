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
        * {
            box-sizing: border-box;
        }

        body {
            display: flex;
            min-height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .sidebar {
            width: 280px;
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            color: white;
            flex-shrink: 0;
            box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
            position: relative;
            overflow: hidden;
        }

        .sidebar::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #3498db, #9b59b6, #e74c3c, #f39c12);
        }

        .sidebar h3 {
            background: rgba(0, 0, 0, 0.2);
            margin: 0;
            padding: 1.5rem;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .sidebar a {
            color: #ecf0f1;
            text-decoration: none;
            display: block;
            padding: 15px 20px;
            border-left: 3px solid transparent;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
        }

        .sidebar a::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
            transition: left 0.5s;
        }

        .sidebar a:hover::before {
            left: 100%;
        }

        .sidebar a:hover {
            background: rgba(52, 152, 219, 0.2);
            border-left-color: #3498db;
            color: #fff;
            transform: translateX(8px);
        }

        .sidebar a.active {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            border-left-color: #fff;
            color: #fff;
            box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.2);
        }

        .content {
            flex-grow: 1;
            padding: 30px;
            background: transparent;
        }

        .page-header {
            background: white;
            border-radius: 20px;
            padding: 2rem;
            margin-bottom: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .contract-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            border: none;
            margin-bottom: 25px;
            overflow: hidden;
            position: relative;
        }

        .contract-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }

        .contract-card:hover {
            transform: translateY(-8px) scale(1.02);
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        }

        .contract-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
            position: relative;
            overflow: hidden;
        }

        .contract-header::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -20px;
            width: 100px;
            height: 200%;
            background: rgba(255, 255, 255, 0.1);
            transform: rotate(15deg);
        }

        .contract-header h5 {
            margin: 0;
            font-size: 1.5rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            position: relative;
            z-index: 2;
        }

        .contract-header h5 i {
            margin-right: 12px;
            font-size: 1.8rem;
            opacity: 0.9;
        }

        .contract-body {
            padding: 2rem;
            background: white;
        }

        .info-item {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
        }

        .info-item:last-child {
            border-bottom: none;
        }

        .info-item:hover {
            background: rgba(102, 126, 234, 0.05);
            margin: 0 -1rem;
            padding-left: 1rem;
            padding-right: 1rem;
            border-radius: 10px;
        }

        .info-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            margin-right: 15px;
            font-size: 16px;
        }

        .info-content {
            flex: 1;
        }

        .info-label {
            font-size: 0.85rem;
            color: #6c757d;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 2px;
        }

        .info-value {
            font-size: 1.1rem;
            color: #2c3e50;
            font-weight: 600;
        }

        .empty-state {
            text-align: center;
            padding: 5rem 3rem;
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
            border: 2px dashed #dee2e6;
            position: relative;
            overflow: hidden;
        }

        .empty-state::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
            animation: shimmer 2s infinite;
        }

        @keyframes shimmer {
            0% { left: -100%; }
            100% { left: 100%; }
        }

        .empty-state-icon {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 2rem;
            color: white;
            font-size: 3rem;
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
        }

        .empty-state h4 {
            color: #2c3e50;
            font-weight: 700;
            margin-bottom: 1rem;
            font-size: 1.8rem;
        }

        .empty-state p {
            color: #6c757d;
            font-size: 1.1rem;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            padding: 2rem;
            text-align: center;
            box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }

        .stats-card::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10px;
            width: 80px;
            height: 200%;
            background: rgba(255, 255, 255, 0.1);
            transform: rotate(15deg);
        }

        .stats-card h4 {
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
            position: relative;
            z-index: 2;
        }

        .stats-card small {
            font-size: 1rem;
            opacity: 0.9;
            font-weight: 500;
            position: relative;
            z-index: 2;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 12px;
            padding: 12px 30px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
        }

        .contract-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
            gap: 25px;
        }

        @media (max-width: 768px) {
            .contract-grid {
                grid-template-columns: 1fr;
            }

            .sidebar {
                width: 100%;
                position: fixed;
                top: 0;
                left: -100%;
                height: 100vh;
                z-index: 1000;
                transition: left 0.3s ease;
            }

            .content {
                padding: 20px;
            }
        }

        /* Animation d'entrée pour les cartes */
        .contract-card {
            animation: slideInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
        }

        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Délai d'animation pour chaque carte */
        .contract-card:nth-child(1) { animation-delay: 0.1s; }
        .contract-card:nth-child(2) { animation-delay: 0.2s; }
        .contract-card:nth-child(3) { animation-delay: 0.3s; }
        .contract-card:nth-child(4) { animation-delay: 0.4s; }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h3 class="border-bottom-0">Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/locataire/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/unites">🏢 Unités Disponibles</a>
    <a href="${pageContext.request.contextPath}/contrats" class="active">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/locataire/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <div class="page-header">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h2 class="mb-2">
                    <i class="fas fa-file-contract me-3" style="color: #667eea;"></i>
                    Mes Contrats de Location
                </h2>
                <p class="text-muted mb-0 fs-5">
                    Bienvenue <strong><%= locataireConnecte.getPrenom() %> <%= locataireConnecte.getNom() %></strong>
                </p>
            </div>
            <div class="stats-card">
                <h4 class="mb-0">${fn:length(contrats)}</h4>
                <small>Contrat(s)</small>
            </div>
        </div>
    </div>

    <!-- Liste des contrats -->
    <c:choose>
        <c:when test="${empty contrats}">
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="fas fa-file-contract"></i>
                </div>
                <h4>Aucun contrat pour le moment</h4>
                <p>Lorsque vous signerez un contrat de location, il apparaîtra ici avec tous les détails importants.</p>
                <a href="${pageContext.request.contextPath}/unites" class="btn btn-primary btn-lg">
                    <i class="fas fa-building me-2"></i>Découvrir les unités disponibles
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="contract-grid">
                <c:forEach var="contrat" items="${contrats}" varStatus="status">
                    <div class="contract-card">
                        <div class="contract-header">
                            <h5>
                                <i class="fas fa-file-signature"></i>
                                Contrat #${contrat.id}
                            </h5>
                        </div>
                        <div class="contract-body">
                            <div class="info-item">
                                <div class="info-icon">
                                    <i class="fas fa-hashtag"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Index</div>
                                    <div class="info-value">${status.index}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Locataire ID</div>
                                    <div class="info-value">${contrat.locataire.id}</div>
                                </div>
                            </div>

                            <!-- Test si l'unité existe -->
                            <c:choose>
                                <c:when test="${not empty contrat.unite}">
                                    <div class="info-item">
                                        <div class="info-icon">
                                            <i class="fas fa-door-open"></i>
                                        </div>
                                        <div class="info-content">
                                            <div class="info-label">Unité</div>
                                            <div class="info-value">${contrat.unite.numero}</div>
                                        </div>
                                    </div>

                                    <c:choose>
                                        <c:when test="${not empty contrat.unite.immeuble}">
                                            <div class="info-item">
                                                <div class="info-icon">
                                                    <i class="fas fa-building"></i>
                                                </div>
                                                <div class="info-content">
                                                    <div class="info-label">Immeuble</div>
                                                    <div class="info-value">${contrat.unite.immeuble.nom}</div>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="info-item">
                                                <div class="info-icon">
                                                    <i class="fas fa-exclamation-triangle text-warning"></i>
                                                </div>
                                                <div class="info-content">
                                                    <div class="info-label">Immeuble</div>
                                                    <div class="info-value text-warning">NON CHARGÉ</div>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <div class="info-item">
                                        <div class="info-icon">
                                            <i class="fas fa-exclamation-triangle text-warning"></i>
                                        </div>
                                        <div class="info-content">
                                            <div class="info-label">Unité</div>
                                            <div class="info-value text-warning">NON CHARGÉE</div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
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