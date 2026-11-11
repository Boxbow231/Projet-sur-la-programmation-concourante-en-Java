package thread;

import controleur.Pont;
import controleur.Segment;
import controleur.Sens;
import ressource.Vehicule;

import java.util.LinkedList;

/**
 * Gère l'affichage dynamique de la simulation et des événements dans le terminal.
 */
public class AffichageConsole {
    private static final LinkedList<String> logs = new LinkedList<>();
    private static final int MAX_LOGS = 8;

    /**
     * Enregistre un message dans l'historique des événements récents.
     */
    public static synchronized void afficherMessage(String message) {
        logs.addLast(message);
        if (logs.size() > MAX_LOGS) {
            logs.removeFirst();
        }
    }

    /**
     * Rafraîchit l'affichage complet du pont et de son état dans la console.
     */
    public static synchronized void rafraichirPont(Pont pont) {
        System.out.print("\033[2J\033[H");

        System.out.println("==================================================================");
        System.out.println(" ÉTAT DU PONT : " + pont.getEtat()
                + " | Urgences actives : " + pont.getPrioritairesActifs()
                + " | Bateaux en file FIFO : " + pont.getNbBateauxEnAttente());
        System.out.println("==================================================================");

        Segment[] segments = pont.getSegments();

        System.out.print("Voie G->D : ");
        for (Segment s : segments) {
            Vehicule v = s.getOccupant(Sens.GAUCHE_DROITE);
            System.out.printf("[%-6s]", (v != null ? v.getIdVehicule() : "......"));
        }
        System.out.println();

        System.out.print("Voie D->G : ");
        for (Segment s : segments) {
            Vehicule v = s.getOccupant(Sens.DROITE_GAUCHE);
            System.out.printf("[%-6s]", (v != null ? v.getIdVehicule() : "......"));
        }
        System.out.println("\n==================================================================");
        System.out.println("--- Derniers événements ---");
        for (String log : logs) {
            System.out.println(" > " + log);
        }
        System.out.flush();
    }
}