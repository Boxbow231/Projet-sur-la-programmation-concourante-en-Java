package ressource;

import controleur.Pont;
import controleur.Sens;

public abstract class Vehicule extends Thread {
    protected String idVehicule;
    protected Pont pont;
    protected Sens sens;
    protected int nbSegmentsNecessaires;
    protected boolean isConvoi;

    /**
     * Construit un véhicule terrestre.
     *
     * @param idVehicule l'identifiant du véhicule
     * @param pont le pont à traverser
     * @param sens le sens de circulation
     * @param nbSegmentsNecessaires le nombre de segments occupés par le véhicule
     * @param isConvoi true si le véhicule est un convoi exceptionnel
     */
    public Vehicule(String idVehicule, Pont pont, Sens sens, int nbSegmentsNecessaires, boolean isConvoi) {
        this.idVehicule = idVehicule;
        this.pont = pont;
        this.sens = sens;
        this.nbSegmentsNecessaires = nbSegmentsNecessaires;
        this.isConvoi = isConvoi;
    }

    /**
     * Retourne l'identifiant du véhicule.
     *
     * @return l'identifiant du véhicule
     */
    public String getIdVehicule() {
        return idVehicule;
    }

    /**
     * Exécute la traversée du pont : entrée, progression segment par segment, puis sortie.
     */
    @Override
    public void run() {
        try {
            int N = pont.getNbSegment();

            pont.entrerPont(this, nbSegmentsNecessaires, sens, isConvoi);

            int pasRestants = N - nbSegmentsNecessaires;
            int tete = (sens == Sens.GAUCHE_DROITE) ? (nbSegmentsNecessaires - 1) : (N - nbSegmentsNecessaires);

            for (int pas = 0; pas < pasRestants; pas++) {
                Thread.sleep(800);
                int prochaineTete = (sens == Sens.GAUCHE_DROITE) ? (tete + 1) : (tete - 1);
                int ancienneQueue = (sens == Sens.GAUCHE_DROITE) ? (tete - nbSegmentsNecessaires + 1) : (tete + nbSegmentsNecessaires - 1);

                pont.avancerSegment(this, prochaineTete, ancienneQueue, sens);
                tete = prochaineTete;
            }

            Thread.sleep(800);
            pont.sortirPont(this, nbSegmentsNecessaires, sens, isConvoi);

        } catch (InterruptedException e) {
            System.err.println(idVehicule + " a été interrompu.");
        }
    }
}
