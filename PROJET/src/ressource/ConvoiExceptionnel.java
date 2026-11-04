package ressource;

public class ConvoiExceptionnel extends Camion {
    private double longueurMetres;

    public ConvoiExceptionnel(String immatriculation, double poidsTonnes, int vitesseMax, double capaciteChargeTonnes, double longueurMetres) {
        super(immatriculation, poidsTonnes, vitesseMax, capaciteChargeTonnes);
        this.longueurMetres = longueurMetres;
    }

    public double getLongueurMetres() { return longueurMetres; }

    @Override
    public void seDeplacer() {
        System.out.println("Le convoi exceptionnel avance lentement avec ses escortes.");
    }
}