package controleur;

public enum Sens {
    GAUCHE_DROITE,
    DROITE_GAUCHE,
    SUD_NORD,
    NORD_SUD;

    /**
     * Indique si la direction concerne un véhicule terrestre.
     *
     * @return true si le sens est GAUCHE_DROITE ou DROITE_GAUCHE, false sinon
     */
    public boolean isTerrestre(){
        if (this == GAUCHE_DROITE || this == DROITE_GAUCHE){
            return true;
        }
        return false;
    }

    /**
     * Indique si la direction concerne un bateau.
     *
     * @return true si le sens est SUD_NORD ou NORD_SUD, false sinon
     */
    public boolean isMaritime(){
        if (this == SUD_NORD || this == NORD_SUD){
            return true;
        }
        return false;
    }

    /**
     * Retourne le sens opposé au sens courant.
     *
     * @return le sens inverse du sens courant
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
