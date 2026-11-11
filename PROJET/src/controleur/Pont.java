package controleur;

import thread.AffichageConsole;
import ressource.Bateau;
import thread.ControleurOuverture;
import ressource.Vehicule;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Représente le pont mobile (ressource partagée centrale).
 * Gère la synchronisation des threads (véhicules et bateaux), les priorités et les états.
 */
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

    private int prioritairesActifs = 0;

    private final Queue<Bateau> fileBateaux = new LinkedList<>();
    private boolean bateauEnCoursDePassage = false;

    /**
     * Initialise le pont avec un nombre défini de segments.
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
     * Associe le contrôleur d'ouverture (timer) au pont.
     */
    public void setControleur(ControleurOuverture controleur) {
        this.controleur = controleur;
    }

    /**
     * Retourne le nombre total de segments du pont.
     */
    public int getNbSegment() {
        return nbSegment;
    }

    /**
     * Retourne le tableau contenant les segments du pont.
     */
    public Segment[] getSegments() {
        return segments;
    }

    /**
     * Retourne l'état actuel du pont.
     */
    public synchronized EtatPont getEtat() {
        return etat;
    }

    /**
     * Retourne le nombre de véhicules prioritaires actuellement sur le pont.
     */
    public synchronized int getPrioritairesActifs() {
        return prioritairesActifs;
    }

    /**
     * Retourne la taille de la file d'attente FIFO des bateaux.
     */
    public synchronized int getNbBateauxEnAttente() {
        return fileBateaux.size();
    }

    /**
     * Gère l'entrée d'un véhicule terrestre sur le pont en réservant simultanément les segments nécessaires.
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
     * Vérifie si les conditions sont réunies pour qu'un véhicule puisse entrer sur le pont.
     */
    private boolean peutEntrer(int nbSeg, Sens sens, boolean isConvoi) {
        if (etat != EtatPont.FERME || prioritairesActifs > 0) return false;
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
     * Fait avancer un véhicule d'un segment, en réservant le nouveau et en libérant l'ancien.
     */
    public synchronized void avancerSegment(Vehicule v, int prochainIdx, int ancienIdx, Sens sens) throws InterruptedException {
        while (prioritairesActifs > 0 || !segments[prochainIdx].isLibre(sens)) {
            wait();
        }
        segments[prochainIdx].occuper(v, sens);
        segments[ancienIdx].sortir(v, sens);

        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Gère la sortie complète d'un véhicule du pont et libère ses derniers segments occupés.
     */
    public synchronized void sortirPont(Vehicule v, int nbSeg, Sens sens, boolean isConvoi) throws InterruptedException {
        while (prioritairesActifs > 0) {
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
     * Gère l'arrivée d'un véhicule prioritaire (force la mise sur bande d'urgence des autres véhicules).
     */
    public synchronized void entrerPrioritaire(Vehicule v) throws InterruptedException {
        prioritairesActifs++;
        AffichageConsole.afficherMessage("🚨 URGENCE : " + v.getIdVehicule() + " arrive ! Mise sur bande d'urgence des véhicules.");
        AffichageConsole.rafraichirPont(this);
        notifyAll();

        while (etat != EtatPont.FERME && etat != EtatPont.OUVERTURE) {
            wait();
        }
        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " traverse le pont à toute vitesse !");
    }

    /**
     * Gère la sortie d'un véhicule prioritaire (autorise la reprise du trafic normal).
     */
    public synchronized void sortirPrioritaire(Vehicule v) {
        prioritairesActifs--;
        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " a quitté le pont. Reprise de la circulation.");
        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    /**
     * Enregistre un bateau dans la file FIFO et déclenche l'ouverture du pont si possible.
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

            while (vehiculesGD > 0 || vehiculesDG > 0 || prioritairesActifs > 0) {
                wait();
            }

            etat = EtatPont.OUVERT;
            AffichageConsole.afficherMessage("🌉 Le pont est OUVERT aux bateaux !");
            AffichageConsole.rafraichirPont(this);
        }

        bateauEnCoursDePassage = true;
    }

    /**
     * Gère la fin de traversée d'un bateau et déclenche la fermeture si la file est vide.
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
     * Réveille les threads en attente (notamment les bateaux) lorsque le délai d'ouverture est écoulé.
     */
    public synchronized void notifierDelaiEcoule() {
        notifyAll();
    }
}