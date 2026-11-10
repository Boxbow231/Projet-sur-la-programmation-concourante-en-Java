package controleur;

import ressource.Vehicule;

public class Segment {
    private final int id;
    private Vehicule occupantGD = null;
    private Vehicule occupantDG = null;

    /**
     * Construit un segment du pont.
     *
     * @param id numéro du segment sur le pont
     */
    public Segment(int id) {
        this.id = id;
    }

    /**
     * Retourne le numéro du segment.
     *
     * @return l'identifiant du segment
     */
    public int getId() {
        return id;
    }

    /**
     * Indique si le segment est libre pour un sens de circulation donné.
     *
     * @param sens le sens de circulation concerné
     * @return true si aucun véhicule n'occupe le segment dans ce sens
     */
    public synchronized boolean isLibre(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? (occupantGD == null) : (occupantDG == null);
    }

    /**
     * Marque le segment comme occupé par un véhicule dans un sens donné.
     *
     * @param v le véhicule qui occupe le segment
     * @param sens le sens de circulation du véhicule
     */
    public synchronized void occuper(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            occupantGD = v;
        } else {
            occupantDG = v;
        }
    }

    /**
     * Libère le segment si le véhicule indiqué en est bien l'occupant dans ce sens.
     *
     * @param v le véhicule qui quitte le segment
     * @param sens le sens de circulation du véhicule
     */
    public synchronized void sortir(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE && occupantGD == v) {
            occupantGD = null;
        } else if (sens == Sens.DROITE_GAUCHE && occupantDG == v) {
            occupantDG = null;
        }
    }

    /**
     * Retourne le véhicule qui occupe le segment dans un sens donné.
     *
     * @param sens le sens de circulation concerné
     * @return le véhicule occupant, ou null si le segment est libre
     */
    public synchronized Vehicule getOccupant(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? occupantGD : occupantDG;
    }
}
