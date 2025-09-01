<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="sn.team.gestion_loc_immeubles.Entities.Contrat" %>

<%
    Contrat contrat = (Contrat) request.getAttribute("contrat");
    boolean isEdit = (contrat != null);
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= isEdit ? "Modifier" : "Ajouter" %> un Contrat</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2><%= isEdit ? "✏ Modifier" : "➕ Ajouter" %> un Contrat</h2>

    <form method="post" action="${pageContext.request.contextPath}/contrats">
        <c:if test="${contrat != null}">
            <input type="hidden" name="id" value="${contrat.id}">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Date début</label>
            <input type="date" class="form-control" name="dateDebut" required
                   value="<c:out value='${contrat.dateDebut}'/>">
        </div>

        <div class="mb-3">
            <label class="form-label">Date fin</label>
            <input type="date" class="form-control" name="dateFin"
                   value="<c:out value='${contrat.dateFin}'/>">
        </div>

        <div class="mb-3">
            <label class="form-label">Montant</label>
            <input type="number" class="form-control" name="montant" required
                   value="<c:out value='${contrat.montant}'/>">
        </div>

        <div class="mb-3">
            <label class="form-label">Locataire</label>
            <select class="form-select" name="locataireId" required>
                <option value="">-- Choisir --</option>
                <c:forEach var="l" items="${locataires}">
                    <option value="${l.id}" <c:if test="${contrat.locataire != null && contrat.locataire.id == l.id}">selected</c:if>>
                            ${l.nom} ${l.prenom}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Unité</label>
            <select class="form-select" name="uniteId" required>
                <option value="">-- Choisir --</option>
                <c:forEach var="u" items="${unites}">
                    <option value="${u.id}" <c:if test="${contrat.unite != null && contrat.unite.id == u.id}">selected</c:if>>
                            ${u.numero} (${u.immeuble.nom})
                    </option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">
            <%= isEdit ? "Mettre à jour" : "Ajouter" %>
        </button>
        <a href="${pageContext.request.contextPath}/contrats" class="btn btn-secondary">Annuler</a>
    </form>
</div>
</body>
</html>
