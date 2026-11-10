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

    // Compteurs de véhicules sur le pont par sens
    private int vehiculesGD = 0;
    private int vehiculesDG = 0;

    // Gestion des Convois Exceptionnels
    private int convoisEnAttenteGD = 0;
    private int convoisEnAttenteDG = 0;
    private int convoisSurPont = 0;

    // Gestion des Véhicules Prioritaires (Ambulances / Pompiers)
    private int prioritairesActifs = 0;

    // Gestion des Bateaux (File FIFO commune)
    private final Queue<Bateau> fileBateaux = new LinkedList<>();
    private boolean bateauEnCoursDePassage = false;

    public Pont(int nbSegment) {
        this.nbSegment = nbSegment;
        this.segments = new Segment[nbSegment];
        for (int i = 0; i < nbSegment; i++) {
            this.segments[i] = new Segment(i);
        }
        this.etat = EtatPont.FERME;
    }

    public void setControleur(ControleurOuverture controleur) {
        this.controleur = controleur;
    }

    public int getNbSegment() {
        return nbSegment;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public synchronized EtatPont getEtat() {
        return etat;
    }

    public synchronized int getPrioritairesActifs() {
        return prioritairesActifs;
    }

    public synchronized int getNbBateauxEnAttente() {
        return fileBateaux.size();
    }

    // --- 1. GESTION DES VÉHICULES NORMAUX, CAMIONS ET CONVOIS ---

    public synchronized void entrerPont(Vehicule v, int nbSeg, Sens sens, boolean isConvoi) throws InterruptedException {
        if (isConvoi) {
            // Bloque immédiatement l'entrée du sens opposé
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

        // Réservation simultanée des segments d'entrée (1, 2 ou 4)
        for (int i = 0; i < nbSeg; i++) {
            int idx = (sens == Sens.GAUCHE_DROITE) ? i : (nbSegment - 1 - i);
            segments[idx].occuper(v, sens);
        }

        if (sens == Sens.GAUCHE_DROITE) vehiculesGD++;
        else vehiculesDG++;

        AffichageConsole.afficherMessage(v.getIdVehicule() + " entre sur le pont (" + sens + ").");
        AffichageConsole.rafraichirPont(this);
    }

    private boolean peutEntrer(int nbSeg, Sens sens, boolean isConvoi) {
        // Interdit d'entrer si le pont n'est pas FERMÉ ou si une urgence est en cours
        if (etat != EtatPont.FERME || prioritairesActifs > 0) return false;

        // Si un bateau attend et que le délai d'ouverture est autorisé, on bloque les entrées
        if (!fileBateaux.isEmpty() && controleur != null && controleur.isOuvertureAutorisee()) return false;

        // Si un convoi est déjà sur le pont dans le sens opposé, ou attend en face
        if (sens == Sens.GAUCHE_DROITE && convoisEnAttenteDG > 0) return false;
        if (sens == Sens.DROITE_GAUCHE && convoisEnAttenteGD > 0) return false;
        if (convoisSurPont > 0) {
            // Un convoi ne tolère aucun véhicule en sens inverse
            if (sens == Sens.GAUCHE_DROITE && vehiculesDG > 0) return false;
            if (sens == Sens.DROITE_GAUCHE && vehiculesGD > 0) return false;
        }

        // Si CE véhicule est un convoi, il attend que la voie opposée soit totalement vide
        if (isConvoi) {
            if (sens == Sens.GAUCHE_DROITE && vehiculesDG > 0) return false;
            if (sens == Sens.DROITE_GAUCHE && vehiculesGD > 0) return false;
        }

        // Vérifie que tous les segments nécessaires à l'entrée sont libres simultanément
        for (int i = 0; i < nbSeg; i++) {
            int idx = (sens == Sens.GAUCHE_DROITE) ? i : (nbSegment - 1 - i);
            if (!segments[idx].isLibre(sens)) return false;
        }
        return true;
    }

    public synchronized void avancerSegment(Vehicule v, int prochainIdx, int ancienIdx, Sens sens) throws InterruptedException {
        // Si un véhicule prioritaire est actif ou si le segment suivant est occupé, on attend (bande d'urgence si prioritaire)
        while (prioritairesActifs > 0 || !segments[prochainIdx].isLibre(sens)) {
            wait();
        }
        // Réserve le segment suivant PUIS libère l'ancien
        segments[prochainIdx].occuper(v, sens);
        segments[ancienIdx].sortir(v, sens);

        AffichageConsole.rafraichirPont(this);
        notifyAll();
    }

    public synchronized void sortirPont(Vehicule v, int nbSeg, Sens sens, boolean isConvoi) throws InterruptedException {
        while (prioritairesActifs > 0) {
            wait();
        }
        // Libère les derniers segments occupés à la sortie du pont
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

    // --- 2. GESTION DES VÉHICULES PRIORITAIRES (Bande d'urgence) ---

    public synchronized void entrerPrioritaire(Vehicule v) throws InterruptedException {
        prioritairesActifs++;
        AffichageConsole.afficherMessage("🚨 URGENCE : " + v.getIdVehicule() + " arrive ! Mise sur bande d'urgence des véhicules.");
        AffichageConsole.rafraichirPont(this);
        notifyAll();

        // Attend uniquement si le pont est ouvert aux bateaux
        while (etat != EtatPont.FERME && etat != EtatPont.OUVERTURE) {
            wait();
        }
        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " traverse le pont à toute vitesse !");
    }

    public synchronized void sortirPrioritaire(Vehicule v) {
        prioritairesActifs--;
        AffichageConsole.afficherMessage("🚨 " + v.getIdVehicule() + " a quitté le pont. Reprise de la circulation.");
        AffichageConsole.rafraichirPont(this);
        notifyAll(); // Libère les véhicules rangés sur la bande d'urgence
    }

    // --- 3. GESTION DES BATEAUX (File FIFO & États du pont) ---

    public synchronized void demanderPassageBateau(Bateau b) throws InterruptedException {
        fileBateaux.add(b);
        AffichageConsole.afficherMessage("⛵ " + b.getNomBateau() + " rejoint la file FIFO (Position " + fileBateaux.size() + ").");
        AffichageConsole.rafraichirPont(this);
        notifyAll();

        // Attend d'être en tête de la file FIFO et qu'aucun autre bateau ne soit au milieu du pont
        while (fileBateaux.peek() != b || bateauEnCoursDePassage) {
            wait();
        }

        // Si le pont est FERMÉ, le bateau en tête de file déclenche la séquence d'ouverture
        if (etat == EtatPont.FERME) {
            while (controleur != null && !controleur.isOuvertureAutorisee()) {
                wait();
            }
            etat = EtatPont.OUVERTURE;
            AffichageConsole.afficherMessage("🌉 Pont en cours d'OUVERTURE (attente de la sortie des véhicules engagés)...");
            AffichageConsole.rafraichirPont(this);

            // Attend que tous les véhicules terrestres et prioritaires aient quitté le pont
            while (vehiculesGD > 0 || vehiculesDG > 0 || prioritairesActifs > 0) {
                wait();
            }

            etat = EtatPont.OUVERT;
            AffichageConsole.afficherMessage("🌉 Le pont est OUVERT aux bateaux !");
            AffichageConsole.rafraichirPont(this);
        }

        bateauEnCoursDePassage = true;
    }

    public synchronized void terminerPassageBateau(Bateau b) {
        fileBateaux.poll(); // Retire le bateau de la file FIFO
        bateauEnCoursDePassage = false;
        AffichageConsole.afficherMessage("⛵ " + b.getNomBateau() + " a terminé sa traversée.");

        // Si la file FIFO des bateaux est vide, on referme le pont
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

    public synchronized void notifierDelaiEcoule() {
        notifyAll();
    }
}