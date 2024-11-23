package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

/**
 * Cette classe représente un gestionnaire d'événement d'un serveur. Lorsqu'un serveur reçoit un texte d'un client,
 * il crée un événement à partir du texte reçu et alerte ce gestionnaire qui réagit en gérant l'événement.
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;

    /**
     * Construit un gestionnaire d'événements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire gère des événements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    /**
     * Méthode de gestion d'événements. Cette méthode contiendra le code qui gère les réponses obtenues d'un client.
     *
     * @param evenement L'événement à gérer.
     */
    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        ServeurBanque serveurBanque = (ServeurBanque)serveur;
        Banque banque;
        ConnexionBanque cnx;
        String msg, typeEvenement, argument, numCompteClient, nip;
        String[] t;

        if (source instanceof Connexion) {
            cnx = (ConnexionBanque) source;
            System.out.println("SERVEUR: Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            cnx.setTempsDerniereOperation(System.currentTimeMillis());
            switch (typeEvenement) {
                /******************* COMMANDES GÉNÉRALES *******************/
                case "EXIT": //Ferme la connexion avec le client qui a envoyé "EXIT":
                    cnx.envoyer("END");
                    serveurBanque.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des numéros de comptes-clients connectés :
                    cnx.envoyer("LIST " + serveurBanque.list());
                    break;
                /******************* COMMANDES DE GESTION DE COMPTES *******************/
                case "NOUVEAU": //Crée un nouveau compte-client :
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("NOUVEAU NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("NOUVEAU NO");
                    }
                    else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient,nip)) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree");
                        }
                        else
                            cnx.envoyer("NOUVEAU NO ");
                    }
                    break;
                /******************* COMMANDES DE CONNEXION COMPTE EXISTANTS *******************/
                case "CONNECT":
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("CONNECT NO deja connecte");
                        break;
                    }

                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("CONNECT NO");
                        break;
                    }
                    else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        CompteClient c1 =  banque.getCompteClient(numCompteClient);
                        if(c1.getNip().equals(nip) && c1.getCompteCheque() != null){
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("CONNECT OK");
                        }
                        else{
                            cnx.envoyer("CONNECT NO");
                        }
                    }
                    break;
                /******************* COMMANDES DE CREER COMPTE EPARGNE *******************/
                case "EPARGNE": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("EPARGNE NO");
                        break;
                    }

                    String test = cnx.getNumeroCompteClient();

                    banque = serveurBanque.getBanque();
                    CompteClient test2 = banque.getCompteClient(cnx.getNumeroCompteClient());
                    CompteEpargne cpe = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne();
                    if(cpe==null){
                        String numerogenere = CompteBancaire.genereNouveauNumero();
                       while(banque.getCompteClient(numerogenere) != null){
                           numerogenere = CompteBancaire.genereNouveauNumero();
                       }
                        CompteBancaire cpe1 = new CompteEpargne(numerogenere,5);
                       boolean reponse = test2.ajouter(cpe1);
                       if(reponse){
                           cnx.envoyer("EPARGNE OK :" +numerogenere);
                       }

                    }
                    break;
             /******************* SELECT LE COMPTE CHEQUE OU EPARGNE *******************/
                case "SELECT": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("SELECT NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    if (argument.equals("CHEQUE")) {
                        cnx.setNumeroCompteActuel(banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().getNumero());
                        cnx.envoyer("CHEQUE OK");
                    }
                    else if(argument.equals("EPARGNE")) {
                        cnx.setNumeroCompteActuel(banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().getNumero());
                        cnx.envoyer("EPARGNE OK");
                    }
                    else {
                        cnx.envoyer("SELECT NO");
                    }
                    break;
                /******************* SELECT LE COMPTE CHEQUE OU EPARGNE *******************/
                case "DEPOT": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("DEPOT NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    try {
                        double montant = Integer.parseInt(argument);

                        if ((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne()).getNumero().equals(cnx.getNumeroCompteActuel())))
                        {
                            boolean depotOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().crediter(montant);
                            if(depotOuiouNon){
                                cnx.envoyer("DEPOT OK");
                            }
                            else{
                                cnx.envoyer("DEPOT NON");
                            }

                        }
                        else if((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque()).getNumero().equals(cnx.getNumeroCompteActuel()))){
                            boolean depotOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().crediter(montant);
                            if(depotOuiouNon){
                                cnx.envoyer("DEPOT OK");
                            }
                            else{
                                cnx.envoyer("DEPOT NON");
                            }
                        }
                        else {
                            cnx.envoyer("DEPOT NO");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur : l'argument fourni n'est pas un entier valide.");
                    }
                    break;

                /******************* RETIRER L'ARGENT DU COMPTE *******************/
                case "RETRAIT": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("RETRAIT NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    try {
                        double montant = Double.parseDouble(argument);

                        if ((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne()).getNumero().equals(cnx.getNumeroCompteActuel())))
                        {
                           boolean payeOuiouNon= banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().debiter(montant);

                            if (payeOuiouNon){
                                cnx.envoyer("RETRAIT OK");
                            }
                            else {
                                cnx.envoyer("RETRAIT NON a cause fonds non suffisant");
                            }
                        }
                        else if((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque()).getNumero().equals(cnx.getNumeroCompteActuel()))){
                            boolean payeOuiouNon= banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().debiter(montant);
                            if (payeOuiouNon){
                                cnx.envoyer("RETRAIT OK");
                            }
                            else {
                                cnx.envoyer("RETRAIT NON a cause fonds non suffisant");
                            }
                        }
                        else {
                            cnx.envoyer("RETRAIT NO");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur : l'argument fourni n'est pas un entier valide.");
                    }
                    break;

                /******************* FACTURE  *******************/
                case "FACTURE": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("FACTURE NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();
                    t = argument.split(" ",3);
                    if (t.length!=3) {
                        cnx.envoyer("FACTURE NO");
                        break;
                    }
                    else {
                        double montant = Double.parseDouble(t[0]);
                        String numfact = t[1];
                        String descr = t[2];

                        if ((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne()).getNumero().equals(cnx.getNumeroCompteActuel())))
                        {
                            boolean payeOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().payerFacture(numfact,montant,descr);
                            if (payeOuiouNon){
                                cnx.envoyer("FACTURE " + t[1] + " paye");
                            }
                            else {
                                cnx.envoyer("FACTURE " + t[1] + " non paye surement a cause des fonds non suffisant");
                            }
                        }
                        else if((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque() != null
                                && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque()).getNumero().equals(cnx.getNumeroCompteActuel()))){
                            boolean payeOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().payerFacture(numfact,montant,descr);
                            if (payeOuiouNon){
                                cnx.envoyer("FACTURE " + t[1] + " paye");
                            }
                            else {
                                cnx.envoyer("FACTURE " + t[1] + " non paye surement a cause des fonds non suffisant");
                            }
                        }
                        }
                    break;

                /******************* TRANSFER VERS UN AUTRE COMPTE  *******************/
                case "TRANSFER": //Crée un nouveau compte-epargne :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("TRANSFER NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();
                    t = argument.split(" ",2);
                    if (t.length!=2) {
                        cnx.envoyer("TRANSFER NO");
                        break;
                    }
                    else {
                        double montant = Double.parseDouble(t[0]);
                        String numCompte = t[1];
                        banque = serveurBanque.getBanque();
                        CompteBancaire cpt =   banque.getCompteBancaire(numCompte);
                        if(cpt != null){
                            if ((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne() != null
                                    && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne()).getNumero().equals(cnx.getNumeroCompteActuel())))
                            {

                                banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().setTransfer(cpt);
                                boolean payeOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteEpargne().transferer(montant,numCompte);
                                if (payeOuiouNon){
                                    cnx.envoyer("PAIEMENT A " + t[1] + " effectue");
                                }
                                else {
                                    cnx.envoyer("PAIEMENT A " + t[1] + " non paye surement a cause des fonds non suffisant");
                                }
                            }
                            else if((banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque() != null
                                    && (banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque()).getNumero().equals(cnx.getNumeroCompteActuel()))){
                                banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().setTransfer(cpt);
                                boolean payeOuiouNon = banque.getCompteClient(cnx.getNumeroCompteClient()).getCompteCheque().transferer(montant,numCompte);
                                if (payeOuiouNon){
                                    cnx.envoyer("PAIEMENT A " + t[1] + " paye");
                                }
                                else {
                                    cnx.envoyer("PAIEMENT A " + t[1] + " non paye surement a cause des fonds non suffisant");
                                }
                            }

                        }
                        break;
                    }


                    /******************* HISTORIQUE D'UN COMPTE  *******************/
                case "HIST": //Nous donne l'historique d'un compte :
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("HIST NO");
                        break;
                    }
                    argument = evenement.getArgument();
                    banque = serveurBanque.getBanque();
                    argument = evenement.getArgument();

                    if(cnx.getNumeroCompteActuel() != null){
                        CompteBancaire cpt = banque.getCompteBancaire(cnx.getNumeroCompteActuel());
                        PileChainee PileCompteBancaire = cpt.getHistorique();
                        cnx.envoyer("HIST : \n");
                        while (!PileCompteBancaire.estVide()) {
                            String valeur = (String) PileCompteBancaire.depiler();
                           cnx.envoyer( valeur + "\n");
                        }
                        break;
                    }

                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}