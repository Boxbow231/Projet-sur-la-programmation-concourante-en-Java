package ressource;

import controleur.Pont;
import controleur.Sens;

public class Voiture extends Vehicule {
    /**
     * Construit une voiture, qui occupe un seul segment du pont.
     *
     * @param idVehicule l'identifiant de la voiture
     * @param pont le pont à traverser
     * @param sens le sens de circulation
     */
    public Voiture(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 1, false);
    }
}
