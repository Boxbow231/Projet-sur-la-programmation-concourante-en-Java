package ressource;

import controleur.Pont;
import controleur.Sens;

/**
 * Représente une voiture standard occupant un seul segment à la fois.
 */
public class Voiture extends Vehicule {
    /**
     * Initialise une voiture nécessitant 1 segment.
     */
    public Voiture(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 1, false);
    }
}