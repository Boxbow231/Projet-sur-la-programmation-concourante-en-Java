package ressource;

import controleur.Pont;
import controleur.Sens;

public class ConvoiExceptionnel extends Vehicule {
    /**
     * Construit un convoi exceptionnel, qui occupe quatre segments consécutifs
     * et interdit la présence de véhicules en sens inverse.
     *
     * @param idVehicule l'identifiant du convoi
     * @param pont le pont à traverser
     * @param sens le sens de circulation
     */
    public ConvoiExceptionnel(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 4, true);
    }
}
