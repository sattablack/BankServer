package com.atoudeft.banque;

public class OperationTransfer extends Operation {
        private double montant;

        private String numeroCompteDestinataire;

    public OperationTransfer(double montant, String numeroCompteDestinataire) {
        super(TypeOperation.TRANSFER);
        this.montant = montant;
        this.numeroCompteDestinataire = numeroCompteDestinataire;
    }

    @Override
    public String toString() {
        return "" + super.getDate() +
                "    " + super.getType() +
                "    " + this.montant +
                "    " + this.numeroCompteDestinataire;
    }
}
