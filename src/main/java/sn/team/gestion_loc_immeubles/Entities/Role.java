package sn.team.gestion_loc_immeubles.Entities;

/**
 * Énumération des rôles disponibles dans l'application
 */
public enum Role {
    ADMIN("Administrateur"),
    PROPRIETAIRE("Propriétaire"),
    LOCATAIRE("Locataire"),
    VISITEUR("Visiteur"); // Nouveau rôle pour les utilisateurs non-locataires

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne le rôle à partir d'une chaîne de caractères
     * @param roleString Le nom du rôle en string
     * @return Le rôle correspondant ou VISITEUR par défaut
     */
    public static Role fromString(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return VISITEUR; // Changé de LOCATAIRE à VISITEUR
        }

        try {
            return Role.valueOf(roleString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Si le rôle n'existe pas, retourner VISITEUR par défaut
            return VISITEUR;
        }
    }

    /**
     * Vérifie si le rôle a des permissions d'administration
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Vérifie si le rôle peut gérer des immeubles
     */
    public boolean canManageImmeubles() {
        return this == ADMIN || this == PROPRIETAIRE;
    }

    /**
     * Vérifie si le rôle peut avoir des contrats de location
     */
    public boolean canHaveContracts() {
        return this == LOCATAIRE;
    }

    /**
     * Vérifie si le rôle peut voir les immeubles et unités disponibles
     */
    public boolean canViewAvailableUnits() {
        return this == VISITEUR || this == LOCATAIRE || this == ADMIN || this == PROPRIETAIRE;
    }

    /**
     * Vérifie si le rôle peut effectuer des demandes de location
     */
    public boolean canRequestRental() {
        return this == VISITEUR;
    }

    @Override
    public String toString() {
        return this.name();
    }
}