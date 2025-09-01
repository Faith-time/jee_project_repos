package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.*;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Administrateur;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Entities.Locataire;

import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service d'authentification sécurisé
 * Gère l'inscription, la connexion et la validation des utilisateurs
 */
public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    // Pattern pour validation email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    /**
     * Constructeur avec injection de dépendances (pour les tests)
     */
    public AuthService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    /**
     * Inscrire un nouvel utilisateur avec validation complète
     * @param utilisateur L'utilisateur à inscrire
     * @throws IllegalArgumentException Si les données sont invalides
     * @throws RuntimeException Si l'email existe déjà ou erreur système
     */
    public void inscription(Utilisateur utilisateur) throws IllegalArgumentException, RuntimeException {
        // Validation des données
        validateUserData(utilisateur);

        // Vérifier si l'email existe déjà
        if (existeDeja(utilisateur.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        try {
            // Normaliser les données
            utilisateur.setEmail(utilisateur.getEmail().toLowerCase().trim());
            utilisateur.setNom(utilisateur.getNom().trim());
            utilisateur.setPrenom(utilisateur.getPrenom().trim());

            // Hash du mot de passe (optionnel pour le moment)
            // utilisateur.setMotDePasse(hashPassword(utilisateur.getMotDePasse()));

            // Sauvegarder l'utilisateur
            utilisateurDAO.save(utilisateur);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage(), e);
        }
    }

    /**
     * Connexion d'un utilisateur avec validation
     * @param email Email de connexion
     * @param motDePasse Mot de passe
     * @return L'utilisateur si la connexion est réussie, null sinon
     */
    public Utilisateur connexion(String email, String motDePasse) {
        // Validation des paramètres d'entrée
        if (email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            return null;
        }

        try {
            // Rechercher l'utilisateur par email
            Utilisateur utilisateur = utilisateurDAO.findByEmail(email.toLowerCase().trim());

            // Vérifier le mot de passe
            if (utilisateur != null && verifyPassword(motDePasse, utilisateur.getMotDePasse())) {
                return utilisateur; // Connexion réussie
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            // Ne pas révéler les erreurs techniques à l'utilisateur
        }

        return null; // Échec de connexion
    }

    /**
     * Vérifier si un utilisateur existe avec cet email
     * @param email Email à vérifier
     * @return true si l'utilisateur existe, false sinon
     */
    public boolean existeDeja(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            return utilisateurDAO.findByEmail(email.toLowerCase().trim()) != null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification d'existence: " + e.getMessage());
            return false; // En cas d'erreur, considérer comme n'existant pas
        }
    }

    /**
     * Déterminer le rôle d'un utilisateur basé sur son type de classe
     * @param utilisateur L'utilisateur
     * @return Le rôle (ADMIN, PROPRIETAIRE, LOCATAIRE, UTILISATEUR)
     */
    public String determinerRole(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return "VISITEUR";
        }

        // Détermination basée sur le type de classe
        if (utilisateur instanceof Administrateur) {
            return "ADMIN";
        } else if (utilisateur instanceof Proprietaire) {
            return "PROPRIETAIRE";
        } else if (utilisateur instanceof Locataire) {
            return "LOCATAIRE";
        } else {
            return "UTILISATEUR";
        }
    }

    /**
     * Changer le mot de passe d'un utilisateur
     * @param utilisateurId ID de l'utilisateur
     * @param ancienMotDePasse Ancien mot de passe
     * @param nouveauMotDePasse Nouveau mot de passe
     * @return true si le changement est réussi
     */
    public boolean changerMotDePasse(Long utilisateurId, String ancienMotDePasse, String nouveauMotDePasse) {
        if (utilisateurId == null || ancienMotDePasse == null || nouveauMotDePasse == null) {
            return false;
        }

        // Validation du nouveau mot de passe
        if (nouveauMotDePasse.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);

            if (utilisateur != null && verifyPassword(ancienMotDePasse, utilisateur.getMotDePasse())) {
                utilisateur.setMotDePasse(nouveauMotDePasse);
                // utilisateur.setMotDePasse(hashPassword(nouveauMotDePasse)); // Si hashage activé

                utilisateurDAO.update(utilisateur);
                return true;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du changement de mot de passe: " + e.getMessage());
        }

        return false;
    }

    /**
     * Valider les données d'un utilisateur
     */
    private void validateUserData(Utilisateur utilisateur) throws IllegalArgumentException {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        // Validation du nom
        if (utilisateur.getNom() == null || utilisateur.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (utilisateur.getNom().trim().length() < 2 || utilisateur.getNom().trim().length() > 50) {
            throw new IllegalArgumentException("Le nom doit contenir entre 2 et 50 caractères");
        }

        // Validation du prénom
        if (utilisateur.getPrenom() == null || utilisateur.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (utilisateur.getPrenom().trim().length() < 2 || utilisateur.getPrenom().trim().length() > 50) {
            throw new IllegalArgumentException("Le prénom doit contenir entre 2 et 50 caractères");
        }

        // Validation de l'email
        if (utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (!EMAIL_PATTERN.matcher(utilisateur.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Format d'email invalide");
        }

        // Validation du mot de passe
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
        if (utilisateur.getMotDePasse().length() > 100) {
            throw new IllegalArgumentException("Le mot de passe ne peut dépasser 100 caractères");
        }
    }

    /**
     * Vérifier le mot de passe
     * Comparaison directe pour le moment, peut être remplacé par vérification de hash
     */
    private boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }

        // Comparaison directe pour le moment
        return plainPassword.equals(storedPassword);

        // Si hashage activé :
        // return hashPassword(plainPassword).equals(storedPassword);
    }

    /**
     * Hash du mot de passe (pour amélioration future de la sécurité)
     * Non utilisé actuellement pour simplifier le développement
     */
    @SuppressWarnings("unused")
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    /**
     * Obtenir un utilisateur par son ID
     * @param id ID de l'utilisateur
     * @return L'utilisateur ou null si non trouvé
     */
    public Utilisateur getUtilisateurById(Long id) {
        if (id == null) {
            return null;
        }

        try {
            return utilisateurDAO.findById(id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtenir un utilisateur par son email
     * @param email Email de l'utilisateur
     * @return L'utilisateur ou null si non trouvé
     */
    public Utilisateur getUtilisateurByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            return utilisateurDAO.findByEmail(email.toLowerCase().trim());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
            return null;
        }
    }
}