package ressource;

import controleur.Pont;
import controleur.Sens;

public class Voiture extends Vehicule {
    public Voiture(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 1, false);
    }
}