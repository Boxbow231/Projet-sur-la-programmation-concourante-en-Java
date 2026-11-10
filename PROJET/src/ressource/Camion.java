package ressource;

import controleur.Pont;
import controleur.Sens;

public class Camion extends Vehicule {
    public Camion(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 2, false);
    }
}