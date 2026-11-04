package ressource;

public class Voiture extends Vehicule {
    private int nombrePortes;

    public Voiture(String immatriculation, double poidsTonnes, int vitesseMax, int nombrePortes) {
        super(immatriculation, poidsTonnes, vitesseMax);
        this.nombrePortes = nombrePortes;
    }

    public int getNombrePortes() { return nombrePortes; }

    @Override
    public void seDeplacer() {
        System.out.println("La voiture roule sur la route.");
    }
}