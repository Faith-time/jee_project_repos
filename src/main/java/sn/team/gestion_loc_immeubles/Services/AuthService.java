package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.*;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Role;

import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Service d'authentification sécurisé avec système de rôles incluant VISITEUR
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
     * Les nouveaux utilisateurs ont automatiquement le rôle VISITEUR
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

            // Forcer le rôle VISITEUR pour tous les nouveaux utilisateurs
            utilisateur.setRole(Role.VISITEUR);

            // Hash du mot de passe (optionnel pour le moment)
            // utilisateur.setMotDePasse(hashPassword(utilisateur.getMotDePasse()));

            // Sauvegarder l'utilisateur
            utilisateurDAO.save(utilisateur);

            System.out.println("[DEBUG] Nouvel utilisateur inscrit: "
                    + utilisateur.getEmail() + " avec le rôle " + utilisateur.getRole());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage(), e);
        }
    }

    /**
     * Promouvoir un visiteur au rôle LOCATAIRE
     * Cette méthode est appelée quand un visiteur effectue sa première location
     * @param utilisateurId ID de l'utilisateur à promouvoir
     * @return true si la promotion est réussie
     */
    public boolean promouvoirVisiteurEnLocataire(Long utilisateurId) {
        if (utilisateurId == null) {
            return false;
        }

        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);

            if (utilisateur != null && utilisateur.getRole() == Role.VISITEUR) {
                utilisateur.setRole(Role.LOCATAIRE);
                utilisateurDAO.update(utilisateur);
                System.out.println("[DEBUG] Utilisateur promu de VISITEUR à LOCATAIRE: " + utilisateur.getEmail());
                return true;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la promotion du visiteur: " + e.getMessage());
        }

        return false;
    }

    /**
     * Connexion d'un utilisateur avec validation
     * @param email Email de connexion
     * @param motDePasse Mot de passe
     * @return L'utilisateur si la connexion est réussie, null sinon
     */
    public Utilisateur connexion(String email, String motDePasse) {
        if (email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            System.out.println("[DEBUG] Email ou mot de passe vide → connexion échouée");
            return null;
        }

        try {
            String emailNormalise = email.toLowerCase().trim();
            System.out.println("[DEBUG] Tentative de connexion avec email=" + emailNormalise);

            Utilisateur utilisateur = utilisateurDAO.findByEmail(emailNormalise);

            if (utilisateur == null) {
                System.out.println("[DEBUG] Aucun utilisateur trouvé avec cet email");
                return null;
            }

            System.out.println("[DEBUG] Utilisateur trouvé : " + utilisateur.getEmail()
                    + " avec rôle " + utilisateur.getRole());

            // Vérification du mot de passe
            boolean mdpOK = verifyPassword(motDePasse, utilisateur.getMotDePasse());
            System.out.println("[DEBUG] Vérification mot de passe : " + (mdpOK ? "OK ✅" : "ÉCHEC ❌"));

            if (mdpOK) {
                System.out.println("[DEBUG] Connexion réussie pour " + utilisateur.getEmail()
                        + " → rôle=" + utilisateur.getRole());
                return utilisateur;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println("[ERREUR] Exception lors de la connexion pour email=" + email);
            e.printStackTrace();
        }

        return null;
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
            return false;
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

    /**
     * Obtenir tous les utilisateurs ayant un rôle spécifique
     * @param role Le rôle recherché
     * @return Liste des utilisateurs avec ce rôle
     */
    public List<Utilisateur> getUtilisateursByRole(Role role) {
        if (role == null) {
            return List.of();
        }

        try {
            return utilisateurDAO.findByRole(role);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des utilisateurs par rôle: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Changer le rôle d'un utilisateur
     * @param utilisateurId ID de l'utilisateur
     * @param nouveauRole Le nouveau rôle
     * @return true si le changement est réussi
     */
    public boolean changerRole(Long utilisateurId, Role nouveauRole) {
        if (utilisateurId == null || nouveauRole == null) {
            return false;
        }

        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);

            if (utilisateur != null) {
                utilisateur.setRole(nouveauRole);
                utilisateurDAO.update(utilisateur);
                System.out.println("[DEBUG] Rôle changé pour " + utilisateur.getEmail()
                        + " : " + nouveauRole);
                return true;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du changement de rôle: " + e.getMessage());
        }

        return false;
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
     * Mettre à jour les informations d'un utilisateur
     * @param utilisateur L'utilisateur avec les nouvelles données
     * @return true si la mise à jour est réussie
     */
    public boolean mettreAJourUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getId() == null) {
            return false;
        }

        try {
            validateUserData(utilisateur);

            // Normaliser les données
            utilisateur.setEmail(utilisateur.getEmail().toLowerCase().trim());
            utilisateur.setNom(utilisateur.getNom().trim());
            utilisateur.setPrenom(utilisateur.getPrenom().trim());

            utilisateurDAO.update(utilisateur);
            return true;

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }

        return false;
    }

    /**
     * Supprimer un utilisateur
     * @param utilisateurId ID de l'utilisateur à supprimer
     * @return true si la suppression est réussie
     */
    public boolean supprimerUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) {
            return false;
        }

        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);
            if (utilisateur != null) {
                utilisateurDAO.delete(utilisateur.getId());
                System.out.println("[DEBUG] Utilisateur supprimé: " + utilisateur.getEmail());
                return true;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }

        return false;
    }

    /**
     * Vérifier si un utilisateur a les permissions pour une action
     * @param utilisateur L'utilisateur
     * @param action L'action demandée
     * @return true si l'utilisateur a les permissions
     */
    public boolean verifierPermissions(Utilisateur utilisateur, String action) {
        if (utilisateur == null || action == null) {
            return false;
        }

        Role role = utilisateur.getRole();

        return switch (action.toUpperCase()) {
            case "ADMIN_ACCESS" -> role == Role.ADMIN;
            case "MANAGE_USERS" -> role == Role.ADMIN;
            case "MANAGE_IMMEUBLES" -> role == Role.ADMIN || role == Role.PROPRIETAIRE;
            case "VIEW_CONTRACTS" -> role == Role.ADMIN || role == Role.PROPRIETAIRE || role == Role.LOCATAIRE;
            case "CREATE_CONTRACT" -> role == Role.ADMIN || role == Role.PROPRIETAIRE;
            case "VIEW_OWN_CONTRACTS" -> role == Role.LOCATAIRE;
            case "VIEW_AVAILABLE_UNITS" -> role == Role.VISITEUR || role == Role.LOCATAIRE || role == Role.ADMIN || role == Role.PROPRIETAIRE;
            case "REQUEST_RENTAL" -> role == Role.VISITEUR;
            default -> false;
        };
    }

    // ========== MÉTHODES PRIVÉES ==========

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

        // Validation du rôle
        if (utilisateur.getRole() == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
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
}