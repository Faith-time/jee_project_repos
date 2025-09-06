<%-- Fragment réutilisable pour la sidebar locataire --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    body {
        display: flex;
        min-height: 100vh;
    }
    .sidebar {
        width: 250px;
        background: #343a40;
        color: white;
        flex-shrink: 0;
        position: fixed;
        height: 100vh;
        overflow-y: auto;
        z-index: 1000;
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
        text-decoration: none;
    }
    .sidebar a.active {
        background: #007bff;
        color: #fff;
    }
    .content {
        flex-grow: 1;
        padding: 20px;
        background: #f8f9fa;
        margin-left: 250px; /* Compense la largeur de la sidebar fixe */
        min-height: 100vh;
    }
    .sidebar h3 {
        margin: 0;
        font-size: 1.2rem;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .sidebar {
            width: 100%;
            height: auto;
            position: relative;
        }
        .content {
            margin-left: 0;
        }
        body {
            flex-direction: column;
        }
    }
</style>

<!-- Sidebar -->
<div class="sidebar">
    <h3 class="p-3 border-bottom">Espace Locataire</h3>
    <a href="${pageContext.request.contextPath}/locataire/dashboard"
       class="${param.activePage == 'dashboard' ? 'active' : ''}">
        📊 Dashboard
    </a>
    <a href="${pageContext.request.contextPath}/unites"
       class="${param.activePage == 'unites' ? 'active' : ''}">
        🏢 Unités Disponibles
    </a>
    <a href="${pageContext.request.contextPath}/contrats"
       class="${param.activePage == 'contrats' ? 'active' : ''}">
        📑 Mes Contrats
    </a>
    <a href="${pageContext.request.contextPath}/paiements"
       class="${param.activePage == 'paiements' ? 'active' : ''}">
        💳 Mes Paiements
    </a>
    <a href="${pageContext.request.contextPath}/logout" class="text-danger">
        🚪 Déconnexion
    </a>
</div>