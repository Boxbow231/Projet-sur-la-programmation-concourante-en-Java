package ressource;

public class VehiculePrioritaire extends Voiture {
    private String typeService; // Police, Pompier, SAMU
    private boolean sireneActive;

    public VehiculePrioritaire(String immatriculation, double poidsTonnes, int vitesseMax, int nombrePortes, String typeService) {
        super(immatriculation, poidsTonnes, vitesseMax, nombrePortes);
        this.typeService = typeService;
        this.sireneActive = false;
    }

    public void activerSirene() {
        this.sireneActive = true;
        System.out.println("PIN PON PIN PON ! (" + typeService + ")");
    }

    @Override
    public void seDeplacer() {
        System.out.println("Le véhicule de " + typeService + " roule à toute vitesse !");
    }
}