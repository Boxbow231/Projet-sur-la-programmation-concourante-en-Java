package ressource;

import controleur.Pont;
import controleur.Sens;

public abstract class Vehicule extends Thread {
    protected String idVehicule;
    protected Pont pont;
    protected Sens sens;
    protected int nbSegmentsNecessaires;
    protected boolean isConvoi;

    public Vehicule(String idVehicule, Pont pont, Sens sens, int nbSegmentsNecessaires, boolean isConvoi) {
        this.idVehicule = idVehicule;
        this.pont = pont;
        this.sens = sens;
        this.nbSegmentsNecessaires = nbSegmentsNecessaires;
        this.isConvoi = isConvoi;
    }

    public String getIdVehicule() {
        return idVehicule;
    }

    @Override
    public void run() {
        try {
            int N = pont.getNbSegment();

            // 1. Entrée sur le pont (réserve simultanément les nbSegmentsNecessaires premiers segments)
            pont.entrerPont(this, nbSegmentsNecessaires, sens, isConvoi);

            int pasRestants = N - nbSegmentsNecessaires;
            int tete = (sens == Sens.GAUCHE_DROITE) ? (nbSegmentsNecessaires - 1) : (N - nbSegmentsNecessaires);

            // 2. Progression segment par segment sur le pont
            for (int pas = 0; pas < pasRestants; pas++) {
                Thread.sleep(800);
                int prochaineTete = (sens == Sens.GAUCHE_DROITE) ? (tete + 1) : (tete - 1);
                int ancienneQueue = (sens == Sens.GAUCHE_DROITE) ? (tete - nbSegmentsNecessaires + 1) : (tete + nbSegmentsNecessaires - 1);

                pont.avancerSegment(this, prochaineTete, ancienneQueue, sens);
                tete = prochaineTete;
            }

            // 3. Sortie complète du pont
            Thread.sleep(3500);
            pont.sortirPont(this, nbSegmentsNecessaires, sens, isConvoi);

        } catch (InterruptedException e) {
            System.err.println(idVehicule + " a été interrompu.");
        }
    }
}