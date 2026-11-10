package thread;

import controleur.Pont;
import java.util.Timer;
import java.util.TimerTask;

public class ControleurOuverture {
    private final Pont pont;
    private boolean ouvertureAutorisee;
    private Timer timer;
    private final long DELAI_ENTRE_OUVERTURES = 60000;

    /**
     * Construit le contrôleur d'ouverture du pont. La première ouverture est autorisée d'emblée.
     *
     * @param pont le pont dont le contrôleur gère les ouvertures
     */
    public ControleurOuverture(Pont pont) {
        this.pont = pont;
        this.ouvertureAutorisee = true;
    }

    /**
     * Indique si une ouverture du pont est actuellement autorisée.
     *
     * @return true si le délai minimal entre deux ouvertures est écoulé
     */
    public synchronized boolean isOuvertureAutorisee() {
        return ouvertureAutorisee;
    }

    /**
     * Signale que le pont est complètement refermé : interdit toute ouverture
     * et programme un Timer qui la réautorisera après le délai minimal.
     */
    public synchronized void notifierFermetureComplete() {
        this.ouvertureAutorisee = false;
        AffichageConsole.afficherMessage("Contrôleur : Pont fermé. Prochaine ouverture possible dans 1 minute.");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            /**
             * Tâche du Timer exécutée à la fin du délai : réautorise l'ouverture.
             */
            @Override
            public void run() {
                autoriserOuverture();
            }
        }, DELAI_ENTRE_OUVERTURES);
    }

    /**
     * Réautorise les ouvertures du pont et réveille les threads en attente sur le pont.
     */
    private synchronized void autoriserOuverture() {
        this.ouvertureAutorisee = true;
        AffichageConsole.afficherMessage("Contrôleur : Délai d'attente terminé. Nouvelles ouvertures autorisées.");
        pont.notifierDelaiEcoule();
    }
}
