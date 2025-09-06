<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Immeubles</title>
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
            --dark-color: #34495e;
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
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
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

        .page-header .subtitle {
            opacity: 0.9;
            margin-top: 10px;
            font-size: 1.1rem;
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

        .immeubles-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }

        .immeuble-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border: 1px solid #e0e6ed;
            position: relative;
            overflow: hidden;
        }

        .immeuble-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--primary-color) 0%, var(--secondary-color) 100%);
        }

        .immeuble-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        }

        .immeuble-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .immeuble-title {
            font-size: 1.4rem;
            font-weight: 700;
            color: var(--primary-color);
            margin: 0;
        }

        .immeuble-id {
            background: linear-gradient(135deg, var(--secondary-color), #5dade2);
            color: white;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .immeuble-info {
            margin-bottom: 20px;
        }

        .info-item {
            display: flex;
            align-items: center;
            margin-bottom: 12px;
            color: #5a6c7d;
        }

        .info-item i {
            width: 20px;
            margin-right: 12px;
            color: var(--secondary-color);
        }

        .immeuble-description {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-style: italic;
            color: #6c757d;
            border-left: 4px solid var(--secondary-color);
        }

        .proprietaire-info {
            background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%);
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 4px solid var(--success-color);
        }

        .proprietaire-info .name {
            font-weight: 600;
            color: var(--success-color);
            margin-bottom: 5px;
        }

        .proprietaire-info .email {
            font-size: 0.9rem;
            color: #6c757d;
        }

        .card-actions {
            display: flex;
            gap: 10px;
            justify-content: center;
            padding-top: 15px;
            border-top: 1px solid #e9ecef;
            flex-wrap: wrap;
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
        }

        .btn-view {
            background: linear-gradient(135deg, #3498db, #5dade2);
            color: white;
        }

        .btn-edit {
            background: linear-gradient(135deg, #f39c12, #f4d03f);
            color: white;
        }

        .btn-delete {
            background: linear-gradient(135deg, #e74c3c, #ec7063);
            color: white;
        }

        .btn-unites {
            background: linear-gradient(135deg, #9b59b6, #bb8fce);
            color: white;
        }

        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            color: white;
        }

        .stats-bar {
            background: white;
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 25px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .stat-item {
            text-align: center;
        }

        .stat-number {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        .stat-label {
            color: #7f8c8d;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.9rem;
        }

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
            .immeubles-grid {
                grid-template-columns: 1fr;
            }
            .page-header h1 {
                font-size: 2rem;
            }
            .card-actions {
                gap: 5px;
            }
            .btn-action {
                font-size: 0.8rem;
                padding: 6px 12px;
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
        <h1><i class="fas fa-building"></i> Gestion des Immeubles</h1>
        <div class="subtitle">Gérez vos biens immobiliers en toute simplicité</div>
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
    <div class="stats-bar">
        <div class="row">
            <div class="col-md-4">
                <div class="stat-item">
                    <div class="stat-number">${immeubles.size()}</div>
                    <div class="stat-label">Immeubles</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-item">
                    <div class="stat-number" id="totalUnites">0</div>
                    <div class="stat-label">Unités Total</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-item">
                    <div class="stat-number" id="unitesDisponibles">0</div>
                    <div class="stat-label">Disponibles</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Barre d'actions -->
    <div class="action-bar">
        <div class="row align-items-center">
            <div class="col-md-6">
                <button type="button" class="btn btn-add" onclick="showAddModal()">
                    <i class="fas fa-plus"></i> Ajouter un Immeuble
                </button>
            </div>
            <div class="col-md-6">
                <div class="search-box">
                    <input type="text" class="form-control" id="searchInput"
                           placeholder="Rechercher un immeuble..." onkeyup="filterImmeubles()">
                    <i class="fas fa-search"></i>
                </div>
            </div>
        </div>
    </div>

    <!-- Liste des immeubles -->
    <div id="immeublesContainer">
        <c:choose>
            <c:when test="${not empty immeubles}">
                <div class="immeubles-grid" id="immeublesGrid">
                    <c:forEach var="immeuble" items="${immeubles}">
                        <div class="immeuble-card" data-search="${immeuble.nom} ${immeuble.adresse} ${immeuble.proprietaire.nom} ${immeuble.proprietaire.prenom}">
                            <div class="immeuble-header">
                                <h3 class="immeuble-title">${immeuble.nom}</h3>
                                <span class="immeuble-id">#${immeuble.id}</span>
                            </div>

                            <div class="immeuble-info">
                                <div class="info-item">
                                    <i class="fas fa-map-marker-alt"></i>
                                    <span>${immeuble.adresse}</span>
                                </div>
                            </div>

                            <c:if test="${not empty immeuble.description}">
                                <div class="immeuble-description">
                                    <i class="fas fa-quote-left"></i>
                                        ${immeuble.description}
                                </div>
                            </c:if>

                            <div class="proprietaire-info">
                                <div class="name">
                                    <i class="fas fa-user"></i>
                                        ${immeuble.proprietaire.prenom} ${immeuble.proprietaire.nom}
                                </div>
                                <div class="email">
                                    <i class="fas fa-envelope"></i>
                                        ${immeuble.proprietaire.email}
                                </div>
                            </div>

                            <div class="card-actions">
                                <a href="${pageContext.request.contextPath}/immeubles?id=${immeuble.id}"
                                   class="btn-action btn-view">
                                    <i class="fas fa-eye"></i> Voir
                                </a>
                                <button type="button" class="btn-action btn-edit"
                                        onclick="showEditModal(${immeuble.id}, '${immeuble.nom}', '${immeuble.adresse}', '${immeuble.description}', ${immeuble.proprietaire.id})">
                                    <i class="fas fa-edit"></i> Modifier
                                </button>
                                <a href="${pageContext.request.contextPath}/unites?immeubleId=${immeuble.id}"
                                   class="btn-action btn-unites">
                                    <i class="fas fa-home"></i> Unités
                                </a>
                                <button type="button" class="btn-action btn-delete"
                                        onclick="confirmDelete(${immeuble.id}, '${immeuble.nom}')">
                                    <i class="fas fa-trash"></i> Supprimer
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <i class="fas fa-building"></i>
                    <h3>Aucun immeuble trouvé</h3>
                    <p>Commencez par ajouter votre premier immeuble</p>
                    <button type="button" class="btn btn-add" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> Ajouter un Immeuble
                    </button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Modal Ajouter/Modifier Immeuble -->
<div class="modal fade" id="immeubleModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">
                    <i class="fas fa-plus"></i> Ajouter un Immeuble
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="immeubleForm" method="post" action="${pageContext.request.contextPath}/immeubles">
                <div class="modal-body">
                    <input type="hidden" id="immeubleId" name="id">
                    <input type="hidden" name="proprietaireId" value="${sessionScope.utilisateur.id}">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="nom" class="form-label">
                                    <i class="fas fa-building text-primary"></i>
                                    Nom de l'immeuble <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="nom" name="nom" required
                                       minlength="3" maxlength="100" placeholder="Ex: Résidence les Palmiers">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="adresse" class="form-label">
                                    <i class="fas fa-map-marker-alt text-danger"></i>
                                    Adresse <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="adresse" name="adresse" required
                                       placeholder="Ex: 15 Avenue Senghor, Dakar">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">
                            <i class="fas fa-file-alt text-info"></i>
                            Description
                        </label>
                        <textarea class="form-control" id="description" name="description"
                                  rows="4" maxlength="255"
                                  placeholder="Décrivez les caractéristiques de l'immeuble..."></textarea>
                        <div class="form-text">
                            <span id="charCount">0</span>/255 caractères
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
        initializeCharCounter();
    });

    // Compteur de caractères
    function initializeCharCounter() {
        const textarea = document.getElementById('description');
        const counter = document.getElementById('charCount');

        if (textarea && counter) {
            textarea.addEventListener('input', function() {
                counter.textContent = this.value.length;
                if (this.value.length > 200) {
                    counter.classList.add('text-warning');
                } else {
                    counter.classList.remove('text-warning');
                }
            });
        }
    }

    // Mettre à jour les statistiques
    function updateStats() {
        const cards = document.querySelectorAll('.immeuble-card');
        let totalUnites = 0;
        let unitesDisponibles = 0;

        // Simulation du calcul (à remplacer par des données réelles)
        cards.forEach(card => {
            totalUnites += Math.floor(Math.random() * 10) + 1;
            unitesDisponibles += Math.floor(Math.random() * 5);
        });

        document.getElementById('totalUnites').textContent = totalUnites;
        document.getElementById('unitesDisponibles').textContent = unitesDisponibles;
    }

    // Afficher le modal d'ajout
    function showAddModal() {
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-plus"></i> Ajouter un Immeuble';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Ajouter';
        document.getElementById('immeubleForm').reset();
        document.getElementById('immeubleId').value = '';
        document.getElementById('charCount').textContent = '0';

        const modal = new bootstrap.Modal(document.getElementById('immeubleModal'));
        modal.show();
    }

    // Afficher le modal de modification
    function showEditModal(id, nom, adresse, description, proprietaireId) {
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Modifier l\'Immeuble';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Modifier';

        document.getElementById('immeubleId').value = id;
        document.getElementById('nom').value = nom;
        document.getElementById('adresse').value = adresse;
        document.getElementById('description').value = description || '';
        document.getElementById('charCount').textContent = (description || '').length;

        // Modifier l'action du formulaire pour inclure l'ID
        const form = document.getElementById('immeubleForm');
        form.action = '${pageContext.request.contextPath}/immeubles?id=' + id;

        const modal = new bootstrap.Modal(document.getElementById('immeubleModal'));
        modal.show();
    }

    // Confirmer la suppression
    function confirmDelete(id, nom) {
        Swal.fire({
            title: 'Confirmer la suppression',
            text: `Êtes-vous sûr de vouloir supprimer l'immeuble "${nom}" ?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#e74c3c',
            cancelButtonColor: '#95a5a6',
            confirmButtonText: 'Oui, supprimer',
            cancelButtonText: 'Annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                deleteImmeuble(id);
            }
        });
    }

    // Supprimer un immeuble
    function deleteImmeuble(id) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/immeubles?id=' + id;

        const methodInput = document.createElement('input');
        methodInput.type = 'hidden';
        methodInput.name = '_method';
        methodInput.value = 'DELETE';

        form.appendChild(methodInput);
        document.body.appendChild(form);
        form.submit();
    }

    // Filtrer les immeubles
    function filterImmeubles() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const cards = document.querySelectorAll('.immeuble-card');

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