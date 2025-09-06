<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Unite" %>

<%
  Unite unite = (Unite) request.getAttribute("unite");
  boolean isEdit = (unite != null);
%>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title><%= isEdit ? "Modifier" : "Ajouter" %> une Unité</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title mb-0">
            <%= isEdit ? "✏ Modifier" : "➕ Ajouter" %> une Unité
          </h3>
        </div>
        <div class="card-body">
          <form method="post" action="${pageContext.request.contextPath}/unites">
            <c:if test="${unite != null}">
              <input type="hidden" name="id" value="${unite.id}">
            </c:if>

            <!-- Numéro de l'unité -->
            <div class="mb-3">
              <label for="numero" class="form-label">
                Numéro <span class="text-danger">*</span>
              </label>
              <input type="number"
                     id="numero"
                     class="form-control"
                     name="numero"
                     required
                     value="<c:out value='${unite.numero}'/>"
                     placeholder="Ex: 101, 202...">
              <div class="form-text">Numéro unique de l'unité</div>
            </div>

            <!-- Nombre de pièces -->
            <div class="mb-3">
              <label for="nbPieces" class="form-label">
                Nombre de pièces <span class="text-danger">*</span>
              </label>
              <input type="number"
                     id="nbPieces"
                     class="form-control"
                     name="nbPieces"
                     required
                     min="1"
                     value="<c:out value='${unite.nbPieces}'/>"
                     placeholder="Ex: 1, 2, 3...">
              <div class="form-text">Nombre minimum : 1 pièce</div>
            </div>

            <!-- Superficie -->
            <div class="mb-3">
              <label for="superficie" class="form-label">
                Superficie (m²) <span class="text-danger">*</span>
              </label>
              <input type="number"
                     id="superficie"
                     class="form-control"
                     name="superficie"
                     required
                     min="10"
                     step="0.01"
                     value="<c:out value='${unite.superficie}'/>"
                     placeholder="Ex: 45.5, 120.25...">
              <div class="form-text">Superficie minimum : 10 m²</div>
            </div>

            <!-- Loyer -->
            <div class="mb-3">
              <label for="loyer" class="form-label">
                Loyer (FCFA) <span class="text-danger">*</span>
              </label>
              <input type="number"
                     id="loyer"
                     class="form-control"
                     name="loyer"
                     required
                     min="0"
                     step="0.01"
                     value="<c:out value='${unite.loyer}'/>"
                     placeholder="Ex: 150000, 250000...">
              <div class="form-text">Montant du loyer mensuel</div>
            </div>

            <!-- Statut -->
            <div class="mb-3">
              <label for="statut" class="form-label">
                Statut <span class="text-danger">*</span>
              </label>
              <select id="statut" class="form-select" name="statut" required>
                <option value="">-- Choisir un statut --</option>
                <option value="DISPONIBLE"
                        <c:if test="${unite != null && unite.statut == 'DISPONIBLE'}">selected</c:if>>
                  Disponible
                </option>
                <option value="LOUE"
                        <c:if test="${unite != null && unite.statut == 'LOUE'}">selected</c:if>>
                  Loué
                </option>
              </select>
              <div class="form-text">Indiquez si l'unité est actuellement disponible ou louée</div>
            </div>

            <!-- Immeuble -->
            <div class="mb-3">
              <label for="immeubleId" class="form-label">
                Immeuble <span class="text-danger">*</span>
              </label>
              <select id="immeubleId" class="form-select" name="immeubleId" required>
                <option value="">-- Choisir un immeuble --</option>
                <c:forEach var="i" items="${immeubles}">
                  <option value="${i.id}"
                          <c:if test="${unite != null && unite.immeuble != null && unite.immeuble.id == i.id}">selected</c:if>>
                      ${i.nom} - ${i.adresse}
                  </option>
                </c:forEach>
              </select>
              <div class="form-text">Sélectionnez l'immeuble auquel appartient cette unité</div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-between mt-4">
              <a href="${pageContext.request.contextPath}/unites"
                 class="btn btn-outline-secondary">
                ← Retour à la liste
              </a>
              <div>
                <button type="reset" class="btn btn-outline-warning me-2">
                  🔄 Réinitialiser
                </button>
                <button type="submit" class="btn btn-primary">
                  <%= isEdit ? "💾 Mettre à jour" : "➕ Ajouter" %>
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Bootstrap JS (optionnel) -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
