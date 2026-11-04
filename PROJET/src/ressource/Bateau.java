package ressource;

public class Bateau extends Vehicule {
    private String nomNavire;

    public Bateau(String immatriculation, double poidsTonnes, int vitesseMax, String nomNavire) {
        super(immatriculation, poidsTonnes, vitesseMax);
        this.nomNavire = nomNavire;
    }

    public String getNomNavire() { return nomNavire; }

    @Override
    public void seDeplacer() {
        System.out.println("Le bateau " + nomNavire + " navigue sur l'eau.");
    }
}