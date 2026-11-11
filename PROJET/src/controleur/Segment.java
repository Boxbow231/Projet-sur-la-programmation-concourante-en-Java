package controleur;

import ressource.Vehicule;

/**
 * Représente un segment individuel du pont, gérant deux voies de circulation distinctes.
 */
public class Segment {
    private final int id;
    private Vehicule occupantGD = null;
    private Vehicule occupantDG = null;

    /**
     * Initialise un segment avec un identifiant unique.
     */
    public Segment(int id) {
        this.id = id;
    }

    /**
     * Retourne l'identifiant du segment.
     */
    public int getId() {
        return id;
    }

    /**
     * Vérifie si la voie demandée est libre sur ce segment.
     */
    public synchronized boolean isLibre(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? (occupantGD == null) : (occupantDG == null);
    }

    /**
     * Assigne un véhicule à la voie demandée sur ce segment.
     */
    public synchronized void occuper(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            occupantGD = v;
        } else {
            occupantDG = v;
        }
    }

    /**
     * Libère la voie demandée de ce segment si elle était occupée par le véhicule spécifié.
     */
    public synchronized void sortir(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE && occupantGD == v) {
            occupantGD = null;
        } else if (sens == Sens.DROITE_GAUCHE && occupantDG == v) {
            occupantDG = null;
        }
    }

    /**
     * Retourne le véhicule occupant actuellement la voie demandée sur ce segment.
     */
    public synchronized Vehicule getOccupant(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? occupantGD : occupantDG;
    }
}