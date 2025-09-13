<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Paiements - Gestion Immobilière</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/bootstrap.min.css'/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f8f9fa;
            color: #212529;
        }

        /* Sidebar */
        .sidebar {
            width: 250px;
            height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            position: fixed;
            left: 0;
            top: 0;
            color: white;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            z-index: 1000;
            overflow-y: auto;
        }

        .sidebar h3 {
            color: white;
            margin: 0;
            padding: 20px;
            border-bottom: 1px solid rgba(255,255,255,0.2);
            font-size: 1.2rem;
            text-align: center;
            background: transparent;
        }

        .sidebar a {
            display: block;
            color: white;
            text-decoration: none;
            padding: 15px 20px;
            transition: all 0.3s ease;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            background: transparent;
        }

        .sidebar a:hover {
            background: rgba(255,255,255,0.1);
            padding-left: 30px;
            color: white;
        }

        .sidebar a.active {
            background: rgba(255,255,255,0.2);
            border-left: 4px solid #fff;
        }

        /* Main Content */
        .main-content {
            margin-left: 250px;
            min-height: 100vh;
            padding: 20px;
            background: #f8f9fa;
        }

        /* Cards styles */
        .card {
            background: white;
            border: none;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        /* Cartes de paiement */
        .paiement-card {
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            background: white;
        }

        .paiement-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.15);
        }

        .status-paye {
            border-left-color: #28a745;
        }

        .status-attente {
            border-left-color: #ffc107;
        }

        .status-retard {
            border-left-color: #dc3545;
        }

        .montant-highlight {
            font-size: 1.5rem;
            font-weight: bold;
            color: #2c3e50;
            background: white;
        }

        .date-info {
            font-size: 0.9rem;
            color: #6c757d;
            background: white;
        }

        .btn-payer {
            background: linear-gradient(45deg, #007bff, #0056b3);
            border: none;
            transition: all 0.3s ease;
            color: white;
        }

        .btn-payer:hover {
            transform: scale(1.02);
            box-shadow: 0 4px 15px rgba(0,123,255,0.4);
            color: white;
        }

        /* Statistiques */
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            border: none;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .stats-card .card-body {
            background: transparent;
            color: white;
        }

        .stats-card .card-title, .stats-card .card-text {
            background: transparent;
            color: white;
        }

        .stats-card-success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }

        .stats-card-success .card-body,
        .stats-card-success .card-title,
        .stats-card-success .card-text {
            background: transparent;
            color: white;
        }

        .stats-card-warning {
            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
        }

        .stats-card-warning .card-body,
        .stats-card-warning .card-title,
        .stats-card-warning .card-text {
            background: transparent;
            color: white;
        }

        .stats-card-danger {
            background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%);
        }

        .stats-card-danger .card-body,
        .stats-card-danger .card-title,
        .stats-card-danger .card-text {
            background: transparent;
            color: white;
        }

        /* Page header */
        .page-header {
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }

        .page-title {
            color: #2c3e50;
            font-weight: 700;
            margin-bottom: 10px;
            background: white;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
            background: white;
        }

        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
            background: white;
        }

        /* Animations */
        .fade-in {
            animation: fadeIn 0.6s ease-in;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* Responsive */
        @media (max-width: 768px) {
            .sidebar {
                width: 200px;
            }

            .main-content {
                margin-left: 200px;
                padding: 15px;
            }
        }

        @media (max-width: 576px) {
            .sidebar {
                transform: translateX(-100%);
                transition: transform 0.3s ease;
            }

            .sidebar.show {
                transform: translateX(0);
            }

            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
<!-- Sidebar -->
<div class="sidebar">
    <h3>📱 Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/locataire/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/unites">🏢 Unités Disponibles</a>
    <a href="${pageContext.request.contextPath}/contrats">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/locataire/paiements" class="active">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/auth?action=logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Main Content -->
<div class="main-content">
    <!-- En-tête de page -->
    <div class="page-header fade-in">
        <h1 class="page-title">
            <i class="fas fa-credit-card me-3"></i>Mes Paiements
        </h1>
        <p class="lead text-muted">Gérez vos paiements de loyer en toute sécurité avec CinetPay</p>
    </div>

    <!-- Messages d'alerte -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show fade-in" role="alert">
            <i class="fas fa-check-circle me-2"></i>
                ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show fade-in" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
                ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Statistiques des paiements -->
    <div class="row mb-4 fade-in">
        <div class="col-xl-3 col-md-6 mb-3">
            <div class="card stats-card h-100">
                <div class="card-body text-center">
                    <i class="fas fa-receipt fa-2x mb-3"></i>
                    <h3 class="card-title">
                        <c:choose>
                            <c:when test="${not empty listePaiements}">
                                ${fn:length(listePaiements)}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </h3>
                    <p class="card-text mb-0">Total Paiements</p>
                </div>
            </div>
        </div>

        <div class="col-xl-3 col-md-6 mb-3">
            <div class="card stats-card-success h-100">
                <div class="card-body text-center">
                    <i class="fas fa-check-circle fa-2x mb-3"></i>
                    <h3 class="card-title">
                        <c:set var="payeCount" value="0"/>
                        <c:if test="${not empty listePaiements}">
                            <c:forEach var="p" items="${listePaiements}">
                                <c:if test="${p.statut == 'PAYE'}">
                                    <c:set var="payeCount" value="${payeCount + 1}"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        ${payeCount}
                    </h3>
                    <p class="card-text mb-0">Payés</p>
                </div>
            </div>
        </div>

        <div class="col-xl-3 col-md-6 mb-3">
            <div class="card stats-card-warning h-100">
                <div class="card-body text-center">
                    <i class="fas fa-clock fa-2x mb-3"></i>
                    <h3 class="card-title">
                        <c:set var="attenteCount" value="0"/>
                        <c:if test="${not empty listePaiements}">
                            <c:forEach var="p" items="${listePaiements}">
                                <c:if test="${p.statut == 'EN_ATTENTE'}">
                                    <c:set var="attenteCount" value="${attenteCount + 1}"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        ${attenteCount}
                    </h3>
                    <p class="card-text mb-0">En attente</p>
                </div>
            </div>
        </div>

        <div class="col-xl-3 col-md-6 mb-3">
            <div class="card stats-card-danger h-100">
                <div class="card-body text-center">
                    <i class="fas fa-exclamation-triangle fa-2x mb-3"></i>
                    <h3 class="card-title">
                        <c:set var="retardCount" value="0"/>
                        <c:if test="${not empty listePaiements}">
                            <c:forEach var="p" items="${listePaiements}">
                                <c:if test="${p.statut == 'EN_RETARD'}">
                                    <c:set var="retardCount" value="${retardCount + 1}"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        ${retardCount}
                    </h3>
                    <p class="card-text mb-0">En retard</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Filtres -->
    <div class="row mb-4 fade-in">
        <div class="col-12">
            <div class="card">
                <div class="card-body">
                    <div class="d-flex justify-content-center">
                        <div class="btn-group" role="group">
                            <input type="radio" class="btn-check" name="filter" id="tous" autocomplete="off" checked>
                            <label class="btn btn-outline-primary" for="tous">
                                <i class="fas fa-list me-2"></i>Tous
                            </label>

                            <input type="radio" class="btn-check" name="filter" id="attente" autocomplete="off">
                            <label class="btn btn-outline-warning" for="attente">
                                <i class="fas fa-clock me-2"></i>En attente
                            </label>

                            <input type="radio" class="btn-check" name="filter" id="payes" autocomplete="off">
                            <label class="btn btn-outline-success" for="payes">
                                <i class="fas fa-check me-2"></i>Payés
                            </label>

                            <input type="radio" class="btn-check" name="filter" id="retards" autocomplete="off">
                            <label class="btn btn-outline-danger" for="retards">
                                <i class="fas fa-exclamation-triangle me-2"></i>En retard
                            </label>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Liste des paiements -->
    <div class="row fade-in">
        <div class="col-12">
            <c:choose>
                <c:when test="${empty listePaiements}">
                    <div class="card">
                        <div class="card-body empty-state">
                            <i class="fas fa-inbox"></i>
                            <h4>Aucun paiement trouvé</h4>
                            <p>Vous n'avez pas encore de paiements enregistrés dans le système.</p>
                            <a href="${pageContext.request.contextPath}/locataire/payer" class="btn btn-primary">
                                <i class="fas fa-arrow-left me-2"></i>
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row" id="paiements-container">
                        <c:forEach var="paiement" items="${listePaiements}" varStatus="status">
                            <div class="col-lg-6 col-xl-4 mb-4 paiement-item"
                                 data-statut="${paiement.statut}">
                                <div class="card paiement-card h-100
                                             <c:choose>
                                                 <c:when test="${paiement.statut == 'PAYE'}">status-paye</c:when>
                                                 <c:when test="${paiement.statut == 'EN_ATTENTE'}">status-attente</c:when>
                                                 <c:when test="${paiement.statut == 'EN_RETARD'}">status-retard</c:when>
                                             </c:choose>">

                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <h6 class="card-title mb-0">
                                            <i class="fas fa-file-invoice me-2"></i>
                                            Paiement #${paiement.id}
                                        </h6>
                                        <c:choose>
                                            <c:when test="${paiement.statut == 'PAYE'}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-check-circle me-1"></i>Payé
                                                </span>
                                            </c:when>
                                            <c:when test="${paiement.statut == 'EN_ATTENTE'}">
                                                <span class="badge bg-warning text-dark">
                                                    <i class="fas fa-clock me-1"></i>En attente
                                                </span>
                                            </c:when>
                                            <c:when test="${paiement.statut == 'EN_RETARD'}">
                                                <span class="badge bg-danger">
                                                    <i class="fas fa-exclamation-triangle me-1"></i>En retard
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </div>

                                    <div class="card-body">
                                        <!-- Montant -->
                                        <div class="text-center mb-4">
                                            <div class="montant-highlight">
                                                <fmt:formatNumber value="${paiement.montant}" type="number" maxFractionDigits="0"/> FCFA
                                            </div>
                                        </div>

                                        <!-- Informations sur les dates -->
                                        <div class="row text-center mb-3">
                                            <div class="col-6">
                                                <small class="text-muted d-block">Échéance</small>
                                                <strong class="date-info">
                                                    <c:if test="${not empty paiement.dateEcheance}">
                                                        <c:set var="dateEchStr" value="${paiement.dateEcheance.toString()}" />
                                                        ${fn:substring(dateEchStr, 8, 10)}/${fn:substring(dateEchStr, 5, 7)}/${fn:substring(dateEchStr, 0, 4)}                                                    </c:if>
                                                </strong>
                                            </div>
                                            <div class="col-6">
                                                <small class="text-muted d-block">Payé le</small>
                                                <strong class="date-info">
                                                    <c:choose>
                                                        <c:when test="${not empty paiement.datePaiement}">
                                                            <c:set var="datePaiStr" value="${paiement.datePaiement.toString()}" />
                                                            ${fn:substring(datePaiStr, 8, 10)}/${fn:substring(datePaiStr, 5, 7)}/${fn:substring(datePaiStr, 0, 4)}                                                        </c:when>
                                                        <c:otherwise>-</c:otherwise>
                                                    </c:choose>
                                                </strong>
                                            </div>
                                        </div>

                                        <!-- Informations contrat -->
                                        <c:if test="${not empty paiement.contrat}">
                                            <div class="mb-3">
                                                <small class="text-muted">Contrat:</small>
                                                <div class="fw-bold">
                                                    <c:if test="${not empty paiement.contrat.unite}">
                                                        Unité #${paiement.contrat.unite.numero}
                                                        <c:if test="${not empty paiement.contrat.unite.immeuble}">
                                                            <br><small class="text-muted">${paiement.contrat.unite.immeuble.nom}</small>
                                                        </c:if>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Transaction ID -->
                                        <c:if test="${not empty paiement.transactionId}">
                                            <div class="mb-2">
                                                <small class="text-muted">Transaction:</small>
                                                <br><code class="text-primary">${paiement.transactionId}</code>
                                            </div>
                                        </c:if>

                                        <!-- Operator ID -->
                                        <c:if test="${not empty paiement.operatorId}">
                                            <div class="mb-2">
                                                <small class="text-muted">Opérateur:</small>
                                                <br><span class="text-success fw-bold">${paiement.operatorId}</span>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="card-footer bg-transparent">
                                        <c:choose>
                                            <c:when test="${paiement.statut == 'EN_ATTENTE' or paiement.statut == 'EN_RETARD'}">
                                                <form method="post" action="${pageContext.request.contextPath}/paiement/direct" class="d-grid">
                                                    <input type="hidden" name="paiementId" value="${paiement.id}">
                                                    <button type="submit" class="btn btn-payer">
                                                        <i class="fas fa-credit-card me-2"></i>
                                                        Payer avec CinetPay
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:when test="${paiement.statut == 'PAYE'}">
                                                <div class="d-grid">
                                                    <button class="btn btn-outline-success" disabled>
                                                        <i class="fas fa-check-circle me-2"></i>
                                                        Paiement effectué
                                                    </button>
                                                </div>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Navigation -->
    <div class="row mt-4 fade-in">
        <div class="col-12">
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/locataire/dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Retour au Dashboard
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="<c:url value='/assets/js/bootstrap.bundle.min.js'/>"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Page paiements chargée');

        const filterButtons = document.querySelectorAll('input[name="filter"]');
        const paiementItems = document.querySelectorAll('.paiement-item');

        console.log('Nombre de paiements trouvés:', paiementItems.length);

        // Filtrage des paiements
        filterButtons.forEach(button => {
            button.addEventListener('change', function() {
                const filter = this.id;
                console.log('Filtre sélectionné:', filter);

                paiementItems.forEach(item => {
                    const statut = item.dataset.statut;
                    let show = false;

                    switch(filter) {
                        case 'tous':
                            show = true;
                            break;
                        case 'attente':
                            show = statut === 'EN_ATTENTE';
                            break;
                        case 'payes':
                            show = statut === 'PAYE';
                            break;
                        case 'retards':
                            show = statut === 'EN_RETARD';
                            break;
                    }

                    if (show) {
                        item.style.display = 'block';
                        setTimeout(() => {
                            item.classList.add('fade-in');
                        }, 50);
                    } else {
                        item.classList.remove('fade-in');
                        setTimeout(() => {
                            item.style.display = 'none';
                        }, 300);
                    }
                });
            });
        });

        // Confirmation avant paiement
        const formsPayment = document.querySelectorAll('form[action*="/paiement/init"]');
        console.log('Nombre de formulaires de paiement:', formsPayment.length);

        formsPayment.forEach(form => {
            form.addEventListener('submit', function(e) {
                const paiementId = this.querySelector('input[name="paiementId"]').value;
                const confirmation = confirm('Êtes-vous sûr de vouloir procéder au paiement via CinetPay ?');

                if (!confirmation) {
                    e.preventDefault();
                } else {
                    const button = this.querySelector('button[type="submit"]');
                    button.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Redirection en cours...';
                    button.disabled = true;
                }
            });
        });

        // Animation d'entrée des cartes
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('fade-in');
                }
            });
        }, { threshold: 0.1 });

        paiementItems.forEach(item => {
            observer.observe(item);
        });
    });
</script>
</body>