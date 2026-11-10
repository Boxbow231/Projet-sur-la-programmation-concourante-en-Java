package controleur;

import ressource.Vehicule;

public class Segment {
    private final int id;
    private Vehicule occupantGD = null; // Voie Gauche -> Droite
    private Vehicule occupantDG = null; // Voie Droite -> Gauche

    public Segment(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized boolean isLibre(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? (occupantGD == null) : (occupantDG == null);
    }

    public synchronized void occuper(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            occupantGD = v;
        } else {
            occupantDG = v;
        }
    }

    public synchronized void sortir(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE && occupantGD == v) {
            occupantGD = null;
        } else if (sens == Sens.DROITE_GAUCHE && occupantDG == v) {
            occupantDG = null;
        }
    }

    public synchronized Vehicule getOccupant(Sens sens) {
        return (sens == Sens.GAUCHE_DROITE) ? occupantGD : occupantDG;
    }
}