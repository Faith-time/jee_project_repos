<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Paiement CinetPay - Gestion Immobilière</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/bootstrap.min.css'/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
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

        /* Payment Card */
        .payment-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            overflow: hidden;
            max-width: 600px;
            margin: 0 auto;
        }

        .payment-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }

        .payment-header h2 {
            margin: 0;
            font-weight: 700;
            background: transparent;
            color: white;
        }

        .payment-body {
            padding: 40px;
            background: white;
        }

        .cinetpay-logo {
            max-width: 200px;
            margin: 0 auto 30px;
            display: block;
        }

        .payment-form {
            background: white;
        }

        .form-control {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 12px 15px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .btn-cinetpay {
            background: linear-gradient(45deg, #ff6b35, #f7931e);
            border: none;
            color: white;
            padding: 15px 30px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 50px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(255, 107, 53, 0.4);
        }

        .btn-cinetpay:hover {
            background: linear-gradient(45deg, #e55a2e, #e0841a);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(255, 107, 53, 0.6);
            color: white;
        }

        .btn-cinetpay:disabled {
            background: #6c757d;
            transform: none;
            box-shadow: none;
        }

        .payment-methods {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 15px;
            margin: 30px 0;
        }

        .method-item {
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .method-item:last-child {
            border-bottom: none;
        }

        .method-icon {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            margin-right: 15px;
        }

        .security-info {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 20px;
            border-radius: 15px;
            margin: 30px 0;
        }

        .security-info h6 {
            color: white;
            background: transparent;
            margin-bottom: 10px;
        }

        .security-info ul {
            margin: 0;
            padding-left: 20px;
            background: transparent;
        }

        .security-info li {
            background: transparent;
            color: white;
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

            .payment-body {
                padding: 20px;
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

        /* Loading Animation */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.8);
            display: none;
            z-index: 2000;
            align-items: center;
            justify-content: center;
        }

        .loading-content {
            background: white;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            max-width: 400px;
            margin: 20px;
        }

        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
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
    <a href="${pageContext.request.contextPath}/locataire/paiements">💳 Mes Paiements</a>
    <a href="${pageContext.request.contextPath}/locataire/payer" class="active">💰 Payer</a>
    <a href="${pageContext.request.contextPath}/auth?action=logout" class="text-danger">🚪 Déconnexion</a>
</div>

<!-- Main Content -->
<div class="main-content">
    <!-- Messages d'alerte -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
                ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
                ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Payment Card -->
    <div class="payment-card">
        <div class="payment-header">
            <h2>
                <i class="fas fa-credit-card me-3"></i>
                Paiement Sécurisé CinetPay
            </h2>
            <p class="mb-0">Payez votre loyer en toute sécurité</p>
        </div>

        <div class="payment-body">
            <!-- Logo CinetPay -->
            <div class="text-center mb-4">
                <div class="cinetpay-logo">
                    <h3 class="text-primary fw-bold">
                        <i class="fas fa-shield-alt me-2"></i>CinetPay
                    </h3>
                    <p class="text-muted small">Paiement mobile sécurisé</p>
                </div>
            </div>

            <!-- Formulaire de sélection du paiement -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                        ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Liste des unités louées -->
            <div class="row">
                <c:choose>
                    <c:when test="${empty listeUnitesLouees}">
                        <div class="col-12">
                            <div class="empty-state">
                                <i class="fas fa-home"></i>
                                <h4>Aucune unité louée</h4>
                                <p>Vous n'avez actuellement aucune unité en location.</p>
                                <p>Contactez votre propriétaire pour plus d'informations.</p>
                                <a href="${pageContext.request.contextPath}/locataire/dashboard" class="btn btn-primary mt-3">
                                    <i class="fas fa-arrow-left me-2"></i>Retour au Dashboard
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="unite" items="${listeUnitesLouees}" varStatus="status">
                            <div class="col-lg-6 col-xl-4 mb-4">
                                <div class="unite-card">
                                    <div class="unite-header">
                                        <h4>
                                            <i class="fas fa-building me-2"></i>
                                            Unité #${unite.numero}
                                        </h4>
                                        <c:if test="${not empty unite.immeuble}">
                                            <small>${unite.immeuble.nom}</small>
                                        </c:if>
                                    </div>

                                    <div class="unite-body">
                                        <!-- Informations de l'unité -->
                                        <div class="unite-info">
                                            <div class="unite-info-item">
                                        <span class="unite-info-label">
                                            <i class="fas fa-door-open me-2"></i>Nombre de pièces
                                        </span>
                                                <span class="unite-info-value">
                                            ${unite.nbPieces} pièces
                                        </span>
                                            </div>

                                            <div class="unite-info-item">
                                        <span class="unite-info-label">
                                            <i class="fas fa-ruler-combined me-2"></i>Superficie
                                        </span>
                                                <span class="unite-info-value">
                                            <fmt:formatNumber value="${unite.superficie}" maxFractionDigits="0"/> m²
                                        </span>
                                            </div>

                                            <div class="unite-info-item">
                                        <span class="unite-info-label">
                                            <i class="fas fa-money-bill-wave me-2"></i>Loyer mensuel
                                        </span>
                                                <span class="unite-info-value loyer-amount">
                                            <fmt:formatNumber value="${unite.loyer}" maxFractionDigits="0"/> FCFA
                                        </span>
                                            </div>
                                        </div>

                                        <!-- Section des paiements en attente pour cette unité -->
                                        <div id="paiements-pending-${unite.id}" class="paiements-pending" style="display: none;">
                                            <h6 class="text-warning mb-2">
                                                <i class="fas fa-clock me-2"></i>Paiements en attente
                                            </h6>
                                            <div id="paiements-list-${unite.id}">
                                                <!-- Contenu chargé dynamiquement -->
                                            </div>
                                        </div>

                                        <!-- Navigation -->
                                        <div class="text-center mt-4">
                                            <a href="${pageContext.request.contextPath}/paiement/direct"
                                               class="btn btn-outline-secondary">
                                                Payer avec CinetPay
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Informations sur les méthodes de paiement -->
            <c:if test="${not empty listeUnitesLouees}">
                <div class="payment-methods-info">
                    <h5 class="text-center mb-4">
                        <i class="fas fa-shield-alt me-2"></i>
                        Méthodes de Paiement CinetPay
                    </h5>

                    <div class="row">
                        <div class="col-md-4">
                            <div class="method-item">
                                <div class="method-icon">
                                    <i class="fas fa-mobile-alt"></i>
                                </div>
                                <div>
                                    <strong>Mobile Money</strong>
                                    <div class="text-muted small">Orange Money, MTN Money, Moov Money</div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="method-item">
                                <div class="method-icon">
                                    <i class="fas fa-credit-card"></i>
                                </div>
                                <div>
                                    <strong>Cartes Bancaires</strong>
                                    <div class="text-muted small">Visa, Mastercard</div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="method-item">
                                <div class="method-icon">
                                    <i class="fas fa-university"></i>
                                </div>
                                <div>
                                    <strong>Virements</strong>
                                    <div class="text-muted small">Transfert direct</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="security-badge">
                        <h6>
                            <i class="fas fa-lock me-2"></i>
                            Paiement 100% Sécurisé
                        </h6>
                        <small>
                            Transactions cryptées • Données protégées • Support 24h/7j
                        </small>
                    </div>
                </div>
            </c:if>
            <!-- Navigation -->
            <div class="text-center">
                <a href="${pageContext.request.contextPath}/locataire/paiements"
                   class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>
                    Retour à mes paiements
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Loading Overlay -->
<div class="loading-overlay" id="loadingOverlay">
    <div class="loading-content">
        <div class="spinner"></div>
        <h5>Redirection vers CinetPay...</h5>
        <p class="text-muted">Veuillez patienter, vous allez être redirigé vers la plateforme de paiement sécurisée.</p>
        <div class="progress mt-3">
            <div class="progress-bar progress-bar-striped progress-bar-animated"
                 role="progressbar" style="width: 100%"></div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="<c:url value='/assets/js/bootstrap.bundle.min.js'/>"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Page paiement CinetPay chargée');

        const paiementSelect = document.getElementById('paiementId');
        const paiementDetails = document.getElementById('paiementDetails');
        const detailsContent = document.getElementById('detailsContent');
        const payButton = document.getElementById('payButton');
        const paymentForm = document.getElementById('paymentForm');
        const loadingOverlay = document.getElementById('loadingOverlay');

        // Données des paiements (à remplir côté serveur si nécessaire)
        const paiementsData = {
            <c:if test="${not empty listePaiementsEnAttente}">
            <c:forEach var="paiement" items="${listePaiementsEnAttente}" varStatus="status">
            <c:if test="${paiement.statut == 'EN_ATTENTE' or paiement.statut == 'EN_RETARD'}">
            "${paiement.id}": {
                id: "${paiement.id}",
                montant: "<fmt:formatNumber value='${paiement.montant}' type='number' maxFractionDigits='0'/>",
                dateEcheance: "<c:if test='${not empty paiement.dateEcheance}'><fmt:formatDate value='${paiement.dateEcheance}' pattern='dd/MM/yyyy'/></c:if>",
                statut: "${paiement.statut}",
                unite: "<c:if test='${not empty paiement.contrat and not empty paiement.contrat.unite}'>Unité ${paiement.contrat.unite.numero}</c:if>",
                immeuble: "<c:if test='${not empty paiement.contrat and not empty paiement.contrat.unite and not empty paiement.contrat.unite.immeuble}'>${paiement.contrat.unite.immeuble.nom}</c:if>"
            }<c:if test="${!status.last}">,</c:if>
            </c:if>
            </c:forEach>
            </c:if>
        };

        // Gestion du changement de sélection
        paiementSelect.addEventListener('change', function() {
            const selectedId = this.value;

            if (selectedId && paiementsData[selectedId]) {
                const paiement = paiementsData[selectedId];

                // Afficher les détails
                detailsContent.innerHTML = `
                <div class="row">
                    <div class="col-sm-6">
                        <strong>Montant:</strong><br>
                        <span class="text-primary fs-5">${paiement.montant} FCFA</span>
                    </div>
                    <div class="col-sm-6">
                        <strong>Échéance:</strong><br>
                        ${paiement.dateEcheance || 'Non définie'}
                    </div>
                </div>
                ${paiement.unite}
                <div class="row mt-2">
                    <div class="col-12">
                        <strong>Statut:</strong><br>
                        <span class="badge ${paiement.statut == 'EN_RETARD' ? 'bg-danger' : 'bg-warning'}">
                            ${paiement.statut == 'EN_RETARD' ? 'En retard' : 'En attente'}
                        </span>
                    </div>
                </div>
            `;

                paiementDetails.style.display = 'block';
                payButton.disabled = false;
                payButton.innerHTML = `
                <i class="fas fa-shield-alt me-2"></i>
                Payer ${paiement.montant} FCFA avec CinetPay
                <i class="fas fa-arrow-right ms-2"></i>
            `;
            } else {
                paiementDetails.style.display = 'none';
                payButton.disabled = true;
                payButton.innerHTML = `
                <i class="fas fa-shield-alt me-2"></i>
                Payer avec CinetPay
                <i class="fas fa-arrow-right ms-2"></i>
            `;
            }
        });

        // Gestion de la soumission du formulaire
        paymentForm.addEventListener('submit', function(e) {
            const selectedId = paiementSelect.value;

            if (!selectedId) {
                e.preventDefault();
                alert('Veuillez sélectionner un paiement à effectuer.');
                return;
            }

            const confirmation = confirm(
                'Vous allez être redirigé vers CinetPay pour effectuer votre paiement de manière sécurisée.\n\n' +
                'Êtes-vous sûr de vouloir continuer ?'
            );

            if (!confirmation) {
                e.preventDefault();
                return;
            }

            // Afficher l'overlay de chargement
            loadingOverlay.style.display = 'flex';

            // Désactiver le bouton pour éviter les double-clics
            payButton.disabled = true;
            payButton.innerHTML = `
            <i class="fas fa-spinner fa-spin me-2"></i>
            Redirection en cours...
        `;

            // Simuler un délai minimum pour l'UX
            setTimeout(() => {
                // Le formulaire sera soumis normalement
            }, 1000);
        });

        // Gestion de la visibilité de la page (retour depuis CinetPay)
        document.addEventListener('visibilitychange', function() {
            if (!document.hidden) {
                // L'utilisateur est revenu sur la page
                setTimeout(() => {
                    if (loadingOverlay.style.display === 'flex') {
                        loadingOverlay.style.display = 'none';
                        payButton.disabled = false;
                        const selectedId = paiementSelect.value;
                        if (selectedId && paiementsData[selectedId]) {
                            const paiement = paiementsData[selectedId];
                            payButton.innerHTML = `
                            <i class="fas fa-shield-alt me-2"></i>
                            Payer ${paiement.montant} FCFA avec CinetPay
                            <i class="fas fa-arrow-right ms-2"></i>
                        `;
                        }
                    }
                }, 500);
            }
        });

        // Pré-sélection si un seul paiement en attente
        if (paiementSelect.options.length === 2) { // 1 option par défaut + 1 paiement
            paiementSelect.selectedIndex = 1;
            paiementSelect.dispatchEvent(new Event('change'));
        }
    });
</script>
</body>
</html>