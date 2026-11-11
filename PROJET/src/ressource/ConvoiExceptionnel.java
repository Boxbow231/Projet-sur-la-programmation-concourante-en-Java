package ressource;

import controleur.Pont;
import controleur.Sens;

/**
 * Représente un convoi exceptionnel occupant quatre segments et bloquant la voie opposée.
 */
public class ConvoiExceptionnel extends Vehicule {
    /**
     * Initialise un convoi exceptionnel nécessitant 4 segments.
     */
    public ConvoiExceptionnel(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 4, true);
    }
}