package controleur;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Représente un segment individuel du pont mobile.
 * Chaque segment est une ressource partagée.
 */
public class Segment{
    private final int id;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition estLibre = lock.newCondition();

    private final Voiture  occupant = null;
    public Segment(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    /**
     * Vérifie si le segment est libre sans bloquer.
     */
    public boolean isLibre(){
        lock.lock();
        try{
            return occupant == null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Tente de réserver le segment sans mise en attente (non bloquant).
     * Très utile pour les camions (2 segments) et convois (4 segments)
     * afin de réserver plusieurs segments de manière atomique sans interblocage.
     *
     * @param v Le véhicule demandeur
     * @return true si la réservation a réussi, false sinon
     */
    public boolean tryOccuper(Vehicule v) {
        lock.lock();
        try {
            if (occupant == null) {
                occupant = v;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Bloque le thread appelant jusqu'à ce que le segment devienne disponible,
     * puis l'occupe pour le véhicule donné.
     *
     * @param v Le véhicule demandeur
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public void entrer(Vehicule v) throws InterruptedException {
        lock.lock();
        try {
            while (occupant != null) {
                estLibre.await();
            }
            occupant = v;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Libère le segment lorsqu'un véhicule l'a complètement quitté.
     * Réveille les threads en attente de ce segment.
     *
     * @param v Le véhicule qui quitte le segment
     */
    public void sortir(Vehicule v) {
        lock.lock();
        try {
            if (occupant == v) {
                occupant = null;
                estLibre.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retourne le véhicule occupant actuellement le segment (pour l'affichage ANSI).
     */
    public Vehicule getOccupant() {
        lock.lock();
        try {
            return occupant;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return "[S" + id + ": " + (occupant == null ? "LIBRE" : occupant.getIdVehicule()) + "]";
        } finally {
            lock.unlock();
        }
    }

}
