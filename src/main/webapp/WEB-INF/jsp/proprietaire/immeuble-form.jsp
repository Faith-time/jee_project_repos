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
</head>
<body class="bg-light">
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