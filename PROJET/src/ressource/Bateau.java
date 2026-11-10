package thread;
import controleur.Pont;

public class Bateau extends Thread {
    private String nom;
    private Pont pont;

    public Bateau(String nom, Pont pont) {
        this.nom = nom;
        this.pont = pont;
    }

    @Override
    public void run() {
        try {
            AffichageConsole.afficherMessage(nom + " arrive et demande l'ouverture du pont.");
            pont.demanderOuverture(); // Bloque jusqu'à ce que le pont soit vide
            
            AffichageConsole.afficherMessage(nom + " traverse le pont (Pont OUVERT).");
            Thread.sleep(3000); // Temps de traversée du bateau
            
            pont.terminerOuverture(); // Referme le pont et libère les véhicules
            AffichageConsole.afficherMessage(nom + " est passé, le pont se referme.");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}