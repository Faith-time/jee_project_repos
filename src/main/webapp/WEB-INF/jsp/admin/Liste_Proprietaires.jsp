<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Propriétaires</title>
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
        .card {
            border: none;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s;
        }
        .card:hover {
            transform: translateY(-2px);
        }
        .badge {
            font-size: 0.75em;
        }
        .search-box {
            border-radius: 20px;
            border: 1px solid #e0e0e0;
            padding: 10px 20px;
        }
        .stats-card {
            background: linear-gradient(135deg, #007bff 0%, #6610f2 100%);
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/utilisateurs">
                            <i class="fas fa-users"></i> Tous les utilisateurs
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/proprietaires">
                            <i class="fas fa-home"></i> Propriétaires
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/locataires">
                            <i class="fas fa-user-friends"></i> Locataires
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/rapports">
                            <i class="fas fa-chart-bar"></i> Rapports
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
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2"><i class="fas fa-home text-primary"></i> Liste des Propriétaires</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <button type="button" class="btn btn-outline-secondary">
                            <i class="fas fa-download"></i> Exporter
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
                <div class="col-md-4">
                    <div class="card stats-card">
                        <div class="card-body text-center">
                            <i class="fas fa-home fa-2x mb-2"></i>
                            <h3 class="card-title">${nbProprietaires != null ? nbProprietaires : 0}</h3>
                            <p class="card-text">Propriétaires actifs</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Search and filter -->
            <div class="row mb-3">
                <div class="col-md-6">
                    <div class="input-group">
                        <input type="text" class="form-control search-box" id="searchInput"
                               placeholder="Rechercher un propriétaire..." onkeyup="filterTable()">
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
                        <i class="fas fa-list"></i> Liste des Propriétaires
                        <span class="badge bg-primary">${nbProprietaires != null ? nbProprietaires : 0}</span>
                    </h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty utilisateurs}">
                            <div class="table-responsive">
                                <table class="table table-hover" id="proprietairesTable">
                                    <thead class="table-light">
                                    <tr>
                                        <th><i class="fas fa-user"></i> Nom & Prénom</th>
                                        <th><i class="fas fa-envelope"></i> Email</th>
                                        <th><i class="fas fa-phone"></i> Téléphone</th>
                                        <th><i class="fas fa-calendar"></i> Date d'inscription</th>
                                        <th><i class="fas fa-building"></i> Propriétés</th>
                                        <th><i class="fas fa-toggle-on"></i> Statut</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="proprietaire" items="${utilisateurs}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar-circle me-2">
                                                        <i class="fas fa-home text-primary"></i>
                                                    </div>
                                                    <div>
                                                        <strong>${proprietaire.nom} ${proprietaire.prenom}</strong>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <a href="mailto:${proprietaire.email}" class="text-decoration-none">
                                                        ${proprietaire.email}
                                                </a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty proprietaire.telephone}">
                                                        <a href="tel:${proprietaire.telephone}" class="text-decoration-none">
                                                                ${proprietaire.telephone}
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Non renseigné</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${proprietaire.dateCreation}" pattern="dd/MM/yyyy"/>
                                            </td>
                                            <td>
                                                        <span class="badge bg-info">
                                                            <i class="fas fa-building"></i>
                                                            ${proprietaire.nombreProprietes != null ? proprietaire.nombreProprietes : 0}
                                                        </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${proprietaire.actif}">
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
                                <i class="fas fa-home fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">Aucun propriétaire trouvé</h5>
                                <p class="text-muted">Il n'y a actuellement aucun propriétaire enregistré dans le système.</p>
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
    // Search functionality
    function filterTable() {
        const input = document.getElementById('searchInput');
        const filter = input.value.toUpperCase();
        const table = document.getElementById('proprietairesTable');
        const tbody = table.getElementsByTagName('tbody')[0];
        const tr = tbody.getElementsByTagName('tr');

        for (let i = 0; i < tr.length; i++) {
            const td = tr[i].getElementsByTagName('td');
            let found = false;

            for (let j = 0; j < td.length; j++) { // Check all columns
                if (td[j] && td[j].textContent.toUpperCase().indexOf(filter) > -1) {
                    found = true;
                    break;
                }
            }

            tr[i].style.display = found ? "" : "none";
        }
    }
</script>
</body>
</html>