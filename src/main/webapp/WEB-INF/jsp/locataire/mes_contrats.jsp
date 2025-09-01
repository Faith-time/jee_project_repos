<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Contrat" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Locataire" %>

<%
    Locataire locataireConnecte = (Locataire) session.getAttribute("utilisateur");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Contrats de Location</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .contract-timeline {
            position: relative;
            padding-left: 2rem;
        }
        .contract-timeline::before {
            content: '';
            position: absolute;
            left: 15px;
            top: 0;
            bottom: 0;
            width: 2px;
            background: #e9ecef;
        }
        .timeline-item {
            position: relative;
            margin-bottom: 2rem;
        }
        .timeline-item::before {
            content: '';
            position: absolute;
            left: -25px;
            top: 20px;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #007bff;
            border: 3px solid white;
            box-shadow: 0 0 0 2px #007bff;
        }
        .contract-card {
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }
        .contract-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        .status-active {
            background: linear-gradient(45deg, #28a745, #20c997);
            color: white;
        }
        .status-ending {
            background: linear-gradient(45deg, #ffc107, #fd7e14);
            color: white;
        }
        .status-expired {
            background: linear-gradient(45deg, #dc3545, #e83e8c);
            color: white;
        }
        .payment-summary {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
        }
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            color: #6c757d;
        }
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body class="bg-light">
<div class="container mt-4">
    <!-- En-tête avec profil du locataire -->
    <div class="profile-header">
        <div class="row align-items-center">
            <div class="col-md-8">
                <div class="d-flex align-items-center">
                    <div class="me-4">
                        <div class="bg-white bg-opacity-20 rounded-circle p-3">
                            <i class="fas fa-user fa-2x"></i>
                        </div>
                    </div>
                    <div>
                        <h2 class="mb-1">Bonjour, <%= locataireConnecte.getPrenom() %> <%= locataireConnecte.getNom() %></h2>
                        <p class="mb-0 opacity-75">
                            <i class="fas fa-envelope me-2"></i><%= locataireConnecte.getEmail() %>
                        </p>
                    </div>
                </div>
            </div>
            <div class="col-md-4 text-md-end">
                <div class="d-flex justify-content-md-end gap-2">
                    <div class="text-center">
                        <h4 class="mb-0">${contrats.size()}</h4>
                        <small class="opacity-75">Contrat(s)</small>
                    </div>
                    <div class="vr mx-3"></div>
                    <div class="text-center">
                        <h4 class="mb-0" id="totalPaiements">0</h4>
                        <small class="opacity-75">Paiement(s)</small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Navigation rapide -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-center border-primary">
                <div class="card-body">
                    <i class="fas fa-file-contract fa-2x text-primary mb-2"></i>
                    <h6>Mes Contrats</h6>
                    <span class="badge bg-primary">${contrats.size()}</span>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center border-success">
                <div class="card-body">
                    <i class="fas fa-credit-card fa-2x text-success mb-2"></i>
                    <h6>Paiements</h6>
                    <span class="badge bg-success" id="badgePaiements">0</span>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center border-warning">
                <div class="card-body">
                    <i class="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                    <h6>À échéance</h6>
                    <span class="badge bg-warning" id="badgeEcheance">0</span>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center border-info">
                <div class="card-body">
                    <i class="fas fa-calendar fa-2x text-info mb-2"></i>
                    <h6>Historique</h6>
                    <span class="badge bg-info">Voir tout</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Liste des contrats -->
    <div class="row">
        <div class="col-12">
            <c:choose>
                <c:when test="${empty contrats}">
                    <!-- État vide -->
                    <div class="card contract-card">
                        <div class="card-body">
                            <div class="empty-state">
                                <i class="fas fa-file-contract"></i>
                                <h4>Aucun contrat trouvé</h4>
                                <p class="mb-4">Vous n'avez actuellement aucun contrat de location.</p>
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle"></i>
                                    <strong>Information :</strong> Contactez votre propriétaire pour établir un contrat de location.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Timeline des contrats -->
                    <div class="contract-timeline">
                        <c:forEach var="contrat" items="${contrats}" varStatus="status">
                            <div class="timeline-item">
                                <div class="contract-card card">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <div>
                                            <h5 class="mb-1">
                                                <i class="fas fa-building text-primary"></i>
                                                    ${contrat.unite.immeuble.nom}
                                            </h5>
                                            <small class="text-muted">
                                                <i class="fas fa-home"></i>
                                                Unité ${contrat.unite.numero}
                                                <c:if test="${contrat.unite.nbPieces != null}">
                                                    - ${contrat.unite.nbPieces} pièce(s)
                                                </c:if>
                                                <c:if test="${contrat.unite.superficie != null}">
                                                    - ${contrat.unite.superficie}m²
                                                </c:if>
                                            </small>
                                        </div>
                                        <span class="badge status-active" id="status-badge-${contrat.id}">
                                                Actif
                                            </span>
                                    </div>

                                    <div class="card-body">
                                        <div class="row">
                                            <!-- Informations principales -->
                                            <div class="col-md-6">
                                                <h4 class="text-primary mb-3">
                                                    <fmt:formatNumber value="${contrat.montant}" pattern="#,##0"/> FCFA
                                                    <small class="text-muted fs-6">/mois</small>
                                                </h4>

                                                <div class="mb-3">
                                                    <div class="d-flex justify-content-between mb-1">
                                                        <small class="text-muted">Début du contrat</small>
                                                        <small class="fw-bold text-success">
                                                            <fmt:formatDate value="${contrat.dateDebut}" pattern="dd/MM/yyyy"/>
                                                        </small>
                                                    </div>
                                                    <div class="d-flex justify-content-between">
                                                        <small class="text-muted">Fin du contrat</small>
                                                        <small class="fw-bold text-danger">
                                                            <c:choose>
                                                                <c:when test="${contrat.dateFin != null}">
                                                                    <fmt:formatDate value="${contrat.dateFin}" pattern="dd/MM/yyyy"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-info">Durée indéterminée</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </small>
                                                    </div>
                                                </div>

                                                <!-- Adresse de l'immeuble -->
                                                <div class="border-start border-primary border-3 ps-3">
                                                    <small class="text-muted d-block">Adresse</small>
                                                    <strong>${contrat.unite.immeuble.adresse}</strong>
                                                    <c:if test="${contrat.unite.immeuble.description != null}">
                                                        <br>
                                                        <small class="text-muted">${contrat.unite.immeuble.description}</small>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <!-- Résumé des paiements -->
                                            <div class="col-md-6">
                                                <div class="payment-summary">
                                                    <h6 class="mb-3">
                                                        <i class="fas fa-chart-line text-success"></i>
                                                        Résumé des paiements
                                                    </h6>

                                                    <c:choose>
                                                        <c:when test="${contrat.paiements != null && !contrat.paiements.isEmpty()}">
                                                            <div class="row text-center">
                                                                <div class="col-6">
                                                                    <div class="mb-2">
                                                                        <h5 class="text-primary mb-0">${contrat.paiements.size()}</h5>
                                                                        <small class="text-muted">Paiement(s)</small>
                                                                    </div>
                                                                </div>
                                                                <div class="col-6">
                                                                    <div class="mb-2">
                                                                        <h5 class="text-success mb-0">
                                                                            <c:set var="totalPaye" value="0"/>
                                                                            <c:forEach var="paiement" items="${contrat.paiements}">
                                                                                <c:set var="totalPaye" value="${totalPaye + paiement.montant}"/>
                                                                            </c:forEach>
                                                                            <fmt:formatNumber value="${totalPaye}" pattern="#,##0"/>
                                                                        </h5>
                                                                        <small class="text-muted">FCFA payés</small>
                                                                    </div>
                                                                </div>
                                                            </div>

                                                            <!-- Dernier paiement -->
                                                            <div class="border-top pt-2 mt-2">
                                                                <small class="text-muted">Dernier paiement :</small>
                                                                <c:set var="dernierPaiement" value="${contrat.paiements[contrat.paiements.size()-1]}"/>
                                                                <div class="d-flex justify-content-between">
                                                                    <strong>
                                                                        <fmt:formatNumber value="${dernierPaiement.montant}" pattern="#,##0"/> FCFA
                                                                    </strong>
                                                                    <small class="text-success">
                                                                        <fmt:formatDate value="${dernierPaiement.datePaiement}" pattern="dd/MM/yyyy"/>
                                                                    </small>
                                                                </div>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="text-center py-3">
                                                                <i class="fas fa-credit-card fa-2x text-muted mb-2"></i>
                                                                <p class="text-muted mb-0">Aucun paiement enregistré</p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="card-footer bg-white">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <small class="text-muted">
                                                    <i class="fas fa-calendar-alt"></i>
                                                    Contrat #${contrat.id}
                                                    <c:choose>
                                                        <c:when test="${contrat.dateFin != null}">
                                                            <c:set var="today" value="<%= new java.util.Date() %>"/>
                                                            <c:choose>
                                                                <c:when test="${contrat.dateFin lt today}">
                                                                    - <span class="text-danger">Expiré</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    - <span class="text-success">En cours</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            - <span class="text-info">Durée indéterminée</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </small>
                                            </div>

                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/contrats/${contrat.id}/details"
                                                   class="btn btn-outline-primary" title="Voir détails">
                                                    <i class="fas fa-eye"></i> Détails
                                                </a>
                                                <c:if test="${contrat.paiements != null && !contrat.paiements.isEmpty()}">
                                                    <a href="${pageContext.request.contextPath}/contrats/${contrat.id}/paiements"
                                                       class="btn btn-outline-success" title="Voir paiements">
                                                        <i class="fas fa-credit-card"></i> Paiements
                                                    </a>
                                                </c:if>
                                                <button type="button" class="btn btn-outline-info"
                                                        onclick="telechargerContrat(${contrat.id})" title="Télécharger PDF">
                                                    <i class="fas fa-download"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Actions rapides -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-tools text-primary"></i>
                        Actions rapides
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-md-3 mb-3">
                            <a href="${pageContext.request.contextPath}/paiements/nouveau" class="btn btn-outline-success w-100">
                                <i class="fas fa-plus-circle fa-2x d-block mb-2"></i>
                                Déclarer un paiement
                            </a>
                        </div>
                        <div class="col-md-3 mb-3">
                            <a href="${pageContext.request.contextPath}/reclamations/nouvelle" class="btn btn-outline-warning w-100">
                                <i class="fas fa-exclamation-triangle fa-2x d-block mb-2"></i>
                                Faire une réclamation
                            </a>
                        </div>
                        <div class="col-md-3 mb-3">
                            <a href="${pageContext.request.contextPath}/profil" class="btn btn-outline-info w-100">
                                <i class="fas fa-user-edit fa-2x d-block mb-2"></i>
                                Modifier profil
                            </a>
                        </div>
                        <div class="col-md-3 mb-3">
                            <a href="${pageContext.request.contextPath}/aide" class="btn btn-outline-secondary w-100">
                                <i class="fas fa-question-circle fa-2x d-block mb-2"></i>
                                Aide & Contact
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal de confirmation pour téléchargement -->
<div class="modal fade" id="downloadModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-download text-primary"></i>
                    Télécharger le contrat
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-file-pdf fa-3x text-danger mb-3"></i>
                <p>Télécharger le contrat au format PDF ?</p>
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    Le document inclura tous les détails du contrat et l'historique des paiements.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <button type="button" class="btn btn-primary" id="confirmDownload">
                    <i class="fas fa-download"></i> Télécharger PDF
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>

<!-- Scripts personnalisés -->
<script>
    let contratToDownload = null;

    document.addEventListener('DOMContentLoaded', function() {
        updateStatistics();
        updateContractStatus();
    });

    function updateStatistics() {
        const contrats = document.querySelectorAll('.timeline-item');
        let totalPaiements = 0;
        let contratsEcheance = 0;

        contrats.forEach(item => {
            // Compter les paiements (simulation)
            const paymentsText = item.textContent;
            if (paymentsText.includes('Paiement(s)')) {
                const matches = paymentsText.match(/(\d+)\s+Paiement\(s\)/);
                if (matches) {
                    totalPaiements += parseInt(matches[1]);
                }
            }
        });

        document.getElementById('totalPaiements').textContent = totalPaiements;
        document.getElementById('badgePaiements').textContent = totalPaiements;
        document.getElementById('badgeEcheance').textContent = contratsEcheance;
    }

    function updateContractStatus() {
        const today = new Date();
        const in30Days = new Date(today.getTime() + (30 * 24 * 60 * 60 * 1000));

        // Simulation du statut des contrats
        document.querySelectorAll('[id^="status-badge-"]').forEach(badge => {
            // Par défaut, marquer comme actif
            badge.className = 'badge status-active';
            badge.textContent = 'Actif';
        });
    }

    function telechargerContrat(contratId) {
        contratToDownload = contratId;
        const modal = new bootstrap.Modal(document.getElementById('downloadModal'));
        modal.show();
    }

    document.getElementById('confirmDownload').addEventListener('click', function() {
        if (contratToDownload) {
            // Simuler le téléchargement
            window.open(`${location.origin}${location.pathname}/../../contrats/${contratToDownload}/pdf`, '_blank');

            // Fermer le modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('downloadModal'));
            modal.hide();

            // Notification de succès
            showNotification('Téléchargement en cours...', 'success');
        }
    });

    function showNotification(message, type = 'info') {
        // Créer une notification simple
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alert.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
        alert.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;

        document.body.appendChild(alert);

        // Auto-suppression après 3 secondes
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 3000);
    }

    // Animation d'apparition progressive des contrats
    document.addEventListener('DOMContentLoaded', function() {
        const timelineItems = document.querySelectorAll('.timeline-item');
        timelineItems.forEach((item, index) => {
            item.style.opacity = '0';
            item.style.transform = 'translateY(20px)';

            setTimeout(() => {
                item.style.transition = 'all 0.5s ease';
                item.style.opacity = '1';
                item.style.transform = 'translateY(0)';
            }, index * 100);
        });
    });
</script>
</body>
</html>