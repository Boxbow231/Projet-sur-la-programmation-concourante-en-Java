package controleur;

import thread.*;
import ressource.*;

public class Main {
    /**
     * Point d'entrée de la simulation : crée le pont et son contrôleur,
     * puis lance les véhicules et les bateaux à intervalles échelonnés.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     * @throws InterruptedException si le thread principal est interrompu pendant une pause
     */
    public static void main(String[] args) throws InterruptedException {
        Pont pont = new Pont(6);
        ControleurOuverture controleur = new ControleurOuverture(pont);
        pont.setControleur(controleur);

        AffichageConsole.rafraichirPont(pont);

        new Voiture("V1", pont, Sens.GAUCHE_DROITE).start();
        new Voiture("V2", pont, Sens.DROITE_GAUCHE).start();
        Thread.sleep(500);
        new Camion("C1", pont, Sens.GAUCHE_DROITE).start();

        Thread.sleep(1000);
        new ConvoiExceptionnel("CE1", pont, Sens.DROITE_GAUCHE).start();

        Thread.sleep(1500);
        new VehiculePrioritaire("AMB1", pont, Sens.GAUCHE_DROITE, "Ambulance").start();

        Thread.sleep(2000);
        new Bateau("Bateau-Nord", pont, Sens.NORD_SUD).start();
        new Bateau("Bateau-Sud", pont, Sens.SUD_NORD).start();

        Thread.sleep(3000);
        new Voiture("V3", pont, Sens.GAUCHE_DROITE).start();
    }
}
