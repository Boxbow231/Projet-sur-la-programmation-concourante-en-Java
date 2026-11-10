package ressource;

import controleur.Pont;
import controleur.Sens;

public class VehiculePrioritaire extends Vehicule {
    private final String typeService;

    /**
     * Construit un véhicule prioritaire (ambulance, pompiers...).
     *
     * @param idVehicule l'identifiant du véhicule
     * @param pont le pont à traverser
     * @param sens le sens de circulation
     * @param typeService le type de service (ex. "Ambulance")
     */
    public VehiculePrioritaire(String idVehicule, Pont pont, Sens sens, String typeService) {
        super(idVehicule, pont, sens, 1, false);
        this.typeService = typeService;
    }

    /**
     * Exécute la traversée prioritaire : déclenche la bande d'urgence,
     * traverse le pont puis rend la circulation aux autres véhicules.
     */
    @Override
    public void run() {
        try {
            pont.entrerPrioritaire(this, sens);
            Thread.sleep(1500);
            pont.sortirPrioritaire(this, sens);
        } catch (InterruptedException e) {
            System.err.println(idVehicule + " (" + typeService + ") interrompu.");
        }
    }
}