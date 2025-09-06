<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Administrateur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .sidebar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .nav-link {
            color: rgba(255,255,255,0.8);
            transition: all 0.3s;
        }
        .nav-link:hover, .nav-link.active {
            color: white;
            background-color: rgba(255,255,255,0.1);
            border-radius: 5px;
        }
        .stats-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            transition: transform 0.3s, box-shadow 0.3s;
            overflow: hidden;
            position: relative;
        }
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
        }
        .stats-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
            background: linear-gradient(90deg, rgba(255,255,255,0.3) 0%, rgba(255,255,255,0.1) 100%);
        }
        .stats-card-users {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .stats-card-proprietaires {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }
        .stats-card-locataires {
            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
        }
        .stats-card-immeubles {
            background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%);
        }
        .stats-number {
            font-size: 3rem;
            font-weight: 700;
            text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .stats-icon {
            font-size: 3rem;
            opacity: 0.3;
            position: absolute;
            right: 20px;
            top: 20px;
        }
        .welcome-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            border: none;
        }
        .activity-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        .activity-item {
            border-left: 3px solid #667eea;
            padding-left: 15px;
            margin-bottom: 15px;
        }
        .activity-time {
            font-size: 0.8rem;
            color: #6c757d;
        }
        .quick-action-btn {
            border-radius: 50px;
            padding: 12px 25px;
            margin: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            text-decoration: none;
            display: inline-block;
        }
        .quick-action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        .chart-container {
            height: 300px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f8f9fa;
            border-radius: 10px;
            color: #6c757d;
        }
    </style>
