<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Utilisateur" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Administrateur" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Proprietaire" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Locataire" %>

<%
    Utilisateur u = (Utilisateur) request.getAttribute("utilisateur");
    boolean isEdit = (u != null);

    // Déterminer le type d'utilisateur de manière sécurisée
    String userType = "";
    if (u != null) {
        if (u instanceof Administrateur) {
            userType = "ADMIN";
        } else if (u instanceof Proprietaire) {
            userType = "PROPRIETAIRE";
        } else if (u instanceof Locataire) {
            userType = "LOCATAIRE";
        }
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= isEdit ? "Modifier" : "Ajouter" %> un Utilisateur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2><%= isEdit ? "✏ Modifier" : "➕ Ajouter" %> un Utilisateur</h2>

    <form method="post" action="${pageContext.request.contextPath}/utilisateurs">
        <!-- Champ caché pour l'ID en mode édition -->
        <% if (isEdit) { %>
        <input type="hidden" name="id" value="<%= u.getId() %>">
        <% } %>

        <div class="mb-3">
            <label class="form-label">Nom</label>
            <input type="text" class="form-control" name="nom" required
                   value="<%= isEdit ? (u.getNom() != null ? u.getNom() : "") : "" %>">
        </div>

        <div class="mb-3">
            <label class="form-label">Prénom</label>
            <input type="text" class="form-control" name="prenom" required
                   value="<%= isEdit ? (u.getPrenom() != null ? u.getPrenom() : "") : "" %>">
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" name="email" required
                   value="<%= isEdit ? (u.getEmail() != null ? u.getEmail() : "") : "" %>">
        </div>

        <div class="mb-3">
            <label class="form-label">Mot de passe</label>
            <input type="password" class="form-control" name="motDePasse"
                <%= isEdit ? "" : "required" %>
                   placeholder="<%= isEdit ? "Laisser vide pour ne pas changer" : "Mot de passe requis" %>">
        </div>

        <div class="mb-3">
            <label class="form-label">Type d'utilisateur</label>
            <select class="form-select" name="type" required>
                <option value="">-- Choisir --</option>
                <option value="ADMIN" <%= "ADMIN".equals(userType) ? "selected" : "" %>>Admin</option>
                <option value="PROPRIETAIRE" <%= "PROPRIETAIRE".equals(userType) ? "selected" : "" %>>Propriétaire</option>
                <option value="LOCATAIRE" <%= "LOCATAIRE".equals(userType) ? "selected" : "" %>>Locataire</option>
            </select>
        </div>

        <div class="mb-3">
            <button type="submit" class="btn btn-primary">
                <%= isEdit ? "Mettre à jour" : "Ajouter" %>
            </button>
            <a href="${pageContext.request.contextPath}/utilisateurs" class="btn btn-secondary">Annuler</a>
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>