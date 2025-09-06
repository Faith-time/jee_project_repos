<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Utilisateur" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Role" %>

<%
    Utilisateur u = (Utilisateur) request.getAttribute("utilisateur");
    boolean isEdit = (u != null);
    String userRole = isEdit && u.getRole() != null ? u.getRole().name() : "";
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= isEdit ? "Modifier" : "Ajouter" %> un utilisateur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2><%= isEdit ? "✏ Modifier" : "➕ Ajouter" %> un utilisateur</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/utilisateurs">
        <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>">
        <% if (isEdit) { %>
        <input type="hidden" name="id" value="<%= u.getId() %>">
        <% } %>

        <div class="mb-3">
            <label>Nom</label>
            <input type="text" name="nom" class="form-control" required
                   value="<%= isEdit ? u.getNom() : "" %>">
        </div>

        <div class="mb-3">
            <label>Prénom</label>
            <input type="text" name="prenom" class="form-control" required
                   value="<%= isEdit ? u.getPrenom() : "" %>">
        </div>

        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="email" class="form-control" required
                   value="<%= isEdit ? u.getEmail() : "" %>">
        </div>

        <div class="mb-3">
            <label>Mot de passe</label>
            <input type="password" name="motDePasse" class="form-control"
                <%= isEdit ? "" : "required" %>
                   placeholder="<%= isEdit ? "Laisser vide pour ne pas changer" : "Mot de passe requis" %>">
        </div>

        <div class="mb-3">
            <label>Rôle</label>
            <select name="role" class="form-select" required>
                <option value="">-- Choisir un rôle --</option>
                <option value="ADMIN" <%= "ADMIN".equals(userRole) ? "selected" : "" %>>Administrateur</option>
                <option value="PROPRIETAIRE" <%= "PROPRIETAIRE".equals(userRole) ? "selected" : "" %>>Propriétaire</option>
                <option value="LOCATAIRE" <%= "LOCATAIRE".equals(userRole) ? "selected" : "" %>>Locataire</option>
                <option value="VISITEUR" <%= "VISITEUR".equals(userRole) ? "selected" : "" %>>Visiteur</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary"><%= isEdit ? "Mettre à jour" : "Ajouter" %></button>
        <a href="${pageContext.request.contextPath}/utilisateurs" class="btn btn-secondary">Annuler</a>
    </form>
</div>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
