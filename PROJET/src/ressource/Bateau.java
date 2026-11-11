package ressource;

import controleur.Pont;
import controleur.Sens;
import thread.AffichageConsole;

/**
 * Représente un bateau naviguant sous le pont et géré par une file FIFO.
 */
public class Bateau extends Thread {
    private final String nom;
    private final Pont pont;
    private final Sens sens;

    /**
     * Crée un nouveau bateau avec sa direction maritime.
     */
    public Bateau(String nom, Pont pont, Sens sens) {
        this.nom = nom;
        this.pont = pont;
        this.sens = sens;
    }

    /**
     * Retourne l'identifiant formaté du bateau.
     */
    public String getNomBateau() {
        return nom + "(" + sens + ")";
    }

    /**
     * Exécute le cycle de traversée du bateau sous le pont.
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