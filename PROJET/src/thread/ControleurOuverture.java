package thread;

import controleur.Pont;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contrôle le délai réglementaire obligatoire entre deux ouvertures successives du pont.
 */
public class ControleurOuverture {
    private final Pont pont;
    private boolean ouvertureAutorisee;
    private Timer timer;
    private final long DELAI_ENTRE_OUVERTURES = 60000;

    /**
     * Initialise le contrôleur avec un délai initial autorisé.
     */
    public ControleurOuverture(Pont pont) {
        this.pont = pont;
        this.ouvertureAutorisee = true;
    }

    /**
     * Indique si une nouvelle ouverture du pont est permise.
     */
    public synchronized boolean isOuvertureAutorisee() {
        return ouvertureAutorisee;
    }

    /**
     * Déclenche le minuteur dès la fermeture complète du pont.
     */
    public synchronized void notifierFermetureComplete() {
        this.ouvertureAutorisee = false;
        AffichageConsole.afficherMessage("Contrôleur : Pont fermé. Prochaine ouverture possible dans 1 minute.");
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoriserOuverture();
            }
        }, DELAI_ENTRE_OUVERTURES);
    }

    /**
     * Réautorise les ouvertures une fois le délai écoulé.
     */
    private synchronized void autoriserOuverture() {
        this.ouvertureAutorisee = true;
        AffichageConsole.afficherMessage("Contrôleur : Délai d'attente terminé. Nouvelles ouvertures autorisées.");
        pont.notifierDelaiEcoule();
    }
}