<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Unités</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11.9.0/dist/sweetalert2.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #2c3e50;
            --secondary-color: #3498db;
            --accent-color: #e74c3c;
            --success-color: #27ae60;
            --warning-color: #f39c12;
            --info-color: #17a2b8;
            --purple-color: #9b59b6;
        }

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
            background: linear-gradient(135deg, var(--purple-color) 0%, #8e44ad 100%);
            color: white;
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }

        .page-header h1 {
            margin: 0;
            font-weight: 600;
            font-size: 2.5rem;
        }

        .breadcrumb {
            background: transparent;
            margin: 0;
            padding-top: 10px;
        }

        .breadcrumb-item a {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
        }

        .breadcrumb-item.active {
            color: white;
        }

        .action-bar {
            background: white;
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 25px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .btn-add {
            background: linear-gradient(135deg, var(--success-color) 0%, #2ecc71 100%);
            border: none;
            padding: 12px 25px;
            border-radius: 10px;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(39, 174, 96, 0.3);
        }

        .btn-add:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(39, 174, 96, 0.4);
            color: white;
        }

        .filter-tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .filter-tab {
            padding: 10px 20px;
            border-radius: 25px;
            border: 2px solid #e0e6ed;
            background: white;
            color: #7f8c8d;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
        }

        .filter-tab.active {
            background: var(--secondary-color);
            border-color: var(--secondary-color);
            color: white;
        }

        .filter-tab:hover {
            border-color: var(--secondary-color);
            color: var(--secondary-color);
        }

        .unites-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 20px;
        }

        .unite-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border: 1px solid #e0e6ed;
            position: relative;
        }

        .unite-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
        }

        .unite-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .unite-numero {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        .statut-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .statut-disponible {
            background: linear-gradient(135deg, #d4edda, #c3e6cb);
            color: #155724;
        }

        .statut-louee {
            background: linear-gradient(135deg, #f8d7da, #f1b0b7);
            color: #721c24;
        }

        .unite-details {
            margin-bottom: 15px;
        }

        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            padding: 8px 0;
            border-bottom: 1px solid #f8f9fa;
        }

        .detail-label {
            color: #6c757d;
            font-weight: 500;
        }

        .detail-value {
            font-weight: 600;
            color: var(--primary-color);
        }

        .loyer-highlight {
            background: linear-gradient(135deg, #fff3cd, #ffeaa7);
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 15px;
            border-left: 4px solid var(--warning-color);
        }

        .loyer-amount {
            font-size: 1.3rem;
            font-weight: 700;
            color: var(--warning-color);
        }

        .card-actions {
            display: flex;
            gap: 8px;
            justify-content: center;
            padding-top: 15px;
            border-top: 1px solid #e9ecef;
            flex-wrap: wrap;
        }

        .btn-action {
            padding: 6px 12px;
            border-radius: 6px;
            border: none;
            font-weight: 600;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            font-size: 0.85rem;
        }

        .btn-edit {
            background: linear-gradient(135deg, #f39c12, #f4d03f);
            color: white;
        }

        .btn-delete {
            background: linear-gradient(135deg, #e74c3c, #ec7063);
            color: white;
        }

        .btn-louer {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            color: white;
        }

        .btn-liberer {
            background: linear-gradient(135deg, #3498db, #5dade2);
            color: white;
        }

        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            color: white;
        }

        .stats-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-bottom: 25px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #7f8c8d;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .total-unites .stat-number { color: var(--primary-color); }
        .unites-disponibles .stat-number { color: var(--success-color); }
        .unites-louees .stat-number { color: var(--accent-color); }
        .revenus-total .stat-number { color: var(--warning-color); }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #7f8c8d;
        }

        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }

        .search-box {
            position: relative;
        }

        .search-box input {
            border-radius: 50px;
            padding: 12px 20px;
            border: 2px solid #e0e6ed;
            transition: all 0.3s ease;
        }

        .search-box input:focus {
            border-color: var(--secondary-color);
            box-shadow: 0 0 15px rgba(52, 152, 219, 0.2);
        }

        .search-box i {
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: #7f8c8d;
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
            .unites-grid {
                grid-template-columns: 1fr;
            }
            .page-header h1 {
                font-size: 2rem;
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
    <a href="${pageContext.request.contextPath}/contrats">📋 Contrats</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <!-- En-tête de page -->
    <div class="page-header">
        <h1><i class="fas fa-home"></i> Gestion des Unités</h1>
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/immeubles">
                        <i class="fas fa-building"></i> Immeubles
                    </a>
                </li>
                <li class="breadcrumb-item active">
                    <i class="fas fa-home"></i> Unités
                </li>
            </ol>
        </nav>
    </div>

    <!-- Messages de notification -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
                ${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>
                ${sessionScope.errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <!-- Statistiques -->
    <div class="stats-row">
        <div class="stat-card total-unites">
            <div class="stat-number" id="totalUnites">${unites.size()}</div>
            <div class="stat-label">Total Unités</div>
        </div>
        <div class="stat-card unites-disponibles">
            <div class="stat-number" id="unitesDisponibles">0</div>
            <div class="stat-label">Disponibles</div>
        </div>
        <div class="stat-card unites-louees">
            <div class="stat-number" id="unitesLouees">0</div>
            <div class="stat-label">Louées</div>
        </div>
        <div class="stat-card revenus-total">
            <div class="stat-number" id="revenusTotal">0</div>
            <div class="stat-label">Revenus (FCFA)</div>
        </div>
    </div>

    <!-- Barre d'actions -->
    <div class="action-bar">
        <div class="row align-items-center mb-3">
            <div class="col-md-6">
                <button type="button" class="btn btn-add" onclick="showAddModal()">
                    <i class="fas fa-plus"></i> Ajouter une Unité
                </button>
            </div>
            <div class="col-md-6">
                <div class="search-box">
                    <input type="text" class="form-control" id="searchInput"
                           placeholder="Rechercher une unité..." onkeyup="filterUnites()">
                    <i class="fas fa-search"></i>
                </div>
            </div>
        </div>

        <!-- Filtres par statut -->
        <div class="filter-tabs">
            <div class="filter-tab active" onclick="filterByStatus('all')">
                <i class="fas fa-list"></i> Toutes
            </div>
            <div class="filter-tab" onclick="filterByStatus('DISPONIBLE')">
                <i class="fas fa-check-circle"></i> Disponibles
            </div>
            <div class="filter-tab" onclick="filterByStatus('LOUEE')">
                <i class="fas fa-user-check"></i> Louées
            </div>
        </div>
    </div>

    <!-- Liste des unités -->
    <div id="unitesContainer">
        <c:choose>
            <c:when test="${not empty unites}">
                <div class="unites-grid" id="unitesGrid">
                    <c:forEach var="unite" items="${unites}">
                        <div class="unite-card" data-statut="${unite.statut}"
                             data-search="unite ${unite.numero} ${unite.nbPieces} pieces ${unite.superficie}m2 ${unite.loyer}fcfa ${unite.immeuble.nom}">

                            <div class="unite-header">
                                <div class="unite-numero">
                                    <i class="fas fa-door-open"></i> Unité #${unite.numero}
                                </div>
                                <span class="statut-badge statut-${unite.statut.toString().toLowerCase()}">
                                        <c:choose>
                                            <c:when test="${unite.statut == 'DISPONIBLE'}">
                                                <i class="fas fa-check-circle"></i> Disponible
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-user-check"></i> Louée
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                            </div>

                            <div class="loyer-highlight">
                                <div class="loyer-amount">
                                    <i class="fas fa-coins"></i> ${unite.loyer} FCFA/mois
                                </div>
                            </div>

                            <div class="unite-details">
                                <div class="detail-row">
                                        <span class="detail-label">
                                            <i class="fas fa-th-large"></i> Pièces
                                        </span>
                                    <span class="detail-value">${unite.nbPieces}</span>
                                </div>
                                <div class="detail-row">
                                        <span class="detail-label">
                                            <i class="fas fa-expand-arrows-alt"></i> Superficie
                                        </span>
                                    <span class="detail-value">${unite.superficie} m²</span>
                                </div>
                                <div class="detail-row">
                                        <span class="detail-label">
                                            <i class="fas fa-building"></i> Immeuble
                                        </span>
                                    <span class="detail-value">${unite.immeuble.nom}</span>
                                </div>
                            </div>

                            <div class="card-actions">
                                <button type="button" class="btn-action btn-edit"
                                        onclick="showEditModal(${unite.id}, ${unite.numero}, ${unite.nbPieces}, ${unite.superficie}, ${unite.loyer}, '${unite.statut}', ${unite.immeuble.id})">
                                    <i class="fas fa-edit"></i> Modifier
                                </button>

                                <c:choose>
                                    <c:when test="${unite.statut == 'DISPONIBLE'}">
                                        <button type="button" class="btn-action btn-louer"
                                                onclick="louerUnite(${unite.id})">
                                            <i class="fas fa-handshake"></i> Louer
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn-action btn-liberer"
                                                onclick="libererUnite(${unite.id})">
                                            <i class="fas fa-unlock"></i> Libérer
                                        </button>
                                    </c:otherwise>
                                </c:choose>

                                <button type="button" class="btn-action btn-delete"
                                        onclick="confirmDelete(${unite.id}, ${unite.numero})">
                                    <i class="fas fa-trash"></i> Supprimer
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <i class="fas fa-home"></i>
                    <h3>Aucune unité trouvée</h3>
                    <p>Commencez par ajouter votre première unité</p>
                    <button type="button" class="btn btn-add" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> Ajouter une Unité
                    </button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Modal Ajouter/Modifier Unité -->
<div class="modal fade" id="uniteModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">
                    <i class="fas fa-plus"></i> Ajouter une Unité
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="uniteForm" method="post" action="${pageContext.request.contextPath}/unites">
                <div class="modal-body">
                    <input type="hidden" id="uniteId" name="id">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="numero" class="form-label">
                                    <i class="fas fa-door-open text-primary"></i>
                                    Numéro <span class="text-danger">*</span>
                                </label>
                                <input type="number" class="form-control" id="numero" name="numero" required
                                       placeholder="Ex: 101, 202...">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="nbPieces" class="form-label">
                                    <i class="fas fa-th-large text-info"></i>
                                    Nombre de pièces <span class="text-danger">*</span>
                                </label>
                                <input type="number" class="form-control" id="nbPieces" name="nbPieces"
                                       required min="1" placeholder="Ex: 1, 2, 3...">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="superficie" class="form-label">
                                    <i class="fas fa-expand-arrows-alt text-success"></i>
                                    Superficie (m²) <span class="text-danger">*</span>
                                </label>
                                <input type="number" class="form-control" id="superficie" name="superficie"
                                       required min="10" step="0.01" placeholder="Ex: 45.5, 120.25...">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="loyer" class="form-label">
                                    <i class="fas fa-coins text-warning"></i>
                                    Loyer (FCFA) <span class="text-danger">*</span>
                                </label>
                                <input type="number" class="form-control" id="loyer" name="loyer"
                                       required min="0" step="1000" placeholder="Ex: 150000, 250000...">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="statut" class="form-label">
                                    <i class="fas fa-flag text-primary"></i>
                                    Statut <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="statut" name="statut" required>
                                    <option value="">-- Choisir un statut --</option>
                                    <option value="DISPONIBLE">Disponible</option>
                                    <option value="LOUEE">Louée</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="immeubleId" class="form-label">
                                    <i class="fas fa-building text-secondary"></i>
                                    Immeuble <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="immeubleId" name="immeubleId" required>
                                    <option value="">-- Choisir un immeuble --</option>
                                    <!-- Les options seront chargées via AJAX -->
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Annuler
                    </button>
                    <button type="submit" class="btn btn-primary" id="submitBtn">
                        <i class="fas fa-save"></i> Enregistrer
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.9.0/dist/sweetalert2.all.min.js"></script>

<script>
    // Initialisation
    document.addEventListener('DOMContentLoaded', function() {
        updateStats();
        loadImmeubles();
    });

    // Mettre à jour les statistiques
    function updateStats() {
        const cards = document.querySelectorAll('.unite-card');
        let disponibles = 0;
        let louees = 0;
        let revenus = 0;

        cards.forEach(card => {
            const statut = card.getAttribute('data-statut');
            if (statut === 'DISPONIBLE') {
                disponibles++;
            } else if (statut === 'LOUEE') {
                louees++;
                // Extraire le loyer du texte (simplification)
                const loyerText = card.querySelector('.loyer-amount').textContent;
                const loyer = parseInt(loyerText.replace(/[^0-9]/g, ''));
                revenus += loyer;
            }
        });

        document.getElementById('unitesDisponibles').textContent = disponibles;
        document.getElementById('unitesLouees').textContent = louees;
        document.getElementById('revenusTotal').textContent = revenus.toLocaleString();
    }

    // Charger la liste des immeubles
    function loadImmeubles() {
        // Simulation - remplacer par un appel AJAX réel
        const select = document.getElementById('immeubleId');

        // Exemple avec des données existantes
        <c:if test="${not empty unites}">
        <c:forEach var="unite" items="${unites}">
        const option = document.createElement('option');
        option.value = '${unite.immeuble.id}';
        option.textContent = '${unite.immeuble.nom} - ${unite.immeuble.adresse}';
        select.appendChild(option);
        </c:forEach>
        </c:if>
    }

    // Afficher le modal d'ajout
    function showAddModal() {
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-plus"></i> Ajouter une Unité';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Ajouter';
        document.getElementById('uniteForm').reset();
        document.getElementById('uniteId').value = '';

        const modal = new bootstrap.Modal(document.getElementById('uniteModal'));
        modal.show();
    }

    // Afficher le modal de modification
    function showEditModal(id, numero, nbPieces, superficie, loyer, statut, immeubleId) {
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Modifier l\'Unité';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Modifier';

        document.getElementById('uniteId').value = id;
        document.getElementById('numero').value = numero;
        document.getElementById('nbPieces').value = nbPieces;
        document.getElementById('superficie').value = superficie;
        document.getElementById('loyer').value = loyer;
        document.getElementById('statut').value = statut;
        document.getElementById('immeubleId').value = immeubleId;

        const form = document.getElementById('uniteForm');
        form.action = '${pageContext.request.contextPath}/unites?id=' + id;

        const modal = new bootstrap.Modal(document.getElementById('uniteModal'));
        modal.show();
    }

    // Louer une unité
    function louerUnite(id) {
        Swal.fire({
            title: 'Louer cette unité',
            text: 'Confirmez-vous vouloir marquer cette unité comme louée ?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#27ae60',
            cancelButtonColor: '#95a5a6',
            confirmButtonText: 'Oui, louer',
            cancelButtonText: 'Annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '${pageContext.request.contextPath}/unites?action=louer&id=' + id;
            }
        });
    }

    // Libérer une unité
    function libererUnite(id) {
        Swal.fire({
            title: 'Libérer cette unité',
            text: 'Confirmez-vous vouloir libérer cette unité ?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3498db',
            cancelButtonColor: '#95a5a6',
            confirmButtonText: 'Oui, libérer',
            cancelButtonText: 'Annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '${pageContext.request.contextPath}/unites?action=liberer&id=' + id;
            }
        });
    }

    // Confirmer la suppression
    function confirmDelete(id, numero) {
        Swal.fire({
            title: 'Confirmer la suppression',
            text: `Êtes-vous sûr de vouloir supprimer l'unité #${numero} ?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#e74c3c',
            cancelButtonColor: '#95a5a6',
            confirmButtonText: 'Oui, supprimer',
            cancelButtonText: 'Annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                deleteUnite(id);
            }
        });
    }

    // Supprimer une unité
    function deleteUnite(id) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/unites?id=' + id;

        const methodInput = document.createElement('input');
        methodInput.type = 'hidden';
        methodInput.name = '_method';
        methodInput.value = 'DELETE';

        form.appendChild(methodInput);
        document.body.appendChild(form);
        form.submit();
    }

    // Filtrer par statut
    function filterByStatus(status) {
        const cards = document.querySelectorAll('.unite-card');
        const tabs = document.querySelectorAll('.filter-tab');

        // Mettre à jour l'apparence des onglets
        tabs.forEach(tab => tab.classList.remove('active'));
        event.target.classList.add('active');

        // Filtrer les cartes
        cards.forEach(card => {
            const cardStatus = card.getAttribute('data-statut');
            if (status === 'all' || cardStatus === status) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    // Filtrer les unités par recherche
    function filterUnites() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const cards = document.querySelectorAll('.unite-card');

        cards.forEach(card => {
            const searchData = card.getAttribute('data-search').toLowerCase();
            if (searchData.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }
</script>
</body>
</html>