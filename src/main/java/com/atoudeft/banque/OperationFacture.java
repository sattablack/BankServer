package com.atoudeft.banque;

public class OperationFacture extends Operation {
         private double montant;
         private String numeroFacture;
         private String description;


            public OperationFacture(double montant, String numeroFacture, String description) {
                super(TypeOperation.FACTURE);
                this.montant = montant;
        this.numeroFacture = numeroFacture;
        this.description = description;
    }

    @Override
    public String toString() {
        return "" + super.getDate() +
                "    " + super.getType() +
                "    " + this.montant +
                "    " + this.numeroFacture +
                "    " + this.description;
    }

}
