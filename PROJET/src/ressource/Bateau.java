package ressource;

import controleur.Pont;
import controleur.Sens;
import thread.AffichageConsole;

public class Bateau extends Thread {
    private final String nom;
    private final Pont pont;
    private final Sens sens;

    /**
     * Construit un bateau qui passera sous le pont.
     *
     * @param nom le nom du bateau
     * @param pont le pont sous lequel le bateau passe
     * @param sens le sens de navigation (NORD_SUD ou SUD_NORD)
     */
    public Bateau(String nom, Pont pont, Sens sens) {
        this.nom = nom;
        this.pont = pont;
        this.sens = sens;
    }

    /**
     * Retourne le nom du bateau suivi de son sens de navigation.
     *
     * @return le nom d'affichage du bateau
     */
    public String getNomBateau() {
        return nom + "(" + sens + ")";
    }

    /**
     * Exécute le passage du bateau : demande de passage dans la file FIFO,
     * navigation sous le pont, puis fin de traversée.
     */
    @Override
    public void run() {
        try {
            pont.demanderPassageBateau(this);
            AffichageConsole.afficherMessage("⛵ " + getNomBateau() + " navigue sous le pont...");
            Thread.sleep(2000);
            pont.terminerPassageBateau(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
