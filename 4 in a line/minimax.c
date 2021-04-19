/*

Marc Seguí Coll (MTZD) 

Novembre 2020 

Algorisme de minimax genèric (versió definitiva)

metarizard@gmail.com
marc.segui@e-campus.uab.cat

*/


#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <string.h>
#include "4enratlla.h"
#include "minimax.h"

//Retorna un enter aleatori entre dos nombres reals (inclou el primer)
int random(double a, double b){
    return (int)(a+b*rand()/RAND_MAX);
}

//Creem un node a partir de un pare, el nombre de fill (index)
//el nivell (jugador) què ens trobem i la profunditat de l'arbre
Node *creaNode(Node *pare,int numFill,int nivell,int profunditat){
    Node *p = calloc(1,sizeof(Node));
    copiaTauler(p,pare);
    aplicaTirada(p,numFill,nivell);
    if(nivell<profunditat){
        p->n_fills = nombreFills(p);
        p->fills = calloc(p->n_fills,sizeof(Node*));
    }
    else{
        p->n_fills = 0;
        p->fills = NULL;
    }
    return p;
}

//Creem un nivel d'un arbre
void creaNivell(Node *pare,int nivell,int profunditat){
    int i;
    for(i=0;i<pare->n_fills;i++){
        pare->fills[i] = creaNode(pare,i,nivell,profunditat);
    }
}

//Creem un arbre donat un node arrel, amb nivell inicial i final per la recursivitat
void creaArbre(Node *arrel, int nivellinicial,int nivellfinal,int profunditat){
    int i;
    if(nivellinicial<nivellfinal+1){
        creaNivell(arrel,nivellinicial,profunditat);
        for(i=0;i<arrel->n_fills;i++){
            creaArbre(arrel->fills[i],nivellinicial+1,nivellfinal,profunditat);
        }
    }
}

//Retorna el valor màxim d'una llista de nodes
double maxim(int dimensio, Node **llistaNodes){
    int i;
    if (dimensio < 1){
        printf("Error, la dimensió de la llista és inferior a 1.");
        return -666;
    }
    double valorMaxim = llistaNodes[0]->valor;
    for(i=1;i<dimensio;i++){
        valorMaxim = MAX(valorMaxim,llistaNodes[i]->valor);
    }
    return valorMaxim;
}

//Retorna el valor mínim d'una llista de nodes
double minim(int dimensio, Node **llistaNodes){
    int i;
    if (dimensio < 1){
        printf("Error, la dimensió de la llista és inferior a 1.");
        return -666;
    }
    double valorMinim = llistaNodes[0]->valor;
    for(i=1;i<dimensio;i++){
        valorMinim = MIN(valorMinim,llistaNodes[i]->valor);
    }
    return valorMinim;
}

//Algorisme del minimax pròpiament dit
void Minimax(Node *p, int nivell,int profunditat,int dificultat){
    int i;
    if(p->n_fills==0){
        p->valor = fheur(p,dificultat);
    }
    else{
        for(i=0;i<p->n_fills;i++){
            Minimax(p->fills[i],nivell-1,profunditat,dificultat);
        }
        if(nivell%2==profunditat%2){
            p->valor = maxim(p->n_fills,p->fills);
        }
        else{
            p->valor = minim(p->n_fills,p->fills);
        }
    }
}

//Algorisme del minimax amb poda alpha beta
double MinimaxAB(Node *p,double A, double B,int jugador, int dificultat){
    int i;
    double valor;
    if(p->n_fills==0){
        p->valor = fheur(p,dificultat);
        return p->valor;
    }
    else{
        if(jugador == 0){
            p->valor = -INT_MAX;
            for(i=0;i<p->n_fills;i++){
                valor = MinimaxAB(p->fills[i],A,B,1,dificultat);
                p->valor = MAX(p->valor,valor);
                A = MAX(p->valor,A);
                if(A>B){
                    break;
                }
            }
            return p->valor;
        }
        else{
            p->valor = INT_MAX;
            for(i=0;i<p->n_fills;i++){
                valor = MinimaxAB(p->fills[i],A,B,0,dificultat);
                p->valor = MIN(p->valor,valor);
                B = MIN(p->valor,B);
                if(A>B){
                    break;
                }
            }
            return p->valor;
        }
    }
}

