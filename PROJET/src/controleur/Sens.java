package controleur;
/**
 * Représente les directions de circulation du pont :
 * - Véhicules terrestres : GAUCHE_VERS_DROITE, DROITE_VERS_GAUCHE
 * - Bateaux : NORD_VERS_SUD, SUD_VERS_NORD
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
        if (this == GAUCHE_DROITE || this == DROITE_GAUCHE){
            return true;
        }
        return false;
    }

    /**
     * Indique si la direction concerne un bateau.
     */
    public boolean isMaritime(){
        if (this == SUD_NORD || this == NORD_SUD){
            return true;
        }
        return false;
    }

    /**
     * Retourne le sens inverse (très utile pour bloquer la voie opposée
     * lors du passage d'un convoi exceptionnel).
     */
    public Sens getSensOppose() {
        switch (this) {
            case GAUCHE_DROITE: return DROITE_GAUCHE;
            case DROITE_GAUCHE: return GAUCHE_DROITE;
            case NORD_SUD:return SUD_NORD;
            case SUD_NORD:return NORD_SUD;
            default: return null;
        }
    }

}
