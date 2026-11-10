package ressource;

import controleur.Pont;
import controleur.Sens;
import thread.AffichageConsole;

public class Bateau extends Thread {
    private final String nom;
    private final Pont pont;
    private final Sens sens;

    public Bateau(String nom, Pont pont, Sens sens) {
        this.nom = nom;
        this.pont = pont;
        this.sens = sens;
    }

    public String getNomBateau() {
        return nom + "(" + sens + ")";
    }

    @Override
    public void run() {
        try {
            pont.demanderPassageBateau(this);
            AffichageConsole.afficherMessage("⛵ " + getNomBateau() + " navigue sous le pont...");
            Thread.sleep(2000); // Temps de passage du bateau
            pont.terminerPassageBateau(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}