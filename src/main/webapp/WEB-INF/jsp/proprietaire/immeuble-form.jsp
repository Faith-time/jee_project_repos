<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Immeuble" %>

<%
    Immeuble immeuble = (Immeuble) request.getAttribute("immeuble");
    boolean isEdit = (immeuble != null);

    // Récupération du propriétaire connecté (stocké en session par ton filtre/login)
    Long proprietaireId = (Long) session.getAttribute("utilisateurId");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= isEdit ? "Modifier" : "Ajouter" %> un Immeuble</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
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

        .form-container {
            max-width: 800px;
            margin: 0 auto;
        }

        .page-header {
            background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
            color: white;
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }

        .page-header h3 {
            margin: 0;
            font-size: 2rem;
            font-weight: 600;
        }

        .form-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            border: none;
        }

        .form-label {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 8px;
        }

        .form-control, .form-select {
            border: 2px solid #e0e6ed;
            border-radius: 10px;
            padding: 12px 15px;
            transition: all 0.3s ease;
            font-size: 1rem;
        }

        .form-control:focus, .form-select:focus {
            border-color: #3498db;
            box-shadow: 0 0 15px rgba(52, 152, 219, 0.2);
        }

        .form-text {
            color: #6c757d;
            font-size: 0.9rem;
            margin-top: 5px;
        }

        .info-card {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border: 1px solid #dee2e6;
            border-radius: 10px;
            padding: 15px;
            border-left: 4px solid #28a745;
        }

        .info-card.warning {
            border-left-color: #ffc107;
            background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
        }

        .btn-custom {
            padding: 12px 25px;
            border-radius: 10px;
            font-weight: 600;
            transition: all 0.3s ease;
            border: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary-custom {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
            box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
        }

        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(52, 152, 219, 0.4);
            color: white;
        }

        .btn-success-custom {
            background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
            color: white;
            box-shadow: 0 5px 15px rgba(39, 174, 96, 0.3);
        }

        .btn-success-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(39, 174, 96, 0.4);
            color: white;
        }

        .btn-secondary-custom {
            background: linear-gradient(135deg, #95a5a6 0%, #bdc3c7 100%);
            color: white;
        }

        .btn-warning-custom {
            background: linear-gradient(135deg, #f39c12 0%, #f4d03f 100%);
            color: white;
        }

        .char-counter {
            text-align: right;
            font-size: 0.85rem;
            margin-top: 5px;
        }

        .char-counter.text-warning {
            color: #f39c12 !important;
        }

        .char-counter.text-danger {
            color: #e74c3c !important;
        }

        .invalid-feedback {
            display: block;
            color: #e74c3c;
            font-size: 0.9rem;
            margin-top: 5px;
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
            .form-card {
                padding: 20px;
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

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h3 class="card-title mb-0">
                        <i class="fas <%= isEdit ? "fa-edit" : "fa-plus-circle" %>"></i>
                        <%= isEdit ? "Modifier" : "Ajouter" %> un Immeuble
                    </h3>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/immeubles" class="needs-validation" novalidate>
                        <!-- Si modification : envoyer l'ID de l'immeuble -->
                        <c:if test="${immeuble != null}">
                            <input type="hidden" name="id" value="${immeuble.id}">
                        </c:if>

                        <!-- Dans tous les cas, on envoie l'ID du propriétaire connecté -->
                        <input type="hidden" name="proprietaireId" value="<%= isEdit ? immeuble.getProprietaire().getId() : proprietaireId %>">

                        <!-- Nom de l'immeuble -->
                        <div class="mb-4">
                            <label for="nom" class="form-label">
                                <i class="fas fa-building text-primary"></i>
                                Nom de l'immeuble <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   id="nom"
                                   class="form-control"
                                   name="nom"
                                   required
                                   minlength="3"
                                   maxlength="100"
                                   value="<c:out value='${immeuble.nom}'/>"
                                   placeholder="Ex: Résidence les Palmiers, Immeuble Central...">
                            <div class="form-text">
                                <i class="fas fa-info-circle"></i>
                                Entre 3 et 100 caractères
                            </div>
                            <div class="invalid-feedback">
                                Veuillez saisir un nom valide (3-100 caractères).
                            </div>
                        </div>

                        <!-- Adresse -->
                        <div class="mb-4">
                            <label for="adresse" class="form-label">
                                <i class="fas fa-map-marker-alt text-danger"></i>
                                Adresse <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   id="adresse"
                                   class="form-control"
                                   name="adresse"
                                   required
                                   value="<c:out value='${immeuble.adresse}'/>"
                                   placeholder="Ex: 15 Avenue Léopold Sédar Senghor, Dakar">
                            <div class="form-text">
                                <i class="fas fa-info-circle"></i>
                                Adresse complète de l'immeuble
                            </div>
                            <div class="invalid-feedback">
                                Veuillez saisir une adresse valide.
                            </div>
                        </div>

                        <!-- Description -->
                        <div class="mb-4">
                            <label for="description" class="form-label">
                                <i class="fas fa-file-alt text-info"></i>
                                Description <span class="text-muted">(optionnel)</span>
                            </label>
                            <textarea id="description"
                                      class="form-control"
                                      name="description"
                                      rows="4"
                                      maxlength="255"
                                      placeholder="Décrivez les caractéristiques de l'immeuble : nombre d'étages, services, équipements, etc."><c:out value='${immeuble.description}'/></textarea>
                            <div class="form-text">
                                <i class="fas fa-info-circle"></i>
                                Maximum 255 caractères
                            </div>
                            <div class="text-end">
                                <small class="text-muted">
                                    <span id="descriptionCount">0</span>/255 caractères
                                </small>
                            </div>
                        </div>

                        <!-- Affichage du propriétaire (info seulement) -->
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-user text-success"></i>
                                Propriétaire
                            </label>
                            <div class="card bg-light">
                                <div class="card-body py-2">
                                    <c:choose>
                                        <c:when test="${isEdit}">
                                            <strong>${immeuble.proprietaire.prenom} ${immeuble.proprietaire.nom}</strong>
                                            <br>
                                            <small class="text-muted">
                                                <i class="fas fa-envelope"></i> ${immeuble.proprietaire.email}
                                                <c:if test="${immeuble.proprietaire.telephone != null}">
                                                    | <i class="fas fa-phone"></i> ${immeuble.proprietaire.telephone}
                                                </c:if>
                                            </small>
                                        </c:when>
                                        <c:otherwise>
                                            <em class="text-muted">Propriétaire connecté (vous-même)</em>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <!-- Informations supplémentaires en mode édition -->
                        <c:if test="${isEdit && immeuble.unites != null && !immeuble.unites.isEmpty()}">
                            <div class="mb-4">
                                <label class="form-label">
                                    <i class="fas fa-home text-warning"></i>
                                    Unités associées
                                </label>
                                <div class="card bg-info bg-opacity-10">
                                    <div class="card-body py-2">
                                        <small class="text-info">
                                            <i class="fas fa-info-circle"></i>
                                            Cet immeuble contient <strong>${immeuble.unites.size()}</strong> unité(s).
                                            <br>
                                            Les modifications apportées affecteront toutes les unités associées.
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Boutons d'action -->
                        <div class="d-flex justify-content-between align-items-center mt-5">
                            <a href="${pageContext.request.contextPath}/immeubles"
                               class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left"></i>
                                Retour à la liste
                            </a>

                            <div>
                                <button type="reset" class="btn btn-outline-warning me-2">
                                    <i class="fas fa-undo"></i>
                                    Réinitialiser
                                </button>
                                <button type="submit" class="btn <%= isEdit ? "btn-success" : "btn-primary" %>">
                                    <i class="fas <%= isEdit ? "fa-save" : "fa-plus" %>"></i>
                                    <%= isEdit ? "Mettre à jour" : "Ajouter" %>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>

<!-- Script pour la validation et le compteur de caractères -->
<script>
    // Validation du formulaire Bootstrap
    (function() {
        'use strict';
        window.addEventListener('load', function() {
            var forms = document.getElementsByClassName('needs-validation');
            var validation = Array.prototype.filter.call(forms, function(form) {
                form.addEventListener('submit', function(event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        }, false);
    })();

    // Compteur de caractères pour la description
    document.addEventListener('DOMContentLoaded', function() {
        const descriptionTextarea = document.getElementById('description');
        const descriptionCount = document.getElementById('descriptionCount');

        function updateCount() {
            const count = descriptionTextarea.value.length;
            descriptionCount.textContent = count;

            if (count > 200) {
                descriptionCount.classList.add('text-warning');
            } else {
                descriptionCount.classList.remove('text-warning');
            }

            if (count > 240) {
                descriptionCount.classList.remove('text-warning');
                descriptionCount.classList.add('text-danger');
            } else {
                descriptionCount.classList.remove('text-danger');
            }
        }

        // Mise à jour initiale
        updateCount();

        // Mise à jour en temps réel
        descriptionTextarea.addEventListener('input', updateCount);
    });
</script>
</body>
</html>