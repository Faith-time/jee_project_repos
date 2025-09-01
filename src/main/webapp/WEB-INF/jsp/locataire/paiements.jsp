<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Paiement" %>

<%
    List<Paiement> paiements = (List<Paiement>) request.getAttribute("paiements");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Paiements</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">💰 Liste des Paiements</h2>
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Montant</th>
            <th>Date Paiement</th>
            <th>Date Échéance</th>
            <th>Statut</th>
            <th>Contrat</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${paiements}">
            <tr>
                <td>${p.id}</td>
                <td>${p.montant} €</td>
                <td>${p.datePaiement}</td>
                <td>${p.dateEcheance}</td>
                <td>
                    <span class="badge
                        ${p.statut == 'PAYE' ? 'bg-success' :
                          (p.statut == 'EN_RETARD' ? 'bg-danger' : 'bg-warning')}">
                            ${p.statut}
                    </span>
                </td>
                <td>${p.contrat.id}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
