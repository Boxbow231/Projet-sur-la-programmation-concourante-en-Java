package controleur;

/**
 * Représente les directions de circulation du pont pour les véhicules terrestres et les bateaux.
 */
public enum Sens {
    GAUCHE_DROITE,
    DROITE_GAUCHE,
    SUD_NORD,
    NORD_SUD;

    /**
     * Indique si la direction concerne un véhicule terrestre.
     */
    public boolean isTerrestre(){
        return this == GAUCHE_DROITE || this == DROITE_GAUCHE;
    }

    /**
     * Indique si la direction concerne un bateau.
     */
    public boolean isMaritime(){
        return this == SUD_NORD || this == NORD_SUD;
    }

    /**
     * Retourne le sens inverse (utilisé notamment pour la gestion des convois exceptionnels).
     */
    public Sens getSensOppose() {
        switch (this) {
            case GAUCHE_DROITE: return DROITE_GAUCHE;
            case DROITE_GAUCHE: return GAUCHE_DROITE;
            case NORD_SUD: return SUD_NORD;
            case SUD_NORD: return NORD_SUD;
            default: return null;
        }
    }
}