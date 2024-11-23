package com.atoudeft.banque;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.atoudeft.banque.TypeCompte.CHEQUE;
import static com.atoudeft.banque.TypeCompte.EPARGNE;

public class CompteClient implements Serializable {
    private String numero;
    private String nip;
    private List<CompteBancaire> comptes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompteClient that = (CompteClient) o;
        return Objects.equals(numero, that.numero) || Objects.equals(nip, that.nip) || Objects.equals(comptes, that.comptes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, nip, comptes);
    }

    /**
     * Crée un compte-client avec un numéro et un nip.
     *
     * @param numero le numéro du compte-client
     * @param nip le nip
     */
    public CompteClient(String numero, String nip) {
        this.numero = numero;
        this.nip = nip;
        comptes = new ArrayList<>();
    }

    public String getNumero() {
        return numero;
    }
    public String getNip() {
        return nip;
    }
    public CompteCheque getCompteCheque() {
        for(CompteBancaire ch:this.comptes){
            if(ch.getType() == CHEQUE ){
                return (CompteCheque) ch;
                }
            }
        return null;
    }

    public CompteEpargne getCompteEpargne() {
        for(CompteBancaire ch:this.comptes){
            if(ch.getType() == EPARGNE ){
                return (CompteEpargne) ch;
            }
        }
        return null;
    }
    /**
     * Ajoute un compte bancaire au compte-client.
     *
     * @param compte le compte bancaire
     * @return true si l'ajout est réussi
     */
    public boolean ajouter(CompteBancaire compte) {
        return this.comptes.add(compte);
    }
}