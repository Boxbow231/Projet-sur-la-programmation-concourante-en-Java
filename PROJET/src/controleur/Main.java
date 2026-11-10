package controleur;

import thread.*;
import ressource.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Création d'un pont de 6 segments
        Pont pont = new Pont(6);
        ControleurOuverture controleur = new ControleurOuverture(pont);
        pont.setControleur(controleur);

        AffichageConsole.rafraichirPont(pont);

        // Lancement de voitures et camions dans les deux sens
        new Voiture("V1", pont, Sens.GAUCHE_DROITE).start();
        new Voiture("V2", pont, Sens.DROITE_GAUCHE).start();
        Thread.sleep(500);
        new Camion("C1", pont, Sens.GAUCHE_DROITE).start();

        // Arrivée d'un convoi exceptionnel (bloque le sens opposé)
        Thread.sleep(1000);
        new ConvoiExceptionnel("CE1", pont, Sens.DROITE_GAUCHE).start();

        // Arrivée d'une ambulance (met tout le monde sur la bande d'urgence)
        Thread.sleep(1500);
        new VehiculePrioritaire("AMB1", pont, Sens.GAUCHE_DROITE, "Ambulance").start();

        // Arrivée de deux bateaux (File FIFO)
        Thread.sleep(2000);
        new Bateau("Bateau-Nord", pont, Sens.NORD_SUD).start();
        new Bateau("Bateau-Sud", pont, Sens.SUD_NORD).start();

        // Nouvelles voitures qui attendront la fermeture du pont
        Thread.sleep(3000);
        new Voiture("V3", pont, Sens.GAUCHE_DROITE).start();
    }
}