<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Role" %>
<%
    // Vérifier si l'utilisateur est connecté
    Object utilisateur = session.getAttribute("utilisateur");
    if (utilisateur == null) {
        // Utilisateur non connecté, rediriger vers la page de connexion
        response.sendRedirect(request.getContextPath() + "/auth?action=login");
        return;
    }

    // Récupérer le rôle de l'utilisateur
    String role = (String) session.getAttribute("role");
    String userName = (String) session.getAttribute("userName");

    // Si l'utilisateur a un autre rôle que VISITEUR, rediriger vers son dashboard approprié
    if (role != null && !role.equals("VISITEUR")) {
        String redirectURL = request.getContextPath() + "/";
        switch (role.toUpperCase()) {
            case "ADMIN":
                redirectURL = request.getContextPath() + "/admin/dashboard";
                break;
            case "PROPRIETAIRE":
                redirectURL = request.getContextPath() + "/proprietaire/dashboard";
                break;
            case "LOCATAIRE":
                redirectURL = request.getContextPath() + "/locataire/dashboard";
                break;
        }
        response.sendRedirect(redirectURL);
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Découvrez nos biens - Gestion Locative</title>
    <!-- Bootstrap CSS (local) -->
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 80px 0;
        }
        .feature-card {
            transition: transform 0.2s ease-in-out;
            border: none;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
        }
        .welcome-banner {
            background: linear-gradient(45deg, #28a745, #20c997);
            color: white;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 30px;
        }
        .btn-explore {
            background: linear-gradient(45deg, #007bff, #0056b3);
            border: none;
            border-radius: 25px;
            padding: 12px 30px;
            color: white;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        .btn-explore:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 123, 255, 0.3);
            color: white;
        }
    </style>
</head>
<body>

<!-- ✅ NAVBAR pour VISITEURS -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            🏠 Gestion Immeubles
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/immeubles">
                        🏢 Nos Immeubles
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/unites/disponibles">
                        🏠 Unités Disponibles
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/contact">
                        📞 Contact
                    </a>
                </li>
            </ul>

            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                        👋 <%= userName != null ? userName : "Visiteur" %>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><h6 class="dropdown-header">Compte Visiteur</h6></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profil">
                            👤 Mon Profil
                        </a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/mes-demandes">
                            📝 Mes Demandes de Location
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth?action=logout">
                            🚪 Déconnexion
                        </a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- Message de bienvenue -->
<%
    String welcomeMessage = (String) session.getAttribute("welcomeMessage");
    if (welcomeMessage != null) {
        session.removeAttribute("welcomeMessage");
%>
<div class="container mt-3">
    <div class="welcome-banner text-center">
        <h5 class="mb-0">🎉 <%= welcomeMessage %></h5>
    </div>
</div>
<% } %>

<!-- Section Hero -->
<section class="hero-section text-center">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <h1 class="display-4 fw-bold mb-4">
                    Trouvez votre logement idéal
                </h1>
                <p class="lead mb-5">
                    Explorez notre sélection d'immeubles et d'unités de qualité.
                    En tant que visiteur, vous pouvez consulter tous nos biens disponibles
                    et faire des demandes de location en toute simplicité.
                </p>
                <div class="d-grid d-sm-flex gap-3 justify-content-center">
                    <a href="${pageContext.request.contextPath}/immeubles"
                       class="btn btn-explore btn-lg">
                        🏢 Voir nos Immeubles
                    </a>
                    <a href="${pageContext.request.contextPath}/unites/disponibles"
                       class="btn btn-outline-light btn-lg">
                        🏠 Unités Disponibles
                    </a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Section Fonctionnalités -->
<section class="py-5">
    <div class="container">
        <div class="row text-center mb-5">
            <div class="col-12">
                <h2 class="fw-bold">Que pouvez-vous faire en tant que visiteur ?</h2>
                <p class="text-muted">Découvrez toutes les fonctionnalités à votre disposition</p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="card feature-card h-100 text-center p-4">
                    <div class="card-body">
                        <div class="display-4 text-primary mb-3">🏢</div>
                        <h5 class="card-title">Parcourir les Immeubles</h5>
                        <p class="card-text text-muted">
                            Consultez la liste complète de nos immeubles avec leurs informations
                            détaillées, photos et localisation.
                        </p>
                        <a href="${pageContext.request.contextPath}/immeubles"
                           class="btn btn-primary btn-sm">
                            Explorer
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card feature-card h-100 text-center p-4">
                    <div class="card-body">
                        <div class="display-4 text-success mb-3">🏠</div>
                        <h5 class="card-title">Unités Disponibles</h5>
                        <p class="card-text text-muted">
                            Découvrez toutes les unités disponibles à la location avec
                            leurs caractéristiques et tarifs.
                        </p>
                        <a href="${pageContext.request.contextPath}/unites/disponibles"
                           class="btn btn-success btn-sm">
                            Voir Disponibilités
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card feature-card h-100 text-center p-4">
                    <div class="card-body">
                        <div class="display-4 text-warning mb-3">📝</div>
                        <h5 class="card-title">Faire une Demande</h5>
                        <p class="card-text text-muted">
                            Intéressé par une unité ? Faites une demande de location
                            et nous vous contacterons rapidement.
                        </p>
                        <a href="${pageContext.request.contextPath}/demande-location"
                           class="btn btn-warning btn-sm">
                            Faire une Demande
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Section Informations -->
<section class="bg-light py-5">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-6">
                <h3 class="fw-bold mb-4">Comment ça marche ?</h3>
                <div class="d-flex mb-3">
                    <div class="flex-shrink-0">
                        <span class="badge bg-primary rounded-pill">1</span>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1">Explorez nos biens</h6>
                        <p class="text-muted mb-0">Parcourez nos immeubles et unités disponibles</p>
                    </div>
                </div>
                <div class="d-flex mb-3">
                    <div class="flex-shrink-0">
                        <span class="badge bg-success rounded-pill">2</span>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1">Faites votre demande</h6>
                        <p class="text-muted mb-0">Soumettez une demande pour l'unité qui vous intéresse</p>
                    </div>
                </div>
                <div class="d-flex mb-3">
                    <div class="flex-shrink-0">
                        <span class="badge bg-warning rounded-pill">3</span>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1">Devenez locataire</h6>
                        <p class="text-muted mb-0">Une fois accepté, vous aurez accès à votre espace locataire</p>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 text-center">
                <div class="bg-white p-4 rounded shadow">
                    <h5 class="text-primary mb-3">💡 Le saviez-vous ?</h5>
                    <p class="text-muted">
                        En tant que visiteur, vous pouvez explorer tous nos biens sans engagement.
                        Ce n'est qu'après validation de votre première demande de location que
                        vous obtiendrez un accès complet à l'espace locataire.
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="bg-dark text-light py-4 mt-5">
    <div class="container text-center">
        <p class="mb-0">© 2024 Gestion Immeubles. Tous droits réservés.</p>
        <p class="mb-0">
            <small>Compte visiteur -
                <a href="${pageContext.request.contextPath}/contact" class="text-light">
                    Contactez-nous pour plus d'informations
                </a>
            </small>
        </p>
    </div>
</footer>

<!-- Bootstrap JS (local) -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>