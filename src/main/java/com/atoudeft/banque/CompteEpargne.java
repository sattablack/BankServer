package com.atoudeft.banque;


import static com.atoudeft.banque.TypeCompte.CHEQUE;
import static com.atoudeft.banque.TypeCompte.EPARGNE;

public class CompteEpargne extends CompteBancaire{
    private static double limiteR = 1000;
    private static double fraisFixe = 2;
    private double tauxInt;
    private CompteBancaire cptTransfer;

    public CompteEpargne(String numero, double tauxInt){
        super(numero,EPARGNE);
        this.tauxInt = tauxInt;
        this.cptTransfer = null;
    }

    public CompteBancaire getTransfer(){
        return cptTransfer;
    }
    public void setTransfer(CompteBancaire cptTransfer){
        this.cptTransfer =  cptTransfer;
    }

    public boolean crediter(double montant){
        if(montant > 0){
            solde = solde + montant;
            OperationDepot opDepot = new OperationDepot(montant);
            this.getHistorique().empiler(opDepot.toString());
            return true;
        }
        return false;
    }

    public boolean debiter(double montant){
        if(montant > 0 && solde >= montant){
            if(solde < limiteR){
                solde = solde - montant - fraisFixe;
            }
            else{
                solde = solde - montant;
            }
            OperationRetrait opRetrait = new OperationRetrait(montant);
            this.getHistorique().empiler(opRetrait.toString());
            return true;
        }
        return false;
    }

    public boolean payerFacture(String numeroFacture, double montant, String description){

        if(montant > 0 && solde >= montant){
            solde = solde - montant;
            OperationFacture opFact = new OperationFacture(montant, numeroFacture, description);
            getHistorique().empiler(opFact.toString());
            return true;
        }
        return false;
    }

    public boolean transferer(double montant, String numeroCompteDestinataire){

        if(montant > 0 && solde >= montant){
            solde = solde - montant;
            if(cptTransfer.getType().equals(CHEQUE)){
                CompteCheque cpt = (CompteCheque) cptTransfer;
                cpt.Ajoute(montant);
            } else if (cptTransfer.getType().equals(EPARGNE)) {
                CompteEpargne cpt = (CompteEpargne) cptTransfer;
                cpt.Ajoute(montant);
            }
            OperationTransfer operationTransfer = new OperationTransfer(montant, numeroCompteDestinataire);
            getHistorique().empiler(operationTransfer.toString());
            return true;
        }
        return false;

    }

    public boolean ajouterInterets(double interet){
            if(interet > 0){
                solde = this.solde - interet * this.solde;
                return true;
            }
          return false;
    }
}
