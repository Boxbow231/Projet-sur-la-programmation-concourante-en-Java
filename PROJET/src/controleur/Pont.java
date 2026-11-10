package controleur;

import ressource.Vehicule;
import ressource.VehiculePrioritaire;
import thread.ControleurOuverture;

public class Pont {

    private final Segment[] segments;
    private final int nbSegment;
    private EtatPont etat;

    private int vehiculesSurPont = 0;
    private int vehiculesSensGaucheDroite = 0;
    private int vehiculesSensDroiteGauche = 0;

    private boolean convoiEnAttenteGaucheDroite = false;
    private boolean convoiEnAttenteDroiteGauche = false;
    private boolean vehiculePrioritaireActif = false;

    private ControleurOuverture controleur;

    public Pont(int nbSegment) {
        this.nbSegment = nbSegment;
        this.segments = new Segment[nbSegment];
        for (int i = 0; i < nbSegment; i++) {
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

    public synchronized EtatPont getEtat() {
        return etat;
    }

    public synchronized int getVehiculesSurPont() {
        return vehiculesSurPont;
    }

    public synchronized boolean isVehiculePrioritaireActif() {
        return vehiculePrioritaireActif;
    }

    /**
     * Entrée sur le pont : incrémente les compteurs du sens concerné.
     */
    public synchronized void entrerPont(Sens sens) {
        vehiculesSurPont++;
        if (sens == Sens.GAUCHE_DROITE) {
            vehiculesSensGaucheDroite++;
        } else if (sens == Sens.DROITE_GAUCHE) {
            vehiculesSensDroiteGauche++;
        }
    }

    /**
     * Sortie du pont : décrémente les compteurs et notifie les threads en attente.
     */
    public synchronized void sortirPont(Sens sens) {
        vehiculesSurPont--;
        if (sens == Sens.GAUCHE_DROITE) {
            vehiculesSensGaucheDroite--;
        } else if (sens == Sens.DROITE_GAUCHE) {
            vehiculesSensDroiteGauche--;
        }

        // Si le pont doit s'ouvrir et que tous les véhicules sont sortis
        if (vehiculesSurPont == 0 && etat == EtatPont.OUVERTURE) {
            notifyAll();
        }

        // Réveille les convois ou véhicules en attente d'évacuation
        notifyAll();
    }

    /**
     * Réservation atomique des segments demandés.
     */
    public synchronized void reserverSegments(int indexTete, int nbSegmentsNecessaires, Sens sens, Vehicule v) throws InterruptedException {
        boolean estPrioritaire = (v instanceof VehiculePrioritaire);

        while (!peutReserver(indexTete, nbSegmentsNecessaires, sens, estPrioritaire)) {
            wait();
        }

        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexAReserver = calculerIndexReel(indexTete, i, sens);
            segments[indexAReserver].tryOccuper(v);
        }
    }

    /**
     * Libère les segments quittés et notifie les autres véhicules.
     */
    public synchronized void libererSegments(int indexTete, int nbSegmentsNecessaires, Sens sens, Vehicule v) {
        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int indexALiberer = calculerIndexReel(indexTete, i, sens);
            if (indexALiberer >= 0 && indexALiberer < nbSegment) {
                segments[indexALiberer].sortir(v);
            }
        }
        notifyAll();
    }

    /**
     * Vérifie la disponibilité des segments et les règles de circulation.
     */
    private boolean peutReserver(int indexTete, int nbSegmentsNecessaires, Sens sens, boolean estPrioritaire) {
        // Le pont doit être FERME pour la circulation routière
        if (etat != EtatPont.FERME) {
            return false;
        }

        if (vehiculePrioritaireActif && !estPrioritaire) {
            return false;
        }

        if (sens == Sens.GAUCHE_DROITE && convoiEnAttenteDroiteGauche && indexTete == 0) {
            return false;
        }
        if (sens == Sens.DROITE_GAUCHE && convoiEnAttenteGaucheDroite && indexTete == nbSegment - 1) {
            return false;
        }

        for (int i = 0; i < nbSegmentsNecessaires; i++) {
            int index = calculerIndexReel(indexTete, i, sens);
            if (index < 0 || index >= nbSegment || !segments[index].isLibre()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calcule l'indice d'un segment selon le sens de marche.
     * Pour un convoi/camion, la tête avance et la queue s'étend en arrière.
     */
    private int calculerIndexReel(int indexTete, int decalageArriere, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            return indexTete - decalageArriere;
        } else {
            return indexTete + decalageArriere;
        }
    }


    public synchronized void declarerConvoiArrive(Sens sens) throws InterruptedException {
        if (sens == Sens.GAUCHE_DROITE) {
            convoiEnAttenteGaucheDroite = true;

            while (vehiculesSensDroiteGauche > 0) {
                wait();
            }
        } else {
            convoiEnAttenteDroiteGauche = true;

            while (vehiculesSensGaucheDroite > 0) {
                wait();
            }
        }
    }

    public synchronized void libererConvoi(Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) {
            convoiEnAttenteGaucheDroite = false;
        } else {
            convoiEnAttenteDroiteGauche = false;
        }
        notifyAll();
    }


    public synchronized void demanderOuverture() throws InterruptedException {
        while (!controleur.isOuvertureAutorisee()) {
        wait();
        }

        etat = EtatPont.OUVERTURE; 
        while (vehiculesSurPont > 0) {
            wait();
        }
        etat = EtatPont.OUVERT; 
        notifyAll();
    }

    public synchronized void demarrerFermeture() {
        etat = EtatPont.FERMETURE;
        notifyAll();
    }

    public synchronized void terminerOuverture() {
        etat = EtatPont.FERMETURE;
        etat = EtatPont.FERME;
        notifyAll(); 

        // On lance le chronomètre d'une minute[cite: 4]
        controleur.notifierFermetureComplete(); 
    }

    // Méthode appelée par le TimerTask du contrôleur
    public synchronized void notifierDelaiEcoule() {
        notifyAll(); // Réveille les bateaux qui attendaient que l'heure passe
    }

    public synchronized void terminerFermeture() {
        etat = EtatPont.FERME;
        notifyAll();
    }


    public synchronized void activerUrgence() {
        vehiculePrioritaireActif = true;
        notifyAll();
    }

    public synchronized void desactiverUrgence() {
        vehiculePrioritaireActif = false;
        notifyAll();
    }

    public void setControleur(ControleurOuverture controleur) {
        this.controleur = controleur;
    }
}