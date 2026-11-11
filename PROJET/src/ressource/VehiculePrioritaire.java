package ressource;

import controleur.Pont;
import controleur.Sens;

/**
 * Représente un véhicule prioritaire (ambulance, pompier) forçant l'arrêt des autres véhicules.
 */
public class VehiculePrioritaire extends Vehicule {
    private final String typeService;

    /**
     * Initialise un véhicule prioritaire.
     */
    public VehiculePrioritaire(String idVehicule, Pont pont, Sens sens, String typeService) {
        super(idVehicule, pont, sens, 1, false);
        this.typeService = typeService;
    }

    /**
     * Exécute la traversée prioritaire en déclenchant la bande d'urgence.
     */
    @Override
    public void run() {
        try {
            pont.entrerPrioritaire(this);
            Thread.sleep(1500);
            pont.sortirPrioritaire(this);
        } catch (InterruptedException e) {
            System.err.println(idVehicule + " (" + typeService + ") interrompu.");
        }
    }
}