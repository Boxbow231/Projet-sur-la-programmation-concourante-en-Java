package thread;

import controleur.Pont;
import java.util.Timer;
import java.util.TimerTask;

public class ControleurOuverture {
    private final Pont pont;
    private boolean ouvertureAutorisee;
    private Timer timer;
    private final long DELAI_ENTRE_OUVERTURES = 60000; 

    public ControleurOuverture(Pont pont) {
        this.pont = pont;
        this.ouvertureAutorisee = true; 
    }

    public synchronized boolean isOuvertureAutorisee() {
        return ouvertureAutorisee;
    }

     
    public synchronized void notifierFermetureComplete() {
        this.ouvertureAutorisee = false;
        AffichageConsole.afficherMessage("Contrôleur : Pont fermé. Prochaine ouverture possible dans 1 minute.");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoriserOuverture();
            }
        }, DELAI_ENTRE_OUVERTURES);
    }

    private synchronized void autoriserOuverture() {
        this.ouvertureAutorisee = true;
        AffichageConsole.afficherMessage("Contrôleur : Délai d'attente terminé. Nouvelles ouvertures autorisées.");
                pont.notifierDelaiEcoule();
    }
}