package com.atoudeft.banque;

import com.atoudeft.banque.io.EntreesSorties;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;

import static com.atoudeft.banque.TypeCompte.CHEQUE;
import static com.atoudeft.banque.TypeCompte.EPARGNE;


public class CompteCheque extends CompteBancaire {
    private CompteBancaire cptTransfer;

    public CompteCheque(String numero){
        super(numero, CHEQUE);
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
            getHistorique().empiler(opDepot.toString());
            return true;
        }
      return false;
    }

    public boolean debiter(double montant){
        if(montant > 0 && solde >= montant){
            solde = solde - montant;
            OperationRetrait opRetrait = new OperationRetrait(montant);
            getHistorique().empiler(opRetrait.toString());
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

    @Override
    public double getSolde() {
        return super.getSolde();
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
}
