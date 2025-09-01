<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Gestion Location Immeubles</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .login-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 2.5rem;
            width: 100%;
            max-width: 450px;
            position: relative;
            overflow: hidden;
        }

        .login-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }

        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-title {
            color: #333;
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .login-subtitle {
            color: #666;
            font-size: 1rem;
            font-weight: 400;
        }

        .form-group {
            margin-bottom: 1.5rem;
            position: relative;
        }

        .form-label {
            display: block;
            color: #333;
            font-weight: 600;
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
        }

        .form-input {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e1e5e9;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: #fafbfc;
        }

        .form-input:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }

        .form-input:hover {
            border-color: #c3c8d4;
        }

        .btn-login {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            color: white;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin: 1rem 0;
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .btn-login:active {
            transform: translateY(0);
        }

        .btn-login:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        /* Messages d'erreur et de succès */
        .error-message {
            background: #fee2e2;
            border: 1px solid #fecaca;
            color: #dc2626;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            text-align: center;
        }

        .success-message {
            background: #d1fae5;
            border: 1px solid #a7f3d0;
            color: #065f46;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            text-align: center;
        }

        .register-link {
            text-align: center;
            margin-top: 1.5rem;
            padding-top: 1.5rem;
            border-top: 1px solid #e1e5e9;
        }

        .register-link p {
            color: #666;
            margin-bottom: 0.5rem;
        }

        .register-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s ease;
        }

        .register-link a:hover {
            color: #764ba2;
            text-decoration: underline;
        }

        .input-icon {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
            font-size: 1.1rem;
        }

        .password-toggle {
            cursor: pointer;
            user-select: none;
            transition: color 0.3s ease;
        }

        .password-toggle:hover {
            color: #667eea;
        }

        @media (max-width: 480px) {
            .login-container {
                margin: 10px;
                padding: 2rem 1.5rem;
            }

            .login-title {
                font-size: 1.7rem;
            }
        }

        /* Animation de chargement */
        .loading {
            display: none;
            width: 20px;
            height: 20px;
            border: 2px solid #ffffff;
            border-top: 2px solid transparent;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-left: 10px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
<div class="login-container">
    <div class="login-header">
        <h1 class="login-title">🏠 Connexion</h1>
        <p class="login-subtitle">Gestion Location Immeubles</p>
    </div>

    <!-- Affichage des messages d'erreur -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}" />
        </div>
    </c:if>

    <!-- Affichage des messages de succès (par exemple après déconnexion) -->
    <c:if test="${not empty success}">
        <div class="success-message">
            <c:out value="${success}" />
        </div>
    </c:if>

    <!-- Formulaire de connexion -->
    <form method="post" action="${pageContext.request.contextPath}/auth" id="loginForm">
        <!-- Champ caché pour l'action -->
        <input type="hidden" name="action" value="login">

        <div class="form-group">
            <label for="email" class="form-label">📧 Adresse email</label>
            <input
                    type="email"
                    id="email"
                    name="email"
                    class="form-input"
                    placeholder="votre.email@exemple.com"
                    required
                    autocomplete="email"
                    value="<c:out value='${email}' />"
            >
        </div>

        <div class="form-group" style="position: relative;">
            <label for="motDePasse" class="form-label">🔐 Mot de passe</label>
            <input
                    type="password"
                    id="motDePasse"
                    name="motDePasse"
                    class="form-input"
                    placeholder="••••••••"
                    required
                    autocomplete="current-password"
                    style="padding-right: 45px;"
            >
            <span class="input-icon password-toggle" onclick="togglePassword()">👁️</span>
        </div>

        <button type="submit" class="btn-login" id="loginButton">
            Se connecter
            <span class="loading" id="loadingSpinner"></span>
        </button>
    </form>

    <!-- Lien vers l'inscription -->
    <div class="register-link">
        <p>Vous n'avez pas encore de compte ?</p>
        <a href="${pageContext.request.contextPath}/auth?action=register">
            ✨ Créer un compte gratuitement
        </a>
    </div>
</div>

<script>
    // Fonction pour basculer l'affichage du mot de passe
    function togglePassword() {
        const passwordInput = document.getElementById('motDePasse');
        const toggleIcon = document.querySelector('.password-toggle');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.textContent = '🙈';
            toggleIcon.title = 'Masquer le mot de passe';
        } else {
            passwordInput.type = 'password';
            toggleIcon.textContent = '👁️';
            toggleIcon.title = 'Afficher le mot de passe';
        }
    }

    // Animation d'entrée de la page
    window.addEventListener('load', function() {
        const container = document.querySelector('.login-container');
        container.style.opacity = '0';
        container.style.transform = 'translateY(30px)';

        setTimeout(() => {
            container.style.transition = 'all 0.6s ease';
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 100);
    });

    // Validation côté client et animation de soumission
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('motDePasse').value;
        const loginButton = document.getElementById('loginButton');
        const loadingSpinner = document.getElementById('loadingSpinner');

        // Validation basique
        if (!email || !password) {
            e.preventDefault();
            alert('⚠️ Veuillez remplir tous les champs');
            return;
        }

        if (!email.includes('@') || !email.includes('.')) {
            e.preventDefault();
            alert('⚠️ Veuillez saisir une adresse email valide');
            document.getElementById('email').focus();
            return;
        }

        if (password.length < 3) {
            e.preventDefault();
            alert('⚠️ Le mot de passe semble trop court');
            document.getElementById('motDePasse').focus();
            return;
        }

        // Animation de chargement
        loginButton.disabled = true;
        loginButton.textContent = 'Connexion en cours...';
        loadingSpinner.style.display = 'inline-block';

        // Réactiver le bouton après 10 secondes au cas où
        setTimeout(() => {
            if (loginButton.disabled) {
                loginButton.disabled = false;
                loginButton.textContent = 'Se connecter';
                loadingSpinner.style.display = 'none';
            }
        }, 10000);
    });

    // Auto-focus sur le champ email si vide, sinon sur le mot de passe
    window.addEventListener('load', function() {
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('motDePasse');

        setTimeout(() => {
            if (emailInput.value.trim() === '') {
                emailInput.focus();
            } else {
                passwordInput.focus();
            }
        }, 600); // Attendre la fin de l'animation
    });

    // Effacer les messages d'erreur quand l'utilisateur commence à taper
    document.getElementById('email').addEventListener('input', function() {
        hideMessages();
    });

    document.getElementById('motDePasse').addEventListener('input', function() {
        hideMessages();
    });

    function hideMessages() {
        const errorMsg = document.querySelector('.error-message');
        const successMsg = document.querySelector('.success-message');

        if (errorMsg) {
            errorMsg.style.transition = 'opacity 0.3s ease';
            errorMsg.style.opacity = '0';
            setTimeout(() => errorMsg.remove(), 300);
        }

        if (successMsg) {
            successMsg.style.transition = 'opacity 0.3s ease';
            successMsg.style.opacity = '0';
            setTimeout(() => successMsg.remove(), 300);
        }
    }

    // Support de la touche Entrée
    document.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            document.getElementById('loginForm').dispatchEvent(new Event('submit'));
        }
    });
</script>
</body>
</html>