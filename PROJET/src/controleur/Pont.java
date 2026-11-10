package controleur;

import thread.AffichageConsole;
import ressource.Bateau;
import thread.ControleurOuverture;
import ressource.Vehicule;

import java.util.LinkedList;
import java.util.Queue;

public class Pont {
    private final int nbSegment;
    private final Segment[] segments;
    private EtatPont etat;
    private ControleurOuverture controleur;

    private int vehiculesGD = 0;
    private int vehiculesDG = 0;

    private int convoisEnAttenteGD = 0;
    private int convoisEnAttenteDG = 0;
    private int convoisSurPont = 0;

    private int prioritairesActifsGD = 0;
    private int prioritairesActifsDG = 0;

    private final Queue<Bateau> fileBateaux = new LinkedList<>();
    private boolean bateauEnCoursDePassage = false;

    /**
     * Construit un pont composé d'un nombre donné de segments, initialement à l'état FERME.
     *
     * @param nbSegment le nombre de segments du pont
     */
    public Pont(int nbSegment) {
        this.nbSegment = nbSegment;
        this.segments = new Segment[nbSegment];
        for (int i = 0; i < nbSegment; i++) {
            this.segments[i] = new Segment(i);
        }
        this.etat = EtatPont.FERME;
    }

    /**
     * Associe le contrôleur d'ouverture au pont.
     *
     * @param controleur le contrôleur qui gère le délai entre deux ouvertures
     */
    public void setControleur(ControleurOuverture controleur) {
        this.controleur = controleur;
    }

    /**
     * Retourne le nombre de segments du pont.
     *
     * @return le nombre de segments
     */
    public int getNbSegment() {
        return nbSegment;
    }

    /**
     * Retourne le tableau des segments du pont.
     *
     * @return les segments du pont
     */
    public Segment[] getSegments() {
        return segments;
    }

    /**
     * Retourne l'état courant du pont.
     *
     * @return l'état du pont (OUVERT, FERME, OUVERTURE ou FERMETURE)
     */
    public synchronized EtatPont getEtat() {
        return etat;
    }

    /**
     * Retourne le nombre total de véhicules prioritaires actuellement actifs sur le pont.
     *
     * @return le nombre de véhicules prioritaires en cours de passage
     */
    public synchronized int getPrioritairesActifs() {
        return prioritairesActifsGD + prioritairesActifsDG;
    }

    /**
     * Retourne le nombre de bateaux présents dans la file FIFO.
     *
     * @return le nombre de bateaux en attente ou en cours de passage
     */
    public synchronized int getNbBateauxEnAttente() {
        return fileBateaux.size();
    }

    /**
     * Fait entrer un véhicule sur le pont.
     *
     * @param v le véhicule qui entre
     * @param nbSeg le nombre de segments à réserver (1, 2 ou 4)
     * @param sens le sens de circulation du véhicule
     * @param isConvoi true si le véhicule est un convoi exceptionnel
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized void entrerPont(Vehicule v, int nbSeg, Sens sens, boolean isConvoi) throws InterruptedException {
        if (isConvoi) {
            if (sens == Sens.GAUCHE_DROITE) convoisEnAttenteGD++;
            else convoisEnAttenteDG++;
            AffichageConsole.afficherMessage(v.getIdVehicule() + " (Convoi) bloque l'entrée du sens opposé et attend.");
            AffichageConsole.rafraichirPont(this);
        }

        while (!peutEntrer(nbSeg, sens, isConvoi)) {
            wait();
        }

        if (isConvoi) {
            if (sens == Sens.GAUCHE_DROITE) convoisEnAttenteGD--;
            else convoisEnAttenteDG--;
            convoisSurPont++;
        }

        for (int i = 0; i < nbSeg; i++) {
            int idx = (sens == Sens.GAUCHE_DROITE) ? i : (nbSegment - 1 - i);
            segments[idx].occuper(v, sens);
        }

        if (sens == Sens.GAUCHE_DROITE) vehiculesGD++;
        else vehiculesDG++;

        AffichageConsole.afficherMessage(v.getIdVehicule() + " entre sur le pont (" + sens + ").");
        AffichageConsole.rafraichirPont(this);
    }

    /**
     * Vérifie si un véhicule peut entrer sur le pont.
     *
     * @param nbSeg le nombre de segments nécessaires à l'entrée
     * @param sens le sens de circulation du véhicule
     * @param isConvoi true si le véhicule est un convoi exceptionnel
     * @return true si le véhicule peut entrer immédiatement
     */
    private boolean peutEntrer(int nbSeg, Sens sens, boolean isConvoi) {
        if (etat != EtatPont.FERME) return false;

        if (sens == Sens.GAUCHE_DROITE && prioritairesActifsGD > 0) return false;
        if (sens == Sens.DROITE_GAUCHE && prioritairesActifsDG > 0) return false;

        if (!fileBateaux.isEmpty() && controleur != null && controleur.isOuvertureAutorisee()) return false;

        if (sens == Sens.GAUCHE_DROITE && convoisEnAttenteDG > 0) return false;
        if (sens == Sens.DROITE_GAUCHE && convoisEnAttenteGD > 0) return false;
        if (convoisSurPont > 0) {
            if (sens == Sens.GAUCHE_DROITE && vehiculesDG > 0) return false;
            if (sens == Sens.DROITE_GAUCHE && vehiculesGD > 0) return false;
        }

        if (isConvoi) {
            if (sens == Sens.GAUCHE_DROITE && vehiculesDG > 0) return false;
            if (sens == Sens.DROITE_GAUCHE && vehiculesGD > 0) return false;
        }

        for (int i = 0; i < nbSeg; i++) {
            int idx = (sens == Sens.GAUCHE_DROITE) ? i : (nbSegment - 1 - i);
            if (!segments[idx].isLibre(sens)) return false;
        }
        return true;
    }

