package controleur;

import ressource.Vehicule;

public class Pont {

    // Espace controleur
    private final Segment[] segments;
    private EtatPont etat;
    private final int nbSegment;

    // Espace ressource
    private int vehiculesSurPont = 0;
    private boolean convoiExceptionnelEnApproche = false;
    private boolean vehiculePrioritaireActif = false;

    public Pont(int nbSegment) {
        this.nbSegment = nbSegment;
        this.segments = new Segment[nbSegment];
        for(int i = 0; i < nbSegment; i++) {
            this.segments[i] = new Segment(i);
        }
        this.etat = EtatPont.FERME;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public int getNbSegment() {
        return nbSegment;
    }

    public synchronized int getVehiculesSurPont() {
        return vehiculesSurPont;
    }

    public synchronized boolean isConvoiExceptionnelEnApproche() {
        return convoiExceptionnelEnApproche;
    }

    public synchronized boolean isVehiculePrioritaireActif() {
        return vehiculePrioritaireActif;
    }

    public synchronized void entrerPont() {
        vehiculesSurPont++;
    }

    public synchronized void sortirPont() {
        vehiculesSurPont--;
        if (vehiculesSurPont == 0 && etat == EtatPont.OUVERTURE) {
            notifyAll();
        }
    }
    public synchronized void reserverSegments(int indexCourant, int nbSegmentsNecessaires, Sens sens, Vehicule v) throws InterruptedException {
        while (etat != EtatPont.FERME || vehiculePrioritaireActif || !sontSegmentsLibres(indexCourant, nbSegmentsNecessaires, sens)) {
            wait();
        }

        // Réservation effective
        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexAReserver = calculerIndexReel(indexCourant, i, sens);
            segments[indexAReserver].tryOccuper(v);
        }
    }

    public synchronized void libererSegments(int indexCourant, int nbSegmentsNecessaires, Sens sens, Vehicule v) {
        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexALiberer = calculerIndexReel(indexCourant, i, sens);
            if (indexALiberer >= 0 && indexALiberer < nbSegment) {
                segments[indexALiberer].sortir(v);
            }
        }
        notifyAll();
    }

    private boolean sontSegmentsLibres(int indexCourant, int nbSegmentsNecessaires, Sens sens) {
        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexAVerifier = calculerIndexReel(indexCourant, i, sens);
            if (indexAVerifier < 0 || indexAVerifier >= nbSegment) {
                return false;
            }
            if (!segments[indexAVerifier].isLibre()) {
                return false;
            }
        }
        return true;
    }

    private int calculerIndexReel(int indexBase, int decalage, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            return indexBase - decalage;
        } else {
            return indexBase + decalage;
        }
    }

    public synchronized void demanderOuverture() throws InterruptedException {
        etat = EtatPont.OUVERTURE;
        while (vehiculesSurPont > 0) {
            wait();
        }
        etat = EtatPont.OUVERT;
        notifyAll();
    }

    public synchronized void terminerOuverture() {
        etat = EtatPont.FERMETURE;
        etat = EtatPont.FERME;
        notifyAll();
    }

    public synchronized void activerUrgence() {
        vehiculePrioritaireActif = true;
    }

    public synchronized void desactiverUrgence() {
        vehiculePrioritaireActif = false;
        notifyAll();
    }
}