package ressource;

import controleur.Pont;
import controleur.Sens;

public class Camion extends Vehicule {
    /**
     * Construit un camion, qui occupe deux segments consécutifs du pont.
     *
     * @param idVehicule l'identifiant du camion
     * @param pont le pont à traverser
     * @param sens le sens de circulation
     */
    public Camion(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 2, false);
    }
}
