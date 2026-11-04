package ressource;

public abstract class Vehicule {
    protected String immatriculation;
    protected double poidsTonnes;
    protected int vitesseMax;

    public Vehicule(String immatriculation, double poidsTonnes, int vitesseMax) {
        this.immatriculation = immatriculation;
        this.poidsTonnes = poidsTonnes;
        this.vitesseMax = vitesseMax;
    }

    public String getImmatriculation() { return immatriculation; }
    public double getPoidsTonnes() { return poidsTonnes; }
    public int getVitesseMax() { return vitesseMax; }

    public abstract void seDeplacer();
}