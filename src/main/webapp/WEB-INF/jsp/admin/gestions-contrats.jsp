<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Contrat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Contrats</title>
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
        .stats-card-contrats {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .stats-card-actifs {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }
        .stats-card-expirant {
            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
        }
        .stats-card-expires {
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
        .status-badge {
            font-size: 0.8em;
            padding: 0.25rem 0.5rem;
        }
        .contract-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s;
        }
        .contract-card:hover {
            transform: translateY(-2px);
        }
        .expired { border-left: 4px solid #dc3545; }
        .active { border-left: 4px solid #28a745; }
        .ending-soon { border-left: 4px solid #ffc107; }
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
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
                <h1 class="h2">
                    <i class="fas fa-file-contract text-primary"></i>
                    Gestion des Contrats
                </h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <a href="${pageContext.request.contextPath}/contrats/nouveau" class="btn btn-success">
                            <i class="fas fa-plus"></i> Nouveau Contrat
                        </a>
                        <button type="button" class="btn btn-outline-secondary">
                            <i class="fas fa-download"></i> Exporter
                        </button>
                    </div>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-contrats text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Total Contrats</h6>
                                    <div class="stats-number">${contrats != null ? contrats.size() : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-file-contract"></i> Enregistrés
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-file-contract stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-actifs text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Actifs</h6>
                                    <div class="stats-number" id="contratsActifs">0</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-check-circle"></i> En cours
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-check-circle stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-expirant text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Expirant bientôt</h6>
                                    <div class="stats-number" id="contratsExpirant">0</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-exclamation-triangle"></i> À renouveler
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-exclamation-triangle stats-icon"></i>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-expires text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Expirés</h6>
                                    <div class="stats-number" id="contratsExpires">0</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-times-circle"></i> Terminés
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-times-circle stats-icon"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filtres et recherche -->
            <div class="card mb-4">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="fas fa-search"></i>
                                </span>
                                <input type="text" id="searchInput" class="form-control"
                                       placeholder="Rechercher par locataire, unité...">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <select id="statusFilter" class="form-select">
                                <option value="">Tous les statuts</option>
                                <option value="active">Actifs</option>
                                <option value="ending">Expirant bientôt</option>
                                <option value="expired">Expirés</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select id="immeubleFilter" class="form-select">
                                <option value="">Tous les immeubles</option>
                                <c:forEach var="contrat" items="${contrats}">
                                    <c:if test="${contrat.unite.immeuble != null}">
                                        <option value="${contrat.unite.immeuble.nom}">
                                                ${contrat.unite.immeuble.nom}
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="button" class="btn btn-outline-secondary w-100" onclick="clearFilters()">
                                <i class="fas fa-times"></i> Effacer
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Liste des contrats -->
            <div class="row" id="contratsContainer">
                <c:choose>
                    <c:when test="${empty contrats}">
                        <div class="col-12">
                            <div class="card contract-card">
                                <div class="card-body text-center py-5">
                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">Aucun contrat trouvé</h5>
                                    <p class="text-muted">Commencez par créer votre premier contrat.</p>
                                    <a href="${pageContext.request.contextPath}/contrats/nouveau" class="btn btn-success">
                                        <i class="fas fa-plus"></i> Nouveau Contrat
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="contrat" items="${contrats}">
                            <div class="col-lg-6 col-xl-4 mb-4 contrat-item"
                                 data-locataire="${contrat.locataire.prenom} ${contrat.locataire.nom}"
                                 data-unite="Unité ${contrat.unite.numero}"
                                 data-immeuble="${contrat.unite.immeuble.nom}">

                                <div class="card contract-card h-100">
                                    <div class="card-header bg-white border-bottom-0 pb-0">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="card-title mb-1">
                                                    <i class="fas fa-user text-primary"></i>
                                                        ${contrat.locataire.prenom} ${contrat.locataire.nom}
                                                </h6>
                                                <small class="text-muted">
                                                    <i class="fas fa-home"></i>
                                                        ${contrat.unite.immeuble.nom} - Unité ${contrat.unite.numero}
                                                </small>
                                            </div>
                                            <span class="status-badge badge" id="status-${contrat.id}"></span>
                                        </div>
                                    </div>

                                    <div class="card-body">
                                        <div class="row g-2 mb-3">
                                            <div class="col-6">
                                                <div class="text-center">
                                                    <small class="text-muted d-block">Début</small>
                                                    <strong class="text-success">
                                                        <fmt:formatDate value="${contrat.dateDebut}" pattern="dd/MM/yyyy"/>
                                                    </strong>
                                                </div>
                                            </div>
                                            <div class="col-6">
                                                <div class="text-center">
                                                    <small class="text-muted d-block">Fin</small>
                                                    <strong class="text-danger">
                                                        <c:choose>
                                                            <c:when test="${contrat.dateFin != null}">
                                                                <fmt:formatDate value="${contrat.dateFin}" pattern="dd/MM/yyyy"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-info">Indéterminée</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </strong>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="text-center mb-3">
                                            <h5 class="text-primary mb-0">
                                                <fmt:formatNumber value="${contrat.montant}" pattern="#,##0"/> FCFA
                                            </h5>
                                            <small class="text-muted">Montant mensuel</small>
                                        </div>

                                        <!-- Informations sur les paiements -->
                                        <c:if test="${contrat.paiements != null && !contrat.paiements.isEmpty()}">
                                            <div class="border-top pt-2">
                                                <small class="text-muted">
                                                    <i class="fas fa-credit-card"></i>
                                                        ${contrat.paiements.size()} paiement(s) enregistré(s)
                                                </small>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="card-footer bg-white border-top">
                                        <div class="d-flex justify-content-between">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/contrats/${contrat.id}"
                                                   class="btn btn-outline-primary" title="Détails">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/contrats/${contrat.id}/modifier"
                                                   class="btn btn-outline-warning" title="Modifier">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                            </div>
                                            <button type="button" class="btn btn-outline-danger btn-sm"
                                                    onclick="confirmerSuppression(${contrat.id})" title="Supprimer">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>

<!-- Modal de confirmation de suppression -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-exclamation-triangle text-warning"></i>
                    Confirmer la suppression
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Êtes-vous sûr de vouloir supprimer ce contrat ?</p>
                <div class="alert alert-warning">
                    <i class="fas fa-info-circle"></i>
                    <strong>Attention :</strong> Cette action est irréversible et supprimera également tous les paiements associés.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <button type="button" class="btn btn-danger" id="confirmDelete">
                    <i class="fas fa-trash"></i> Supprimer
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let contratToDelete = null;

    document.addEventListener('DOMContentLoaded', function() {
        updateStatistics();
        setupFilters();
        updateContractStatus();
    });

    function updateStatistics() {
        const contrats = document.querySelectorAll('.contrat-item');
        let actifs = 0, expirant = 0, expires = 0;
        const today = new Date();
        const in30Days = new Date(today.getTime() + (30 * 24 * 60 * 60 * 1000));

        contrats.forEach(item => {
            // Logique de calcul des statistiques basée sur les dates
            actifs++;
        });

        document.getElementById('contratsActifs').textContent = actifs;
        document.getElementById('contratsExpirant').textContent = expirant;
        document.getElementById('contratsExpires').textContent = expires;
    }

    function updateContractStatus() {
        const contrats = document.querySelectorAll('.contrat-item');
        const today = new Date();
        const in30Days = new Date(today.getTime() + (30 * 24 * 60 * 60 * 1000));

        contrats.forEach(item => {
            const card = item.querySelector('.card');
            const statusBadge = item.querySelector('.status-badge');

            // Par défaut, considérer comme actif
            card.classList.add('active');
            statusBadge.className = 'status-badge badge bg-success';
            statusBadge.textContent = 'Actif';
        });
    }

    function setupFilters() {
        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');
        const immeubleFilter = document.getElementById('immeubleFilter');

        [searchInput, statusFilter, immeubleFilter].forEach(filter => {
            filter.addEventListener('input', filterContrats);
        });
    }

    function filterContrats() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const statusFilter = document.getElementById('statusFilter').value;
        const immeubleFilter = document.getElementById('immeubleFilter').value;
        const contrats = document.querySelectorAll('.contrat-item');

        contrats.forEach(item => {
            let show = true;

            // Filtre de recherche
            if (searchTerm) {
                const locataire = item.dataset.locataire.toLowerCase();
                const unite = item.dataset.unite.toLowerCase();
                if (!locataire.includes(searchTerm) && !unite.includes(searchTerm)) {
                    show = false;
                }
            }

            // Filtre par immeuble
            if (immeubleFilter && item.dataset.immeuble !== immeubleFilter) {
                show = false;
            }

            item.style.display = show ? 'block' : 'none';
        });
    }

    function clearFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('immeubleFilter').value = '';
        filterContrats();
    }

    function confirmerSuppression(contratId) {
        contratToDelete = contratId;
        const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
        modal.show();
    }

    document.getElementById('confirmDelete').addEventListener('click', function() {
        if (contratToDelete) {
            // Ici, vous ajouterez la logique de suppression
            fetch(`${location.pathname}/${contratToDelete}`, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        }
    });
</script>
</body>
</html>