    /**
     * Fait avancer un véhicule d'un segment.
     *
     * @param v le véhicule qui avance
     * @param prochainIdx l'indice du segment à réserver
     * @param ancienIdx l'indice du segment à libérer
     * @param sens le sens de circulation du véhicule
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized void avancerSegment(Vehicule v, int prochainIdx, int ancienIdx, Sens sens) throws InterruptedException {
        while ((sens == Sens.GAUCHE_DROITE && prioritairesActifsGD > 0) ||
                (sens == Sens.DROITE_GAUCHE && prioritairesActifsDG > 0) ||
                !segments[prochainIdx].isLibre(sens)) {
            wait();
        }
        segments[prochainIdx].occuper(v, sens);
        segments[ancienIdx].sortir(v, sens);

        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Fait sortir complètement un véhicule du pont en libérant ses derniers segments.
     *
     * @param v le véhicule qui sort
     * @param nbSeg le nombre de segments occupés par le véhicule
     * @param sens le sens de circulation du véhicule
     * @param isConvoi true si le véhicule est un convoi exceptionnel
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized void sortirPont(Vehicule v, int nbSeg, Sens sens, boolean isConvoi) throws InterruptedException {
        while ((sens == Sens.GAUCHE_DROITE && prioritairesActifsGD > 0) ||
                (sens == Sens.DROITE_GAUCHE && prioritairesActifsDG > 0)) {
            wait();
        }
        for (int i = 0; i < nbSeg; i++) {
            int idx = (sens == Sens.GAUCHE_DROITE) ? (nbSegment - 1 - i) : i;
            segments[idx].sortir(v, sens);
        }

        if (sens == Sens.GAUCHE_DROITE) vehiculesGD--;
        else vehiculesDG--;

        if (isConvoi) convoisSurPont--;

        AffichageConsole.afficherMessage(v.getIdVehicule() + " a quitté le pont.");
        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Enregistre l'arrivée d'un véhicule prioritaire.
     *
     * @param v le véhicule prioritaire
     * @param sens le sens de circulation du véhicule
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized void entrerPrioritaire(Vehicule v, Sens sens) throws InterruptedException {
        if (sens == Sens.GAUCHE_DROITE) prioritairesActifsGD++;
        else prioritairesActifsDG++;

        AffichageConsole.afficherMessage("🚨 URGENCE : " + v.getIdVehicule() + " arrive ! Bande d'urgence pour le sens " + sens);
        AffichageConsole.rafraichirPont(this);
        notifyAll();

        while (etat != EtatPont.FERME && etat != EtatPont.OUVERTURE) {
            wait();
        }
        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " traverse le pont à toute vitesse !");
    }

    /**
     * Enregistre la sortie d'un véhicule prioritaire.
     *
     * @param v le véhicule prioritaire qui quitte le pont
     * @param sens le sens de circulation du véhicule
     */
    public synchronized void sortirPrioritaire(Vehicule v, Sens sens) {
        if (sens == Sens.GAUCHE_DROITE) prioritairesActifsGD--;
        else prioritairesActifsDG--;

        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " a quitté le pont. Reprise de la circulation (" + sens + ").");
        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Inscrit un bateau dans la file FIFO commune et le bloque jusqu'à son tour.
     *
     * @param b le bateau qui demande à passer
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized void demanderPassageBateau(Bateau b) throws InterruptedException {
        fileBateaux.add(b);
        AffichageConsole.afficherMessage("⛵ " + b.getNomBateau() + " rejoint la file FIFO (Position " + fileBateaux.size() + ").");
        AffichageConsole.rafraichirPont(this);
        notifyAll();

        while (fileBateaux.peek() != b || bateauEnCoursDePassage) {
            wait();
        }

        if (etat == EtatPont.FERME) {
            while (controleur != null && !controleur.isOuvertureAutorisee()) {
                wait();
            }
            etat = EtatPont.OUVERTURE;
            AffichageConsole.afficherMessage("🌉 Pont en cours d'OUVERTURE (attente de la sortie des véhicules engagés)...");
            AffichageConsole.rafraichirPont(this);

            while (vehiculesGD > 0 || vehiculesDG > 0 || getPrioritairesActifs() > 0) {
                wait();
            }

            etat = EtatPont.OUVERT;
            AffichageConsole.afficherMessage("🌉 Le pont est OUVERT aux bateaux !");
            AffichageConsole.rafraichirPont(this);
        }

        bateauEnCoursDePassage = true;
    }

    /**
     * Termine le passage d'un bateau en le retirant de la file FIFO.
     *
     * @param b le bateau qui a fini sa traversée
     */
    public synchronized void terminerPassageBateau(Bateau b) {
        fileBateaux.poll();
        bateauEnCoursDePassage = false;
        AffichageConsole.afficherMessage("⛵ " + b.getNomBateau() + " a terminé sa traversée.");

        if (fileBateaux.isEmpty()) {
            etat = EtatPont.FERMETURE;
            AffichageConsole.afficherMessage("🌉 File de bateaux vide : Pont en cours de FERMETURE...");
            AffichageConsole.rafraichirPont(this);

            etat = EtatPont.FERME;
            AffichageConsole.afficherMessage("🌉 Le pont est FERMÉ (ouvert à la circulation routière).");
            if (controleur != null) {
                controleur.notifierFermetureComplete();
            }
        }
        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Réveille tous les threads en attente sur le pont une fois le délai écoulé.
     */
    public synchronized void notifierDelaiEcoule() {
        notifyAll();
    }
}