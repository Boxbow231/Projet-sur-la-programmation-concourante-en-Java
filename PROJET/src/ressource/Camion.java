package ressource;

import controleur.Pont;
import controleur.Sens;

/**
 * Représente un camion occupant deux segments consécutifs du pont.
 */
public class Camion extends Vehicule {
    /**
     * Initialise un camion nécessitant 2 segments.
     */
    public Camion(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 2, false);
    }
}