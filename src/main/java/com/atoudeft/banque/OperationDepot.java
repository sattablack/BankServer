package com.atoudeft.banque;

public class OperationDepot extends Operation {
    private double montant;

    public OperationDepot(double montant) {
        super(TypeOperation.DEPOT);
        this.montant = montant;


    }

    @Override
    public String toString() {
        return "" + super.getDate() +
                "    " + super.getType() +
                "    " + this.montant;
    }
}
