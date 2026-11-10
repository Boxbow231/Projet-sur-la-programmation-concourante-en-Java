package thread;

import controleur.Pont;
import controleur.Sens;

public abstract class Vehicule extends Thread {
    protected String idVehicule;
    protected Pont pont;
    protected Sens sens;
    protected int nbSegmentsNecessaires;

    public Vehicule(String idVehicule, Pont pont, Sens sens, int nbSegmentsNecessaires) {
        this.idVehicule = idVehicule;
        this.pont = pont;
        this.sens = sens;
        this.nbSegmentsNecessaires = nbSegmentsNecessaires;
    }

    public String getIdVehicule() {
        return idVehicule;
    }

    @Override
    public void run() {
        try {
            int positionEntree = (sens == Sens.GAUCHE_DROITE) ? 0 : pont.getNbSegment() - 1;
            int positionSortie = (sens == Sens.GAUCHE_DROITE) ? pont.getNbSegment() : -1;
            int direction = (sens == Sens.GAUCHE_DROITE) ? 1 : -1;
            AffichageConsole.afficherMessage(idVehicule + " attend pour entrer.");
            pont.reserverSegments(positionEntree, nbSegmentsNecessaires, sens, this);
            pont.entrerPont();
            AffichageConsole.afficherMessage(idVehicule + " est entré sur le pont.");

            int positionCourante = positionEntree;

            for (int i = 1; i < pont.getNbSegment(); i++) {
                Thread.sleep(1000); // Temps de trajet pour un segment

                int nouvellePosition = positionCourante + direction;
                pont.libererSegments(positionCourante, nbSegmentsNecessaires, sens, this);
                pont.reserverSegments(nouvellePosition, nbSegmentsNecessaires, sens, this);
                
                positionCourante = nouvellePosition;
                AffichageConsole.afficherMessage(idVehicule + " avance en position " + positionCourante);
            }

            Thread.sleep(1000);
            pont.libererSegments(positionCourante, nbSegmentsNecessaires, sens, this);
            pont.sortirPont();
            AffichageConsole.afficherMessage(idVehicule + " a quitté le pont.");

        } catch (InterruptedException e) {
            System.err.println(idVehicule + " a été interrompu.");
        }
    }
}