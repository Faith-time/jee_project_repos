<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Unite" %>

<%
    List<Unite> unites = (List<Unite>) request.getAttribute("unites");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Unités</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">🏠 Liste des Unités</h2>
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Numéro</th>
            <th>Nb Pièces</th>
            <th>Superficie</th>
            <th>Loyer</th>
            <th>Immeuble</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${unites}">
            <tr>
                <td>${u.id}</td>
                <td>${u.numero}</td>
                <td>${u.nbPieces}</td>
                <td>${u.superficie} m²</td>
                <td>${u.loyer} FCFA</td>
                <td>${u.immeuble.nom}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