</head>
<body class="bg-light">
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <nav class="col-md-3 col-lg-2 d-md-block sidebar collapse">
            <div class="position-sticky pt-3">
                <div class="text-center mb-4">
                    <h5 class="text-white"><i class="fas fa-user-shield"></i> Administration</h5>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Tableau de bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/utilisateurs">
                            <i class="fas fa-users"></i> Tous les utilisateurs
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/proprietaires">
                            <i class="fas fa-home"></i> Propriétaires
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/locataires">
                            <i class="fas fa-user-friends"></i> Locataires
                        </a>
                    </li>

                </ul>
                <hr class="text-white">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt"></i> Déconnexion
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Main content -->
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <!-- Header -->
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2"><i class="fas fa-tachometer-alt text-primary"></i> Dashboard Administrateur</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <button type="button" class="btn btn-outline-secondary" onclick="refreshStats()">
                            <i class="fas fa-sync-alt"></i> Actualiser
                        </button>
                    </div>
                </div>
            </div>

            <!-- Welcome Card -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card welcome-card">
                        <div class="card-body">
                            <div class="row align-items-center">
                                <div class="col-md-8">
                                    <h4 class="mb-2">
                                        <i class="fas fa-hand-wave"></i> Bienvenue sur votre espace administrateur
                                    </h4>
                                    <p class="mb-0">Gérez efficacement votre système de gestion locative</p>
                                    <small class="opacity-75">
                                        <i class="fas fa-calendar-alt"></i> Dernière connexion :
                                        <fmt:formatDate value="${lastLogin}" pattern="dd/MM/yyyy à HH:mm" />
                                    </small>
                                </div>
                                <div class="col-md-4 text-center">
                                    <i class="fas fa-user-shield fa-4x opacity-25"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-users text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Utilisateurs</h6>
                                    <div class="stats-number">${nbUtilisateurs != null ? nbUtilisateurs : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-arrow-up"></i> Total enregistrés
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-users stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-proprietaires text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Propriétaires</h6>
                                    <div class="stats-number">${nbProprietaires != null ? nbProprietaires : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-home"></i> Actifs
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-home stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-locataires text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Locataires</h6>
                                    <div class="stats-number">${nbLocataires != null ? nbLocataires : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-user-friends"></i> Enregistrés
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-user-friends stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-immeubles text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Immeubles</h6>
                                    <div class="stats-number">${nbImmeubles != null ? nbImmeubles : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-building"></i> Gérés
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-building stats-icon"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card activity-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-bolt text-warning"></i> Actions rapides
                            </h5>
                        </div>
                        <div class="card-body text-center">
                            <a href="${pageContext.request.contextPath}/utilisateurs?action=form"
                               class="quick-action-btn btn btn-primary">
                                <i class="fas fa-plus"></i> Nouvel utilisateur
                            </a>
                            <a href="${pageContext.request.contextPath}/proprietaires?action=form"
                               class="quick-action-btn btn btn-success">
                                <i class="fas fa-home"></i> Nouveau propriétaire
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/contrats"
                               class="quick-action-btn btn btn-info">
                                <i class="fas fa-file-contract"></i> Gérer contrats
                            </a>
                            <a href="${pageContext.request.contextPath}/paiements"
                               class="quick-action-btn btn btn-warning">
                                <i class="fas fa-credit-card"></i> Voir paiements
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Activity & Charts -->
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="card activity-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-clock text-info"></i> Activité récente
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="activity-item">
                                <div class="d-flex justify-content-between">
                                    <span><i class="fas fa-user-plus text-success"></i> Nouvel utilisateur ajouté</span>
                                    <span class="activity-time">Il y a 2h</span>
                                </div>
                            </div>
                            <div class="activity-item">
                                <div class="d-flex justify-content-between">
                                    <span><i class="fas fa-file-contract text-info"></i> Contrat mis à jour</span>
                                    <span class="activity-time">Il y a 5h</span>
                                </div>
                            </div>
                            <div class="activity-item">
                                <div class="d-flex justify-content-between">
                                    <span><i class="fas fa-credit-card text-warning"></i> Paiement reçu</span>
                                    <span class="activity-time">Hier</span>
                                </div>
                            </div>
                            <div class="activity-item">
                                <div class="d-flex justify-content-between">
                                    <span><i class="fas fa-home text-success"></i> Nouvel immeuble ajouté</span>
                                    <span class="activity-time">Il y a 2 jours</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6 mb-4">
                    <div class="card activity-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-chart-pie text-primary"></i> Statistiques mensuelles
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <div class="text-center">
                                    <i class="fas fa-chart-bar fa-3x mb-3"></i>
                                    <p>Graphiques à venir</p>
                                    <small class="text-muted">Les statistiques détaillées seront bientôt disponibles</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Function to refresh statistics
    function refreshStats() {
        // Show loading state
        const refreshBtn = document.querySelector('[onclick="refreshStats()"]');
        const originalContent = refreshBtn.innerHTML;
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Actualisation...';
        refreshBtn.disabled = true;

        // Simulate refresh (replace with actual AJAX call)
        setTimeout(() => {
            refreshBtn.innerHTML = originalContent;
            refreshBtn.disabled = false;

            // Show success message
            const alert = document.createElement('div');
            alert.className = 'alert alert-success alert-dismissible fade show position-fixed';
            alert.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
            alert.innerHTML = `
                <i class="fas fa-check-circle"></i> Statistiques mises à jour avec succès
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            document.body.appendChild(alert);

            // Auto remove after 3 seconds
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 3000);
        }, 1500);
    }

    // Add hover animations to stats cards
    document.addEventListener('DOMContentLoaded', function() {
        const statsCards = document.querySelectorAll('.stats-card');

        statsCards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-5px) scale(1.02)';
            });

            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0) scale(1)';
            });
        });
    });

    // Welcome animation
    document.addEventListener('DOMContentLoaded', function() {
        const welcomeCard = document.querySelector('.welcome-card');
        welcomeCard.style.opacity = '0';
        welcomeCard.style.transform = 'translateY(20px)';

        setTimeout(() => {
            welcomeCard.style.transition = 'all 0.5s ease';
            welcomeCard.style.opacity = '1';
            welcomeCard.style.transform = 'translateY(0)';
        }, 100);
    });
</script>
</body>
</html>