package controleur;

import thread.*;
import ressource.*;

/**
 * Point d'entrée de l'application. Initialise le pont et lance un scénario de test
 * impliquant tous les types de véhicules et de bateaux.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Pont pont = new Pont(6);
        ControleurOuverture controleur = new ControleurOuverture(pont);
        pont.setControleur(controleur);

        AffichageConsole.rafraichirPont(pont);

        new Voiture("V1", pont, Sens.GAUCHE_DROITE).start();
        new Voiture("V2", pont, Sens.DROITE_GAUCHE).start();
        Thread.sleep(1500);
        new Camion("C1", pont, Sens.GAUCHE_DROITE).start();

        Thread.sleep(2000);
        new ConvoiExceptionnel("CE1", pont, Sens.DROITE_GAUCHE).start();

        Thread.sleep(2500);
        new VehiculePrioritaire("AMB1", pont, Sens.GAUCHE_DROITE, "Ambulance").start();

        Thread.sleep(3000);
        new Bateau("Bateau-Nord", pont, Sens.NORD_SUD).start();
        new Bateau("Bateau-Sud", pont, Sens.SUD_NORD).start();

        Thread.sleep(4000);
        new Voiture("V3", pont, Sens.GAUCHE_DROITE).start();
    }
}