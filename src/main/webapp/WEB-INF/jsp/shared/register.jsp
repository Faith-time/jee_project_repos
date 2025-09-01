<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Gestion Location Immeubles</title>
    <!-- Bootstrap CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .register-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 2.5rem;
            width: 100%;
            max-width: 500px;
            position: relative;
            overflow: hidden;
        }

        .register-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }

        .register-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .register-title {
            color: #333;
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .register-subtitle {
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

        .form-input, .form-select {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e1e5e9;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: #fafbfc;
        }

        .form-input:focus, .form-select:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }

        .form-input:hover, .form-select:hover {
            border-color: #c3c8d4;
        }

        .btn-register {
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

        .btn-register:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .btn-register:active {
            transform: translateY(0);
        }

        .login-link {
            text-align: center;
            margin-top: 1.5rem;
            padding-top: 1.5rem;
            border-top: 1px solid #e1e5e9;
        }

        .login-link p {
            color: #666;
            margin-bottom: 0.5rem;
        }

        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s ease;
        }

        .login-link a:hover {
            color: #764ba2;
            text-decoration: underline;
        }

        .home-link {
            position: absolute;
            top: 20px;
            left: 20px;
            color: white;
            text-decoration: none;
            font-weight: 500;
            padding: 8px 16px;
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
        }

        .home-link:hover {
            background: rgba(255, 255, 255, 0.3);
            color: white;
        }

        .row {
            margin-left: -0.5rem;
            margin-right: -0.5rem;
        }

        .col-md-6 {
            padding-left: 0.5rem;
            padding-right: 0.5rem;
        }

        @media (max-width: 480px) {
            .register-container {
                margin: 10px;
                padding: 2rem 1.5rem;
            }

            .register-title {
                font-size: 1.7rem;
            }
        }
    </style>
</head>
<body>
<!-- Lien retour à l'accueil -->
<a href="${pageContext.request.contextPath}/" class="home-link">🏠 Accueil</a>

<div class="register-container">
    <div class="register-header">
        <h1 class="register-title">Inscription</h1>
        <p class="register-subtitle">Créez votre compte</p>
    </div>

    <!-- Affichage des messages d'erreur avec JSTL -->
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger" role="alert">
            <i class="fas fa-exclamation-triangle"></i>
                ${requestScope.error}
        </div>
    </c:if>

    <!-- Affichage des messages de succès -->
    <c:if test="${not empty requestScope.success}">
        <div class="alert alert-success" role="alert">
            <i class="fas fa-check-circle"></i>
                ${requestScope.success}
        </div>
    </c:if>

    <!-- Formulaire d'inscription -->
    <form method="post" action="${pageContext.request.contextPath}/auth" novalidate>
        <input type="hidden" name="action" value="register">

        <!-- Nom et Prénom -->
        <div class="row">
            <div class="col-md-6">
                <div class="form-group">
                    <label for="nom" class="form-label">Nom *</label>
                    <input
                            type="text"
                            id="nom"
                            name="nom"
                            class="form-input"
                            placeholder="Votre nom"
                            required
                            minlength="2"
                            maxlength="50"
                            value="${requestScope.nom != null ? requestScope.nom : ''}"
                    >
                    <div class="invalid-feedback">
                        Le nom doit contenir entre 2 et 50 caractères.
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="form-group">
                    <label for="prenom" class="form-label">Prénom *</label>
                    <input
                            type="text"
                            id="prenom"
                            name="prenom"
                            class="form-input"
                            placeholder="Votre prénom"
                            required
                            minlength="2"
                            maxlength="50"
                            value="${requestScope.prenom != null ? requestScope.prenom : ''}"
                    >
                    <div class="invalid-feedback">
                        Le prénom doit contenir entre 2 et 50 caractères.
                    </div>
                </div>
            </div>
        </div>

        <!-- Email -->
        <div class="form-group">
            <label for="email" class="form-label">Adresse email *</label>
            <input
                    type="email"
                    id="email"
                    name="email"
                    class="form-input"
                    placeholder="votre.email@exemple.com"
                    required
                    value="${requestScope.email != null ? requestScope.email : ''}"
            >
            <div class="invalid-feedback">
                Veuillez saisir une adresse email valide.
            </div>
        </div>

        <!-- Type d'utilisateur -->
        <div class="form-group">
            <label for="type" class="form-label">Type de compte *</label>
            <select id="type" name="type" class="form-select" required>
                <option value="">-- Choisissez votre profil --</option>
                <option value="LOCATAIRE" ${requestScope.type == 'LOCATAIRE' ? 'selected' : ''}>
                    🏠 Locataire - Je recherche un logement
                </option>
                <option value="PROPRIETAIRE" ${requestScope.type == 'PROPRIETAIRE' ? 'selected' : ''}>
                    🏢 Propriétaire - Je loue mes biens
                </option>
                <option value="ADMIN" ${requestScope.type == 'ADMIN' ? 'selected' : ''}>
                    ⚙️ Administrateur - Je gère la plateforme
                </option>
            </select>
            <div class="invalid-feedback">
                Veuillez sélectionner un type de compte.
            </div>
        </div>

        <!-- Mots de passe -->
        <div class="row">
            <div class="col-md-6">
                <div class="form-group" style="position: relative;">
                    <label for="motDePasse" class="form-label">Mot de passe *</label>
                    <input
                            type="password"
                            id="motDePasse"
                            name="motDePasse"
                            class="form-input"
                            placeholder="••••••••"
                            required
                            minlength="6"
                    >
                    <span class="input-icon password-toggle" onclick="togglePassword('motDePasse')">👁️</span>
                    <div class="invalid-feedback">
                        Le mot de passe doit contenir au moins 6 caractères.
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="form-group" style="position: relative;">
                    <label for="confirmMotDePasse" class="form-label">Confirmer *</label>
                    <input
                            type="password"
                            id="confirmMotDePasse"
                            name="confirmMotDePasse"
                            class="form-input"
                            placeholder="••••••••"
                            required
                            minlength="6"
                    >
                    <span class="input-icon password-toggle" onclick="togglePassword('confirmMotDePasse')">👁️</span>
                    <div class="invalid-feedback">
                        Les mots de passe ne correspondent pas.
                    </div>
                </div>
            </div>
        </div>

        <button type="submit" class="btn-register" id="registerBtn">
            <span class="btn-text">Créer mon compte</span>
            <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
        </button>
    </form>

    <!-- Lien vers la connexion -->
    <div class="login-link">
        <p>Vous avez déjà un compte ?</p>
        <a href="${pageContext.request.contextPath}/auth?action=login">
            Se connecter
        </a>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script>
    function togglePassword(inputId) {
        const passwordInput = document.getElementById(inputId);
        const toggleIcon = passwordInput.parentElement.querySelector('.password-toggle');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.textContent = '🙈';
        } else {
            passwordInput.type = 'password';
            toggleIcon.textContent = '👁️';
        }
    }

    // Animation d'entrée
    window.addEventListener('load', function() {
        const container = document.querySelector('.register-container');
        container.style.opacity = '0';
        container.style.transform = 'translateY(30px)';

        setTimeout(() => {
            container.style.transition = 'all 0.6s ease';
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 100);
    });

    // Validation Bootstrap et gestion du formulaire
    (function() {
        'use strict';

        const form = document.querySelector('form');
        const registerBtn = document.getElementById('registerBtn');
        const btnText = registerBtn.querySelector('.btn-text');
        const spinner = registerBtn.querySelector('.spinner-border');

        form.addEventListener('submit', function(event) {
            event.preventDefault();
            event.stopPropagation();

            // Validation des champs
            const nom = document.getElementById('nom');
            const prenom = document.getElementById('prenom');
            const email = document.getElementById('email');
            const type = document.getElementById('type');
            const motDePasse = document.getElementById('motDePasse');
            const confirmMotDePasse = document.getElementById('confirmMotDePasse');

            let isValid = true;

            // Validation nom
            if (!nom.value || nom.value.trim().length < 2) {
                nom.classList.add('is-invalid');
                isValid = false;
            } else {
                nom.classList.remove('is-invalid');
                nom.classList.add('is-valid');
            }

            // Validation prénom
            if (!prenom.value || prenom.value.trim().length < 2) {
                prenom.classList.add('is-invalid');
                isValid = false;
            } else {
                prenom.classList.remove('is-invalid');
                prenom.classList.add('is-valid');
            }

            // Validation email
            if (!email.value || !email.checkValidity()) {
                email.classList.add('is-invalid');
                isValid = false;
            } else {
                email.classList.remove('is-invalid');
                email.classList.add('is-valid');
            }

            // Validation type
            if (!type.value) {
                type.classList.add('is-invalid');
                isValid = false;
            } else {
                type.classList.remove('is-invalid');
                type.classList.add('is-valid');
            }

            // Validation mot de passe
            if (!motDePasse.value || motDePasse.value.length < 6) {
                motDePasse.classList.add('is-invalid');
                isValid = false;
            } else {
                motDePasse.classList.remove('is-invalid');
                motDePasse.classList.add('is-valid');
            }

            // Validation confirmation mot de passe
            if (!confirmMotDePasse.value || confirmMotDePasse.value !== motDePasse.value) {
                confirmMotDePasse.classList.add('is-invalid');
                isValid = false;
            } else {
                confirmMotDePasse.classList.remove('is-invalid');
                confirmMotDePasse.classList.add('is-valid');
            }

            if (isValid) {
                // Afficher le spinner
                btnText.textContent = 'Création en cours...';
                spinner.classList.remove('d-none');
                registerBtn.disabled = true;

                // Soumettre le formulaire
                setTimeout(() => {
                    form.submit();
                }, 500);
            }
        });

        // Supprimer les classes de validation lors de la saisie
        document.querySelectorAll('.form-input, .form-select').forEach(input => {
            input.addEventListener('input', function() {
                this.classList.remove('is-invalid', 'is-valid');
            });
        });

        // Validation en temps réel de la confirmation du mot de passe
        const motDePasse = document.getElementById('motDePasse');
        const confirmMotDePasse = document.getElementById('confirmMotDePasse');

        function validatePasswordMatch() {
            if (confirmMotDePasse.value && motDePasse.value) {
                if (confirmMotDePasse.value === motDePasse.value) {
                    confirmMotDePasse.classList.remove('is-invalid');
                    confirmMotDePasse.classList.add('is-valid');
                } else {
                    confirmMotDePasse.classList.remove('is-valid');
                    confirmMotDePasse.classList.add('is-invalid');
                }
            }
        }

        motDePasse.addEventListener('input', validatePasswordMatch);
        confirmMotDePasse.addEventListener('input', validatePasswordMatch);
    })();

    // Gestion des messages d'alerte (auto-hide après 5 secondes)
    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => {
                    alert.remove();
                }, 500);
            }, 5000);
        });
    });

    // Style pour les icônes de mot de passe
    const style = document.createElement('style');
    style.textContent = `
            .input-icon {
                position: absolute;
                right: 12px;
                top: 50%;
                transform: translateY(-50%);
                color: #999;
                font-size: 1.1rem;
                cursor: pointer;
                user-select: none;
                transition: color 0.3s ease;
            }
            .input-icon:hover {
                color: #667eea;
            }
        `;
    document.head.appendChild(style);
</script>
</body>
</html>