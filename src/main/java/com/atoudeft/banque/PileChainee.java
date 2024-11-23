package com.atoudeft.banque;

import java.io.Serializable;

public class PileChainee implements Serializable {
    private Noeud sommet; 		//référence l'élément au sommet de la file

    private int nbElement; 	//Nombre d'éléments dans la pile (utilisé pour
    //ne pas avoir à recalculer le nombre d'éléments dans
    //la méthode taille()). Ne pas oublier de l'incrémenter
    //lorsqu'on empile et le décrémenter lorsqu'on dépile.

    /**
     * Constructeur sans paramètre
     * Initialise la référence premier à null et nbElement à 0.
     */
    public PileChainee(){
        this.sommet = null;
        this.nbElement = 0;
    }

    /**
     * Crée une pile de la même façon que le premier constructeur.
     * Ce constructeur est laissé là juste pour que les programmes déjà écrits
     * avec le version statique de la pile continuent de fonctionner.
     * @param taille La taille voulue pour la file.
     */
    public PileChainee(int taille){
        this.sommet = null;
        this.nbElement = 0;
    }

    /**
     * Ajoute un élément au sommet de la pile.
     *
     * @param element l'élément à empiler.
     * @return true si l'opération réussit et false sinon (ce qui n'arrive jamais)
     */
    public boolean empiler(Object element) {
        Noeud nouveau = new Noeud(sommet,element);
        this.sommet = nouveau;
        this.nbElement++;
        return true;
    }

    /**
     * Retire l'élément au sommet de la pile.
     *
     * @return L'élément au sommet de la pile s'il existe ou null sinon.
     */
    public Object depiler(){

        if (estVide())
            return null;

        Object objRetire = this.sommet.donnee;
        this.sommet = this.sommet.suivant;
        this.nbElement--;
        return objRetire;
    }

    /**
     * Indique si la pile est vide.
     *
     * @return true si la  pile est vide et false sinon.
     */
    public boolean estVide(){

        return nbElement==0; //ou: return this.sommet==null;
    }

    /**
     * Vide la pile.
     */
    public void vider(){
        while (!estVide())
            depiler();
    }

    /**
     * Permet de consulter l'élément au sommet de la pile sans l'enlever.
     *
     * @return L'élément au sommet si la pile n'est pas vide et null sinon.
     */
    public Object peek(){
        if (this.sommet==null) //ou : if (this.nbElement==0)
            return null;
        return this.sommet.donnee;
    }


    /**
     * Retourne le nombre d'éléments dans la pile.
     *
     * @return Le nombre d'éléments actuellement dans la pile.
     */
    public int taille(){

        return nbElement;
    }

    private class Noeud implements Serializable {
        public Object donnee;
        public Noeud suivant;

        public Noeud(Noeud next, Object data) {
            this.donnee = data;
            this.suivant = next;
        }
    }
}
