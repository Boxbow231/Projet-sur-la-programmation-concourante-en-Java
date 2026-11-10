package ressource;

import controleur.Pont;
import controleur.Sens;

public class VehiculePrioritaire extends Vehicule {
    private final String typeService;

    public VehiculePrioritaire(String idVehicule, Pont pont, Sens sens, String typeService) {
        super(idVehicule, pont, sens, 1, false);
        this.typeService = typeService;
    }

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