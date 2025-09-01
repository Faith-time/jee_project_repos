<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Utilisateur" %>

<%
    List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Utilisateurs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">👤 Liste des Utilisateurs</h2>

    <!-- Bouton Ajouter -->
    <a href="${pageContext.request.contextPath}/utilisateurs?action=form" class="btn btn-success">
        ➕ Ajouter un utilisateur
    </a>

    <!-- Tableau -->
    <table class="table table-striped table-hover">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Email</th>
            <th>Type</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${utilisateurs}">
            <tr>
                <td>${u.id}</td>
                <td>${u.nom}</td>
                <td>${u.prenom}</td>
                <td>${u.email}</td>
                <td>${u.className}
                </td>
                <td>
                    <!-- Bouton Modifier -->
                    <a href="${pageContext.request.contextPath}/utilisateurs?action=form&id=${u.id}"
                       class="btn btn-warning btn-sm">✏ Modifier</a>

                    <!-- Bouton Supprimer -->
                    <a href="${pageContext.request.contextPath}/utilisateurs?action=delete&id=${u.id}"
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?');">
                        🗑 Supprimer
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
