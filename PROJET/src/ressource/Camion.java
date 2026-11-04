package ressource;

public class Camion extends Vehicule {
    protected double capaciteChargeTonnes;

    public Camion(String immatriculation, double poidsTonnes, int vitesseMax, double capaciteChargeTonnes) {
        super(immatriculation, poidsTonnes, vitesseMax);
        this.capaciteChargeTonnes = capaciteChargeTonnes;
    }

    public double getCapaciteChargeTonnes() { return capaciteChargeTonnes; }

    @Override
    public void seDeplacer() {
        System.out.println("Le camion transporte sa marchandise sur l'autoroute.");
    }
}