<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Locataires</title>
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
        .stats-card-locataires {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s;
        }
        .card:hover {
            transform: translateY(-2px);
        }
        .search-box {
            border-radius: 20px;
            border: 1px solid #e0e0e0;
            padding: 10px 20px;
        }
        .avatar-circle {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/locataires">
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
                <h1 class="h2"><i class="fas fa-user-friends text-success"></i> Liste des Locataires</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <button type="button" class="btn btn-outline-secondary">
                            <i class="fas fa-download"></i> Exporter
                        </button>
                        <button type="button" class="btn btn-outline-secondary" onclick="refreshStats()">
                            <i class="fas fa-sync-alt"></i> Actualiser
                        </button>
                    </div>
                </div>
            </div>

            <!-- Alert messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Statistics card -->
            <div class="row mb-4">
                <div class="col-xl-3 col-lg-6 col-md-6 mb-4">
                    <div class="card stats-card stats-card-locataires text-white">
                        <div class="card-body position-relative">
                            <div class="row">
                                <div class="col">
                                    <h6 class="text-uppercase mb-1 opacity-75">Locataires</h6>
                                    <div class="stats-number">${nbLocataires != null ? nbLocataires : 0}</div>
                                    <small class="opacity-75">
                                        <i class="fas fa-user-friends"></i> Actifs
                                    </small>
                                </div>
                            </div>
                            <i class="fas fa-user-friends stats-icon"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Search and filter -->
            <div class="row mb-3">
                <div class="col-md-6">
                    <div class="input-group">
                        <input type="text" class="form-control search-box" id="searchInput"
                               placeholder="Rechercher un locataire..." onkeyup="filterTable()">
                        <span class="input-group-text"><i class="fas fa-search"></i></span>
                    </div>
                </div>
                <div class="col-md-6 text-end">
                    <select class="form-select" id="statusFilter" onchange="filterByStatus()" style="width: auto; display: inline-block;">
                        <option value="">Tous les statuts</option>
                        <option value="ACTIF">Actifs</option>
                        <option value="INACTIF">Inactifs</option>
                    </select>
                </div>
            </div>

            <!-- Users table -->
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-list"></i> Liste des Locataires
                        <span class="badge bg-success">${nbLocataires != null ? nbLocataires : 0}</span>
                    </h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty locataires}">
                            <div class="table-responsive">
                                <table class="table table-hover" id="locatairesTable">
                                    <thead class="table-light">
                                    <tr>
                                        <th><i class="fas fa-user"></i> Nom & Prénom</th>
                                        <th><i class="fas fa-envelope"></i> Email</th>
                                        <th><i class="fas fa-phone"></i> Téléphone</th>
                                        <th><i class="fas fa-calendar"></i> Date d'inscription</th>
                                        <th><i class="fas fa-home"></i> Logement</th>
                                        <th><i class="fas fa-toggle-on"></i> Statut</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="locataire" items="${locataires}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar-circle me-2">
                                                        <i class="fas fa-user-friends"></i>
                                                    </div>
                                                    <div>
                                                        <strong>${locataire.nom} ${locataire.prenom}</strong>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <a href="mailto:${locataire.email}" class="text-decoration-none">
                                                        ${locataire.email}
                                                </a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty locataire.telephone}">
                                                        <a href="tel:${locataire.telephone}" class="text-decoration-none">
                                                                ${locataire.telephone}
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Non renseigné</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${locataire.dateCreation}" pattern="dd/MM/yyyy"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty locataire.logementActuel}">
                                                        <span class="badge bg-info">
                                                            <i class="fas fa-home"></i>
                                                            ${locataire.logementActuel}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning text-dark">
                                                            <i class="fas fa-question-circle"></i> Sans logement
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${locataire.actif}">
                                                        <span class="badge bg-success">
                                                            <i class="fas fa-check-circle"></i> Actif
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">
                                                            <i class="fas fa-times-circle"></i> Inactif
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5">
                                <i class="fas fa-user-friends fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">Aucun locataire trouvé</h5>
                                <p class="text-muted">Il n'y a actuellement aucun locataire enregistré dans le système.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Function to refresh statistics
    function refreshStats() {
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
                <i class="fas fa-check-circle"></i> Données mises à jour avec succès
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

    // Search functionality
    function filterTable() {
        const input = document.getElementById('searchInput');
        const filter = input.value.toUpperCase();
        const table = document.getElementById('locatairesTable');
        const tbody = table.getElementsByTagName('tbody')[0];
        const tr = tbody.getElementsByTagName('tr');

        for (let i = 0; i < tr.length; i++) {
            const td = tr[i].getElementsByTagName('td');
            let found = false;

            for (let j = 0; j < td.length; j++) {
                if (td[j] && td[j].textContent.toUpperCase().indexOf(filter) > -1) {
                    found = true;
                    break;
                }
            }

            tr[i].style.display = found ? "" : "none";
        }
    }

    // Filter by status functionality
    function filterByStatus() {
        const statusFilter = document.getElementById('statusFilter');
        const selectedStatus = statusFilter.value.toUpperCase();
        const table = document.getElementById('locatairesTable');
        const tbody = table.getElementsByTagName('tbody')[0];
        const tr = tbody.getElementsByTagName('tr');

        for (let i = 0; i < tr.length; i++) {
            const statusCell = tr[i].getElementsByTagName('td')[5]; // Status column

            if (selectedStatus === "" || statusCell.textContent.toUpperCase().indexOf(selectedStatus) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }

    // Auto-dismiss alerts after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
</script>
</body>
</html>