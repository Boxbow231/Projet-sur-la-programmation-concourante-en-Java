package ressource;

import controleur.Pont;
import controleur.Sens;

public class ConvoiExceptionnel extends Vehicule {
    public ConvoiExceptionnel(String idVehicule, Pont pont, Sens sens) {
        super(idVehicule, pont, sens, 4, true);
    }
}