//Retorna l'índex (fill) al que tirar aplicant el minimax
int ferMinimax(Node *p,int profunditat,int dificultat){
    int i,j = 0,tamany = 0,tirada = -666;
    int valorrand = 0;
    int *llista;
    if(p->n_fills == 0){
        printf("\nHi ha hagut algun error, l'ordinador no ha pogut efectuar la tirada.\n");
    }
    else{
        Minimax(p,profunditat,profunditat,dificultat);
        for(i=0;i<p->n_fills;i++){
            if(abs(p->valor-p->fills[i]->valor)<tol){
                tamany++;
            }
        }
        if(tamany == 0){
            printf("\nHi ha hagut algun error, no hi ha fills amb mateix valor que el node.\n");
        }
        else{
            llista = calloc(tamany,sizeof(int));
            for(i=0;i<p->n_fills;i++){
                if(abs(p->valor-p->fills[i]->valor)<tol){
                    llista[j] = i;
                    j++;
                }
            }
            valorrand = random(0,tamany);
            printf("%d",valorrand);
            tirada = llista[valorrand];
            free(llista);
        }
    }
    return tirada;
}

//Mostrem el valor d'un node per pantalla
void mostraNode(Node *p,int nivell){
    for(int i=0;i<nivell;i++){
        printf("\t");
    }
    printf("%f\n",p->valor);
}

//Recorr l'arbre recursivament (tot)
void recorreArbreRecursiu(Node *p, int nivell){
    mostraNode(p,nivell);
    if (p->n_fills>0){
        for(int i=0;i<p->n_fills;i++){
           recorreArbreRecursiu(p->fills[i],nivell+1);
        }
    }
}

//Retorna l'índex (fill) al que tirar aplicant el minimax amb poda alpha beta
int ferMinimaxAB(Node *p,int dificultat){
    int i,j = 0,tamany = 0,tirada = -666;
    int *llista;
    double valorpare;
    if(p->n_fills == 0){
        printf("\nHi ha hagut algun error, l'ordinador no ha pogut efectuar la tirada.\n");
    }
    else{
        valorpare = MinimaxAB(p,-INT_MAX,INT_MAX,0,dificultat);
        for(i=0;i<p->n_fills;i++){
            if(abs(valorpare-p->fills[i]->valor)<tol){
                tamany++;
                if(quatreEnRatlla(p->fills[i])!='0'){
                    return i;
                }
            }
        }
        if(tamany == 0){
            printf("\nHi ha hagut algun error, no hi ha fills amb mateix valor que el node.\n");
        }
        else{
            llista = calloc(tamany,sizeof(int));
            for(i=0;i<p->n_fills;i++){
                if(abs(valorpare-p->fills[i]->valor)<tol){
                    llista[j] = i;
                    j++;
                }
            }
            tirada = llista[random(0,tamany)];
            free(llista);
        }
    }
    return tirada;
}

//Esborrem un node, alliberem memòria i alliberem la memòrria
//reservada per l'apuntador fills (en cas que tingui fills)
void esborraNode(Node *p){
    free(p);
    if(p->n_fills != 0){
        free(p->fills);
    }
}

//Esborrem TOT l'arbre recursivament
void esborraArbre(Node *p){
    int i;
    if(p->n_fills == 0){
        esborraNode(p);
    }
    else{
        for(i=0;i<p->n_fills;i++){
            esborraArbre(p->fills[i]);
        }
        esborraNode(p);
    }
}

//Escanegem un enter per pantalla entre liminf i limsup
//Si l'input és incorrecte retorna error
//Si l'enter no està entre els límits establerts retorna errror
int escanejaEnter(int liminf,int limsup){
    char line[256];
    int enter;
    printf("\nIntrodueix un enter entre %d i %d i prem 'Enter/Intro': ",liminf,limsup);
    while(1){
        if(fgets(line,sizeof(line), stdin) == NULL){
            printf("\nInput invàlid, torna a provar.\n");
        }
        else{
            enter = line[0] - '0';
            if((enter>=liminf)&&(enter<=limsup)&&(strlen(line)<3)){
                break;
            }
            else{
                printf("\nEl caràcter llegit no és un enter comprès entre %d i %d, torna a provar: ",liminf,limsup);
            }
        }
    }
    return enter;
}

//Escaneig de caràcters per pantalla per escriure el record
void escanejaNom(char *nom){
    printf("\nIntrodueix el teu nom (seguit de 'Intro'). Només es llegiran els primers 7 caràcters: ");
    while(1){
        if(fgets(nom,sizeof(nom), stdin) == NULL){
            printf("\nInput invàlid, torna a provar.\n");
        }
        else{
            break;
        }
    }
}
