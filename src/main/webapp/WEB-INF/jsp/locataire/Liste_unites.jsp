<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Unite" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Unités Disponibles - Espace Locataire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            margin: 0;
        }
        .sidebar {
            width: 250px;
            background: #343a40;
            color: white;
            flex-shrink: 0;
        }
        .sidebar a {
            color: #ddd;
            text-decoration: none;
            display: block;
            padding: 12px 20px;
        }
        .sidebar a:hover {
            background: #495057;
            color: #fff;
        }
        .sidebar a.active {
            background: #007bff;
            color: #fff;
        }
        .content {
            flex-grow: 1;
            padding: 20px;
            background: #f8f9fa;
        }
        .property-card {
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border: none;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            background: white;
            margin-bottom: 20px;
        }
        .property-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .property-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
        }
        .property-info {
            padding: 1.5rem;
        }
        .info-item {
            display: flex;
            align-items: center;
            margin-bottom: 0.8rem;
            color: #6c757d;
        }
        .info-item i {
            width: 20px;
            margin-right: 10px;
            color: #667eea;
        }
        .price-tag {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1.1rem;
            display: inline-block;
        }
        .btn-louer {
            background: linear-gradient(135deg, #fd7e14 0%, #e83e8c 100%);
            border: none;
            border-radius: 25px;
            padding: 0.75rem 2rem;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
            color: white;
            width: 100%;
        }
        .btn-louer:hover {
            transform: scale(1.05);
            box-shadow: 0 5px 15px rgba(253, 126, 20, 0.4);
            color: white;
        }
        .status-badge {
            background: #28a745;
            color: white;
            padding: 0.25rem 0.75rem;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: bold;
        }
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 1rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
            margin-bottom: 1rem;
        }
        .stats-number {
            font-size: 2rem;
            font-weight: bold;
            color: #667eea;
            display: block;
        }
        .alert-custom {
            border-radius: 10px;
            border: none;
            font-weight: 500;
        }
        .no-properties {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .no-properties i {
            font-size: 4rem;
            margin-bottom: 1rem;
            color: #dee2e6;
        }
        .filters-section {
            background: white;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom">Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/unites" class="active">🏢 Unités Disponibles</a>
    <a href="${pageContext.request.contextPath}/contrats">📑 Mes Contrats</a>
    <a href="${pageContext.request.contextPath}/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Contenu -->
<div class="content">
    <!-- En-tête -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2><i class="fas fa-building me-2 text-primary"></i>Unités Disponibles</h2>
            <p class="text-muted mb-0">Trouvez votre logement idéal parmi nos unités disponibles</p>
        </div>
        <div class="text-end">
            <div class="stats-card d-inline-block">
                <%
                    List<Unite> unites = (List<Unite>) request.getAttribute("unites");
                    int nombreUnites = (unites != null) ? unites.size() : 0;
                %>
                <span class="stats-number"><%= nombreUnites %></span>
                <small class="text-muted">Unités disponibles</small>
            </div>
        </div>
    </div>

    <!-- Messages de notification -->
    <%
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (successMessage != null) {
            session.removeAttribute("successMessage");
    %>
    <div class="alert alert-success alert-custom alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-2"></i>
        <%= successMessage %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>
    <% if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    %>
    <div class="alert alert-danger alert-custom alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>
        <%= errorMessage %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <!-- Filtres et recherche -->
    <div class="filters-section">
        <h5 class="mb-3"><i class="fas fa-filter me-2"></i>Filtres de recherche</h5>
        <div class="row">
            <div class="col-md-4 mb-3">
                <label class="form-label">Recherche</label>
                <div class="input-group">
                    <span class="input-group-text">
                        <i class="fas fa-search"></i>
                    </span>
                    <input type="text" class="form-control" id="searchInput" placeholder="Nom d'immeuble, adresse...">
                </div>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Nombre de pièces</label>
                <select class="form-select" id="piecesFilter">
                    <option value="">Toutes les pièces</option>
                    <option value="1">1 pièce</option>
                    <option value="2">2 pièces</option>
                    <option value="3">3 pièces</option>
                    <option value="4">4+ pièces</option>
                </select>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Prix</label>
                <select class="form-select" id="prixFilter">
                    <option value="">Tous les prix</option>
                    <option value="0-50000">0 - 50.000 FCFA</option>
                    <option value="50000-100000">50.000 - 100.000 FCFA</option>
                    <option value="100000-200000">100.000 - 200.000 FCFA</option>
                    <option value="200000+">200.000+ FCFA</option>
                </select>
            </div>
        </div>
        <div class="text-end">
            <button class="btn btn-outline-secondary" onclick="clearFilters()">
                <i class="fas fa-times me-1"></i>Effacer les filtres
            </button>
        </div>
    </div>

    <!-- Liste des unités -->
    <div class="row" id="propertiesContainer">
        <%
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);

            if (unites != null && !unites.isEmpty()) {
                for (Unite unite : unites) {
                    if (unite.getStatut() == Unite.StatutUnite.DISPONIBLE) {
        %>
        <div class="col-lg-4 col-md-6 mb-4 property-item"
             data-pieces="<%= unite.getNbPieces() %>"
             data-prix="<%= unite.getLoyer().intValue() %>"
             data-search="<%= unite.getImmeuble().getNom().toLowerCase() + " " + unite.getImmeuble().getAdresse().toLowerCase() %>">
            <div class="card property-card h-100">
                <div class="property-header">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h5 class="card-title mb-1">
                                <i class="fas fa-building me-2"></i>
                                <%= unite.getImmeuble().getNom() %>
                            </h5>
                            <p class="mb-0 opacity-75">
                                <i class="fas fa-map-marker-alt me-1"></i>
                                <%= unite.getImmeuble().getAdresse() %>
                            </p>
                        </div>
                        <span class="status-badge">
                            <i class="fas fa-check-circle me-1"></i>
                            Disponible
                        </span>
                    </div>
                </div>

                <div class="property-info">
                    <div class="mb-3">
                        <strong class="text-primary">Unité N° <%= unite.getNumero() %></strong>
                    </div>

                    <div class="info-item">
                        <i class="fas fa-door-open"></i>
                        <span><strong><%= unite.getNbPieces() %></strong> pièce<%= unite.getNbPieces() > 1 ? "s" : "" %></span>
                    </div>

                    <div class="info-item">
                        <i class="fas fa-expand-arrows-alt"></i>
                        <span><strong><%= formatter.format(unite.getSuperficie()) %></strong> m²</span>
                    </div>

                    <div class="info-item">
                        <i class="fas fa-user"></i>
                        <span>Propriétaire: <strong><%= unite.getImmeuble().getProprietaire().getNom() %> <%= unite.getImmeuble().getProprietaire().getPrenom() %></strong></span>
                    </div>

                    <% if (unite.getImmeuble().getDescription() != null && !unite.getImmeuble().getDescription().trim().isEmpty()) { %>
                    <div class="info-item">
                        <i class="fas fa-info-circle"></i>
                        <span><%= unite.getImmeuble().getDescription() %></span>
                    </div>
                    <% } %>

                    <hr>

                    <div class="d-flex justify-content-center mb-3">
                        <span class="price-tag">
                            <%= formatter.format(unite.getLoyer()) %> FCFA/mois
                        </span>
                    </div>

                    <!-- Bouton de location -->
                    <form method="post" action="${pageContext.request.contextPath}/unites" class="d-grid">
                        <input type="hidden" name="action" value="louer">
                        <input type="hidden" name="uniteId" value="<%= unite.getId() %>">
                        <button type="submit" class="btn btn-louer"
                                onclick="return confirm('Êtes-vous sûr de vouloir louer cette unité ? Cette action changera son statut à LOUÉE.')">
                            <i class="fas fa-key me-2"></i>
                            Louer cette unité
                        </button>
                    </form>
                </div>
            </div>
        </div>
        <%
                }
            }
        } else {
        %>
        <div class="col-12">
            <div class="no-properties">
                <i class="fas fa-home"></i>
                <h3>Aucune unité disponible</h3>
                <p class="text-muted">Il n'y a actuellement aucune unité disponible à la location.</p>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                    <i class="fas fa-arrow-left me-2"></i>
                    Retour au dashboard
                </a>
            </div>
        </div>
        <% } %>
    </div>

    <!-- Message si aucun résultat après filtrage -->
    <div class="row" id="noResultsMessage" style="display: none;">
        <div class="col-12">
            <div class="no-properties">
                <i class="fas fa-search"></i>
                <h3>Aucun résultat</h3>
                <p class="text-muted">Aucune unité ne correspond à vos critères de recherche.</p>
                <button class="btn btn-primary" onclick="clearFilters()">
                    <i class="fas fa-times me-2"></i>
                    Effacer les filtres
                </button>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script>
    // Fonction de recherche et filtrage
    function filterProperties() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const piecesFilter = document.getElementById('piecesFilter').value;
        const prixFilter = document.getElementById('prixFilter').value;

        const properties = document.querySelectorAll('.property-item');
        let visibleCount = 0;

        properties.forEach(property => {
            const searchData = property.getAttribute('data-search');
            const pieces = parseInt(property.getAttribute('data-pieces'));
            const prix = parseInt(property.getAttribute('data-prix'));

            let showProperty = true;

            // Filtre de recherche textuelle
            if (searchTerm && !searchData.includes(searchTerm)) {
                showProperty = false;
            }

            // Filtre par nombre de pièces
            if (piecesFilter) {
                if (piecesFilter === '4' && pieces < 4) {
                    showProperty = false;
                } else if (piecesFilter !== '4' && pieces !== parseInt(piecesFilter)) {
                    showProperty = false;
                }
            }

            // Filtre par prix
            if (prixFilter) {
                const [min, max] = prixFilter.split('-');
                if (max === undefined) { // Format "200000+"
                    if (prix < parseInt(min.replace('+', ''))) {
                        showProperty = false;
                    }
                } else {
                    if (prix < parseInt(min) || prix > parseInt(max)) {
                        showProperty = false;
                    }
                }
            }

            if (showProperty) {
                property.style.display = 'block';
                visibleCount++;
            } else {
                property.style.display = 'none';
            }
        });

        // Afficher/masquer le message "aucun résultat"
        const noResultsMessage = document.getElementById('noResultsMessage');
        if (visibleCount === 0 && properties.length > 0) {
            noResultsMessage.style.display = 'block';
        } else {
            noResultsMessage.style.display = 'none';
        }
    }

    // Fonction pour effacer tous les filtres
    function clearFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('piecesFilter').value = '';
        document.getElementById('prixFilter').value = '';
        filterProperties();
    }

    // Ajout des écouteurs d'événements
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('searchInput').addEventListener('input', filterProperties);
        document.getElementById('piecesFilter').addEventListener('change', filterProperties);
        document.getElementById('prixFilter').addEventListener('change', filterProperties);

        // Animation d'apparition des cartes
        const cards = document.querySelectorAll('.property-card');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            setTimeout(() => {
                card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });

        // Auto-dismiss des alertes après 5 secondes
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert && !alert.classList.contains('d-none')) {
                    const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                    bsAlert.close();
                }
            }, 5000);
        });
    });
</script>
</body>
</html>