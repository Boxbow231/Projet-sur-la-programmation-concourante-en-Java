package controleur;

public class Pont {

    //espace controleur
    private final Segment[]  segments;
    private EtatPont etat;
    private final int nbSegment;

    //espace ressource
    private int vehiculesSurPont = 0;
    private boolean convoiExceptionnelEnApproche = false;
    private boolean vehiculePrioritaireActif = false;

    //espace thread
    //à faire après le reste

    public Pont(int nbSegment) {
        this.nbSegment = nbSegment;
        this.segments = new Segment[nbSegment];
        for(int i = 0; i < nbSegment; i++) {
            this.segments[i] = new Segment();
        }
        this.etat = EtatPont.FERME;
    }

    public Segment[] getSegments() {
        return segments;
    }
    public int getNbSegment() {
        return nbSegment;
    }

    public int getVehiculesSurPont() {
        return vehiculesSurPont;
    }

    public void setVehiculesSurPont(int vehiculesSurPont) {
        this.vehiculesSurPont = vehiculesSurPont;
    }

    public boolean isConvoiExceptionnelEnApproche() {
        return convoiExceptionnelEnApproche;
    }

    public boolean isVehiculePrioritaireActif() {
        return vehiculePrioritaireActif;
    }
    public synchronized void reserverSegments(int indexCourant, int nbSegmentsNecessaires, Sens sens) throws InterruptedException {
        while (etat != EtatPont.FERME || vehiculePrioritaireActif || !sontSegmentsLibres(indexCourant, nbSegmentsNecessaires, sens)) {
            wait();
        }
        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexAReserver = calculerIndexReel(indexCourant, i, sens);
            segments[indexAReserver].setOccupe(true);
        }

    }

    private int calculerIndexReel(int indexCourant, int i, Sens sens) {
    }

    private boolean sontSegmentsLibres(int indexCourant, int nbSegmentsNecessaires, Sens sens) {
        if
    }
}



