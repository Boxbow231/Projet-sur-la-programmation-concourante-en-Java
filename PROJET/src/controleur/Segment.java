package controleur;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import ressource.Vehicule;

/**
 * Représente un segment individuel du pont mobile.
 * Chaque segment est une ressource partagée.
 */
public class Segment {
    private final int id;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition estLibre = lock.newCondition();

    private Vehicule occupant = null;

    public Segment(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isLibre() {
        lock.lock();
        try {
            return occupant == null;
        } finally {
            lock.unlock();
        }
    }

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
            return "[S" + id + ": " + (occupant == null ? "LIBRE" : occupant.toString()) + "]";
        } finally {
            lock.unlock();
        }
    }
}