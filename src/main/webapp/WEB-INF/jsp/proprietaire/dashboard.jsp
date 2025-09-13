<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Propriétaire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
        .content {
            flex-grow: 1;
            padding: 20px;
            background: rgba(255,255,255,0.95);
            backdrop-filter: blur(10px);
            margin: 20px;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        }

        .welcome-header {
            background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
            color: white;
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }

        .welcome-header h2 {
            margin: 0;
            font-size: 2.2rem;
            font-weight: 600;
        }

        .welcome-subtitle {
            margin-top: 10px;
            opacity: 0.9;
            font-size: 1.1rem;
        }

        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            text-align: center;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 5px;
            border-radius: 20px 20px 0 0;
        }

        .stat-card.immeubles::before { background: linear-gradient(90deg, #3498db, #2980b9); }
        .stat-card.unites::before { background: linear-gradient(90deg, #27ae60, #229954); }
        .stat-card.disponibles::before { background: linear-gradient(90deg, #f39c12, #e67e22); }
        .stat-card.locataires-actifs::before { background: linear-gradient(90deg, #e74c3c, #c0392b); }
        .stat-card.total-locataires::before { background: linear-gradient(90deg, #9b59b6, #8e44ad); }
        .stat-card.paiements::before { background: linear-gradient(90deg, #34495e, #2c3e50); }

        .stat-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 25px 50px rgba(0,0,0,0.15);
        }

        .stat-icon {
            font-size: 3rem;
            margin-bottom: 15px;
            opacity: 0.8;
        }

        .immeubles .stat-icon { color: #3498db; }
        .unites .stat-icon { color: #27ae60; }
        .disponibles .stat-icon { color: #f39c12; }
        .locataires-actifs .stat-icon { color: #e74c3c; }
        .total-locataires .stat-icon { color: #9b59b6; }
        .paiements .stat-icon { color: #34495e; }

        .stat-number {
            font-size: 3rem;
            font-weight: 700;
            margin: 15px 0;
            line-height: 1;
        }

        .stat-label {
            font-size: 1.1rem;
            font-weight: 600;
            color: #5a6c7d;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        /* Styles pour la liste des locataires */
        .locataires-section {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-top: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .locataires-list {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-top: 20px;
        }

        .locataires-list h4 {
            color: #2c3e50;
            margin-bottom: 20px;
            font-weight: 600;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }

        .locataire-item {
            background: white;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
            border-left: 4px solid #3498db;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            transition: all 0.3s ease;
        }

        .locataire-item:hover {
            transform: translateX(5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .locataire-name {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
        }

        .locataire-email {
            color: #7f8c8d;
            font-size: 0.9rem;
        }

        .debug-info {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 10px;
            padding: 20px;
            margin-top: 30px;
            font-family: 'Courier New', monospace;
            font-size: 0.9rem;
        }

        .debug-title {
            font-weight: 600;
            color: #495057;
            margin-bottom: 15px;
            font-size: 1.1rem;
        }

        .debug-item {
            display: flex;
            justify-content: space-between;
            padding: 5px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .debug-item:last-child {
            border-bottom: none;
        }

        .debug-label {
            font-weight: 600;
            color: #6c757d;
        }

        .debug-value {
            color: #28a745;
            font-weight: 600;
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
            .stats-container {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom text-center">Espace Propriétaire</h3>
    <a href="${pageContext.request.contextPath}/dashboard">🏠 Dashboard</a>
    <a href="${pageContext.request.contextPath}/locataires">👥 Locataires</a>
    <a href="${pageContext.request.contextPath}/immeubles">🏢 Immeubles</a>
    <a href="${pageContext.request.contextPath}/unites">🏢 Unités</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <!-- En-tête de bienvenue -->
    <div class="welcome-header">
        <h2>📊 Dashboard Propriétaire</h2>
        <div class="welcome-subtitle">
            Bienvenue <strong>${sessionScope.utilisateur.prenom} ${sessionScope.utilisateur.nom}</strong>
        </div>
    </div>

    <!-- Cartes de statistiques -->
    <div class="stats-container">
        <div class="stat-card immeubles">
            <div class="stat-icon">🏢</div>
            <div class="stat-number">${nbImmeubles != null ? nbImmeubles : 0}</div>
            <div class="stat-label">Mes Immeubles</div>
        </div>

        <div class="stat-card unites">
            <div class="stat-icon">🏠</div>
            <div class="stat-number">${nbUnites != null ? nbUnites : 0}</div>
            <div class="stat-label">Total Unités</div>
        </div>

        <div class="stat-card disponibles">
            <div class="stat-icon">✅</div>
            <div class="stat-number">${unitesDisponibles != null ? unitesDisponibles : 0}</div>
            <div class="stat-label">Unités Disponibles</div>
        </div>

        <!-- Nouvelles cartes pour les locataires -->
        <div class="stat-card locataires-actifs">
            <div class="stat-icon">👥</div>
            <div class="stat-number">${nbLocatairesActifs != null ? nbLocatairesActifs : 0}</div>
            <div class="stat-label">Locataires Actifs</div>
        </div>

        <div class="stat-card total-locataires">
            <div class="stat-icon">📊</div>
            <div class="stat-number">${nbTotalLocataires != null ? nbTotalLocataires : 0}</div>
            <div class="stat-label">Total Locataires</div>
        </div>

        <div class="stat-card paiements">
            <div class="stat-icon">💰</div>
            <div class="stat-number">${nbPaiements != null ? nbPaiements : 0}</div>
            <div class="stat-label">Paiements Reçus</div>
        </div>
    </div>

    <!-- Section des locataires -->
    <div class="locataires-section">
        <h3>👥 Gestion des Locataires</h3>

        <!-- Liste détaillée des locataires -->
        <c:if test="${not empty locataires}">
            <div class="locataires-list">
                <h4>Vos locataires actuels (${locataires.size()}):</h4>
                <c:forEach items="${locataires}" var="locataire">
                    <div class="locataire-item">
                        <div class="locataire-name">${locataire.nomComplet}</div>
                        <div class="locataire-email">${locataire.email}</div>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <c:if test="${empty locataires}">
            <div class="alert alert-info mt-3">
                <h5>Aucun locataire actuel</h5>
                <p>Vous n'avez actuellement aucun locataire avec des contrats actifs.</p>
            </div>
        </c:if>
    </div>

    <!-- Actions rapides -->
    <div class="row mt-4">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Actions rapides</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3 mb-2">
                            <a href="${pageContext.request.contextPath}/immeubles" class="btn btn-primary btn-block">
                                Gérer mes immeubles
                            </a>
                        </div>
                        <div class="col-md-3 mb-2">
                            <a href="${pageContext.request.contextPath}/unites" class="btn btn-success btn-block">
                                Gérer mes unités
                            </a>
                        </div>
                        <div class="col-md-3 mb-2">
                            <a href="${pageContext.request.contextPath}/locataires" class="btn btn-warning btn-block">
                                Voir mes locataires
                            </a>
                        </div>
                        <div class="col-md-3 mb-2">
                            <a href="${pageContext.request.contextPath}/contrats" class="btn btn-info btn-block">
                                Gérer les contrats
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Informations de débogage (à supprimer en production) -->
    <div class="debug-info">
        <div class="debug-title">🔍 Informations de débogage</div>
        <div class="debug-item">
            <span class="debug-label">ID Utilisateur:</span>
            <span class="debug-value">${sessionScope.utilisateur.id}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Rôle:</span>
            <span class="debug-value">${sessionScope.utilisateur.role}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Nombre Immeubles:</span>
            <span class="debug-value">${nbImmeubles}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Total Unités:</span>
            <span class="debug-value">${nbUnites}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Unités Disponibles:</span>
            <span class="debug-value">${unitesDisponibles}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Locataires Actifs:</span>
            <span class="debug-value">${nbLocatairesActifs}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Total Locataires:</span>
            <span class="debug-value">${nbTotalLocataires}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Nombre de Paiements:</span>
            <span class="debug-value">${nbPaiements}</span>
        </div>
        <div class="debug-item">
            <span class="debug-label">Request URI:</span>
            <span class="debug-value">${pageContext.request.requestURI}</span>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>