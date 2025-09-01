<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Immeuble" %>

<%
    List<Immeuble> immeubles = (List<Immeuble>) request.getAttribute("immeubles");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Immeubles</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">🏢 Liste des Immeubles</h2>
    <table class="table table-hover">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Adresse</th>
            <th>Description</th>
            <th>Propriétaire</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="i" items="${immeubles}">
            <tr>
                <td>${i.id}</td>
                <td>${i.nom}</td>
                <td>${i.adresse}</td>
                <td>${i.description}</td>
                <td>${i.proprietaire.nom} ${i.proprietaire.prenom}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
