/*

Marc Seguí Coll (MTZD) 

Novembre 2020 

Programa de Simulació d'una partida de 4 en ratlla (versió definitiva)

metarizard@gmail.com
marc.segui@e-campus.uab.cat

Pel funcionament complet del programa 
requerim un fitxer "Records.txt" a la 
mateixa carpeta que els arxius del 
4 en ratlla

Instruccions a la consola:
c99 -o 4enratlla -g -Wall -pedantic 4enratlla.c minimax.c
*/

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include "4enratlla.h"
#include "minimax.h"

//Capçalera del tauler
void header(void){
    int i;
    printf("\n ");
    for(i=0;i<4*(N-4);i++){
        if(i==2*(N-2)-1){
            printf("TAULER DE");
        }
        else{
            printf(" ");
        }
    }
    printf(" \n");
    for(i=0;i<4*(N-3);i++){
        if(i==2*(N-2)-1){
            printf("4 EN RATLLA");
        }
        else{
            printf(" ");
        }
    }
    printf(" \n");
}

//Label de les columnes sota el tauler
void columnes(void){
    int i;
    for(i=0;i<N;i++){
        printf("  %d ",i+1);
    }
    printf("\n");
}

//Línia horitzontal per pantalla
void horitzontal(void){
    int i;
    printf("-");
    for(i=0;i<4*N;i++){
        printf("-");
    }
    printf(" \n");
}

//Potència d'un nombre a exponents enters
double pot(double base, int exponent){
    int i;
    for(i=1;i<exponent;i++){
        base *= base;
    }
    return base;
}

//Fila del tauler per pantalla
//Retorna de diferent color la darrera tirada (MAGENTA)
//Retorna de diferent color si s'assoleix un 4 en ratlla (VERD)
void fila(char tauler[N][N],int i, int tirada, int ratlla[4][2]){
    int j;
    printf("|");
    for(j=0;j<N;j++){
        if(tauler[i][j]=='C'){
            if(((i==ratlla[0][0])&&(j==ratlla[0][1]))||((i==ratlla[1][0])&&(j==ratlla[1][1]))||((i==ratlla[2][0])&&(j==ratlla[2][1]))||((i==ratlla[3][0])&&(j==ratlla[3][1]))){
                printf(VERD " %c " RESET "|",tauler[i][j]);
            }
            else{
                if(j==tirada){
                    printf(MAGENTA " %c " RESET "|",tauler[i][j]);
                }
                else{
                    printf(VERMELL " %c " RESET "|",tauler[i][j]);
                }
            }
        }
        if(tauler[i][j]=='X'){
            if(((i==ratlla[0][0])&&(j==ratlla[0][1]))||((i==ratlla[1][0])&&(j==ratlla[1][1]))||((i==ratlla[2][0])&&(j==ratlla[2][1]))||((i==ratlla[3][0])&&(j==ratlla[3][1]))){
                printf(VERD " %c " RESET "|",tauler[i][j]);
            }
            else{
                if(j==tirada){
                    printf(MAGENTA " %c " RESET "|",tauler[i][j]);
                }
                else{
                    printf(BLAU " %c " RESET "|",tauler[i][j]);
                }
            }
        }
        if(tauler[i][j]=='0'){
            printf("   |");
            //printf(" %c |",caracter[i]);
        }
    }
    printf("\n");
}

//Tauler de 0's inicial
void taulerInicial(Node *inicial){
    int i,j;
    for(i=0;i<N;i++){
        for(j=0;j<N;j++){
            inicial->tauler[i][j] = '0';
        }
    }
}

//Llegenda de colors per pantalla
void llegendaColors(void){
    printf(MAGENTA"\nX/C"RESET" -> darrera tirada\t"BLAU"X"RESET" -> Ordinador\t"VERMELL"C"RESET" -> Jugador\n");
}

//Funció per mostrar per pantalla TOT el tauler
//L'apuntador "ratlla" guarda les coordenades [i,j
//de les caselles on s'assoleix un 4 en ratlla 
void mostraTauler(Node *p,int tirada){
    int i,j;
    int ratlla[4][2];
    ratlla[0][0] = N;
    ratlla[0][1] = N;
    ratlla[1][0] = N;
    ratlla[1][1] = N;
    ratlla[2][0] = N;
    ratlla[2][1] = N;
    ratlla[3][0] = N;
    ratlla[3][1] = N;
    header();
    if(quatreEnRatlla(p)!='0'){
        for(i=0;i<N;i++){
            for(j=0;j<N;j++){
                if(p->tauler[i][j]!='0'){
                    if(fitxesColumna(p,i,j)>3){
                        ratlla[0][0] = i;
                        ratlla[0][1] = j;
                        ratlla[1][0] = i+1;
                        ratlla[1][1] = j;
                        ratlla[2][0] = i+2;
                        ratlla[2][1] = j;
                        ratlla[3][0] = i+3;
                        ratlla[3][1] = j;
                        break;
                    }
                    if(fitxesFila(p,i,j)>3){
                        ratlla[0][0] = i; 
                        ratlla[0][1] = j;
                        ratlla[1][0] = i;
                        ratlla[1][1] = j+1;
                        ratlla[2][0] = i;
                        ratlla[2][1] = j+2;
                        ratlla[3][0] = i;
                        ratlla[3][1] = j+3;
                        break;
                    }
                    if(fitxesDiagC(p,i,j)>3){
                        ratlla[0][0] = i; 
                        ratlla[0][1] = j;
                        ratlla[1][0] = i+1;
                        ratlla[1][1] = j-1;
                        ratlla[2][0] = i+2;
                        ratlla[2][1] = j-2;
                        ratlla[3][0] = i+3;
                        ratlla[3][1] = j-3;
                        break;
                    }
                    if(fitxesDiagDC(p,i,j)>3){
                        ratlla[0][0] = i;
                        ratlla[0][1] = j;
                        ratlla[1][0] = i+1;
                        ratlla[1][1] = j+1;
                        ratlla[2][0] = i+2;
                        ratlla[2][1] = j+2;
                        ratlla[3][0] = i+3;
                        ratlla[3][1] = j+3;
                        break;
                    }
                }   
            }
        }
    }
    for(i=0;i<N;i++){
        horitzontal();
        fila(p->tauler,i,tirada,ratlla);
        if(p->tauler[i][tirada]!='0'){
            tirada = N;
        }
    }
    horitzontal();
    columnes();
}

//Funció que copia el contingut del tauler d'un node 
//a l'altre passant-li el node del tauler a enganxar
//i el node del tauler a copiar 
void copiaTauler(Node *enganxa,Node *copia){
    int i,j;
    for(i=0;i<N;i++){
        for(j=0;j<N;j++){
            enganxa->tauler[i][j] = copia->tauler[i][j];
        }
    }
}

//Recompte de quantes fitxes d'un mateix jugador
//hi ha en una fila determinada 
//donat un element [fila,columna] del tauler
double fitxesFila(Node *p,int fila,int columna){
    int j;
    double rFila = 1;
    for(j=columna+1;j<N;j++){
        if(p->tauler[fila][j]==p->tauler[fila][columna]){
            rFila++;
        }
        else{
            break;
        }
    }
    return rFila;
}

//Recompte de quantes fitxes d'un mateix jugador
//hi ha en una columna determinada 
//donat un element [fila,columna] del tauler
double fitxesColumna(Node *p,int fila,int columna){
    int i;
    double rColumna = 1;
    for(i=fila+1;i<N;i++){
        if(p->tauler[i][columna]==p->tauler[fila][columna]){
            rColumna++;
        }
        else{
            break;
        }
    }
    return rColumna;
}

//Recompte de quantes fitxes d'un mateix jugador
//hi ha en una diagonal creixent determinada 
//donat un element [fila,columna] del tauler
double fitxesDiagC(Node *p,int fila,int columna){
    int i;
    double rDiagC = 1;
    for(i=1;i<MIN(N-fila,columna+1);i++){
        if(p->tauler[fila+i][columna-i]==p->tauler[fila][columna]){
            rDiagC++;
        }
        else{
            break;
        }
    }
    return rDiagC;
}

//Recompte de quantes fitxes d'un mateix jugador
//hi ha en una diagonal decreixent determinada 
//donat un element [fila,columna] del tauler
double fitxesDiagDC(Node *p,int fila,int columna){
    int i;
    double rDiagDC = 1;
    for(i=1;i<MIN(N-fila,N-columna);i++){
        if(p->tauler[fila+i][columna+i]==p->tauler[fila][columna]){
            rDiagDC++;
        }
        else{

            break;
        }
    }
    return rDiagDC;
}

//Funció heurística aleatòria
//Retorna un nombre aleatori entre 0 i 1000 (0 inclòs, 1000 no inclòs)
double fheurrandom(Node *p){
    return random(0,1000);
}

//Funció que retorna 'C' si hi ha 4 en ratlla del jugador real
//Retorna 'X' si hi ha 4 en ratlla de l'ordinador
//Retorna '0' si no hi ha cap 4 en ratlla
char quatreEnRatlla(Node *p){
    int i,j;
    for(i=0;i<N;i++){
        for(j=0;j<N;j++){
            if(p->tauler[i][j]=='X'){
                if((fitxesFila(p,i,j)>3)||(fitxesColumna(p,i,j)>3)||(fitxesDiagC(p,i,j)>3)||(fitxesDiagDC(p,i,j)>3)){
                    return 'X';
                }
            }
            if(p->tauler[i][j]=='C'){
                if((fitxesFila(p,i,j)>3)||(fitxesColumna(p,i,j)>3)||(fitxesDiagC(p,i,j)>3)||(fitxesDiagDC(p,i,j)>3)){
                    return 'C';
                }
            }
        }
    }
    return '0';
}

//Funció que determina el nombre de fills d'un node
int nombreFills(Node *p){
    int i,n = 0;
    if(quatreEnRatlla(p)=='0'){
        for(i=0;i<N;i++){
            if(p->tauler[0][i]=='0'){
                n++;
            }
        }
    }
    return n;
}

//Funció heurística que assigna un valor a un node
//donada la dificultat en la que s'està jugant
double fheur(Node *p,int dificultat){
    double recompteOrd = 0, recompteHuma = 0, recompte = 0;
    int i,j;
    if(dificultat == 0){
        return fheurrandom(p);
    }
    if(quatreEnRatlla(p)=='C'){
        return -1e+9;
    }
    if(quatreEnRatlla(p)=='X'){
        return 1e+9;
    }
    for(i=0;i<N;i++){
        for(j=0;j<N;j++){
            if(p->tauler[i][j]=='X'){
                recompteOrd += pot(fitxesFila(p,i,j),dificultat)+pot(fitxesColumna(p,i,j),dificultat)+pot(fitxesDiagC(p,i,j),dificultat)+pot(fitxesDiagDC(p,i,j),dificultat);
            }
            if(p->tauler[i][j]=='C'){
                recompteHuma += pot(fitxesFila(p,i,j),dificultat)+pot(fitxesColumna(p,i,j),dificultat)+pot(fitxesDiagC(p,i,j),dificultat)+pot(fitxesDiagDC(p,i,j),dificultat);
            }
        }
    }
    recompte += recompteOrd - recompteHuma;
    if(recompte>1e+8){
        return 1e+7;
    }
    if(recompte<-1e+8){
        return -1e+7;
    }
    return recompte;
}

//Funció que inicialitza el node arrel
void Inicialitzar(Node *arrel){
    taulerInicial(arrel);
    arrel->n_fills = nombreFills(arrel);
    arrel->fills = calloc(arrel->n_fills,sizeof(Node*));
}

//Retorna una llista amb les columnes buides del tauler d'un node
int *columnesBuides(Node *p){
    int tamany = 0, i, j = 0;
    for(i=0;i<N;i++){
        if(p->tauler[0][i] == '0'){
            tamany++;
        }
    }
    int *colbuides = calloc(tamany,sizeof(int));
    for(i=0;i<N;i++){
        if(p->tauler[0][i] == '0'){
            colbuides[j] = i;
            j++;
        }
    }
    return colbuides;
}

//Escriu en el tauler corresponent la nova tirada (índex)
//Utilitzem index degut que la columna pot no correspondre
//amb l'índex dels fills (quan hi ha columnes plenes)
void aplicaTirada(Node *p, int indexFill, int n){
    int i,columna;
    int *tirades = columnesBuides(p);
    columna = tirades[indexFill];
    free(tirades);    
    for(i=N-1;i>-1;i--){
        if(p->tauler[i][columna]=='0'){
            if(n%2==0){
                p->tauler[i][columna] = 'C';
                break;                
            }
            else{
                p->tauler[i][columna] = 'X';
                break;
            }
        }
    }
}

//Funció que retorna l'índex donada la columna
int columnaIndex(int columna, Node *p){
    int *cols = columnesBuides(p);
    int i;
    for(i=0;i<N;i++){
        if(cols[i] == columna){
            free(cols);
            return i;
        }
    }
    printf("\nError, no s'ha pogut passar de columna a índex!");
    return -666;
}

//Funció que retorna la columna donat l'índex
int indexColumna(int index, Node *p){
    int *cols = columnesBuides(p);
    int i = cols[index];
    free(cols);
    return i;
}

//Missatge per pantalla en cas de 4 en ratlla
void missatgeVictoria(char caracter){
    if(caracter == 'X'){
        printf("\nVictòria de l'ordinador! Has perdut xd.\n");
    }
    if(caracter == 'C'){
        printf("\nVictòria de l'humà! Ets millor que una màquina.\n");
    }
}

//Missatge d'empat en cas d'emplenar el tauler sense assolir cap 4 en ratlla
void missatgeEmpat(void){
    printf("\nNo s'ha assolit cap 4 en ratlla... FI DEL JOC.\n");
}

//Funció que retorna un enter demanat per pantalla (cas de la columna a tirar)
int demanaTirada(void){
    printf("\n(Prem les tecles 'Ctrl' + 'C' per sortir)\nÉs el teu torn.");
    return escanejaEnter(1,N) - 1;
}

//Llegida de records a partir de l'arxiu "Records.txt"
void Records(void){
    int c;
    FILE *file;
    file = fopen("Records.txt", "r");
    printf("\n\n"CIAN"RECORDS:"RESET"\n\n");
    if (file) {
        while ((c = getc(file)) != EOF){
            putchar(c);
        }
        fclose(file);
    }
    printf("\n\n");
}

//Menú de selecció de dificultat
//Escaneig de la dificultat seleccionada per pantalla
void Dificultat(int *dificultat){
    printf("\n\n"CIAN"SELECCIÓ DE DIFICULTAT:"RESET"\n\n");
    printf("\n\t0. Aleatori");
    printf("\n\t1. Fàcil");
    printf("\n\t2. Normal");
    printf("\n\t3. Difícil");
    printf("\n\t4. Expert");
    printf("\n\t5. Hardcore");
    printf("\n\nSelecciona nivell (quan l'introdueixis començarà la partida): ");
    *dificultat = escanejaEnter(0,5);
}

//Menú principal
//Crida als submenús en cas que es seleccionin
void MenuPrincipal(int *dificultat){
    int eleccio;
    printf("\n\n"CIAN"MENÚ PRINCIPAL:"RESET"\n\n");
    printf("\n\t1. Partida ràpida (dificultat Difícil predeterminada)");
    printf("\n\t2. Instruccions");
    printf("\n\t3. Jugar amb selecció de dificultat");
    printf("\n\t4. Veure Records (millors temps en cada dificultat)");
    printf("\n\nQuina de les 4 opcions vols?");
    eleccio = escanejaEnter(1,4);
    if(eleccio == 1){
        *dificultat = 3;
    }
    if(eleccio == 2){
        Instruccions();
        MenuPrincipal(dificultat);
    }
    if(eleccio == 3){
        Dificultat(dificultat);
    }
    if(eleccio == 4){
        Records();
        MenuPrincipal(dificultat);
    }
}

//Funció que printeja les instruccions del joc
//Explica per sobre la dificultat
void Instruccions(void){
    printf("\n\n"CIAN"INSTRUCCIONS I EXPLICACIÓ:"RESET"\n\n");
    printf("Benvolgut al 4 en ratlla dissenyat pel programador Marc Seguí Coll (MTZD)");
    printf("\nEl 4 en ratlla és un joc per torns de dos jugadors en el qual");
    printf("\ncada jugador ha d'intentar tenir 4 fitxes del seu color alineades");
    printf("\nde manera consecutiva.");
    printf("\n\nEn aquest programa simulem un tauler, on les fitxes de l'ordinador");
    printf("\nestan representades amb creus 'X' i les del jugador real per cares 'C'.");
    printf("\nMitançant comandes per pantalla el jugador real pot decidir on tirar,");
    printf("\nmentre acte seguit l'ordinador realitza la seva tirada.");
    printf("\nEls nivells de dificultat es classifiquen de la manera següent:\n");
    printf("\n\tAleatori (profunditat 1): els moviments de l'ordinador són aleatoris.");
    printf("\n\tFàcil (profunditat 2): l'ordinador considera el millor torn següent.");
    printf("\n\tNormal (profunditat 4): l'ordinador és una mica intel·ligent (pot ser quasi com tu?).");
    printf("\n\tDifícil (profunditat 6): a la mínima que et despistis, l'ordinador et guanyarà.");
    printf("\n\tExpert (profunditat 7): apte per als més atrevits, només per professionals.");
    printf("\n\tHardcore (profunditat 8): no tens cap opció contra la màquina (usa prou memòria).\n");
}

//Retorna la profunditat en funció de la dificultat
int assignacio(int dificultat){
    if(dificultat == 0){
        return 1;
    }
    if(dificultat == 1){
        return 2;
    }
    if(dificultat == 2){
        return 4;
    }
    if(dificultat == 3){
        return 6;
    }
    if(dificultat == 4){
        return 7;
    }
    if(dificultat == 5){
        return 8;
    }
    printf("\nNo s'ha pogut assignar cap dificultat.");
    return -666;
}

//Obre i escriu sobre el fitxer "Records.txt" en cas que es guanyi a la màquina
//Guarda la dificultat, el temps i el nom del jugador
void escriuRecord(char *nom,double temps, int dificultat){
    char dif[12]; 
    if(dificultat == 0){
        strcpy(dif,"Aleatori");
    }
    if(dificultat == 1){
        strcpy(dif,"Fàcil");
    }
    if(dificultat == 2){
        strcpy(dif,"Normal");
    }
    if(dificultat == 3){
        strcpy(dif,"Difícil");
    }
    if(dificultat == 4){
        strcpy(dif,"Expert");
    }
    if(dificultat == 5){
        strcpy(dif,"Hardcore");
    }
    FILE *f = fopen("Records.txt", "a");
    if (f == NULL){
        printf("No s'ha pogut obrir el fitxer !\n");
        exit(1);
    }
    fprintf(f,"\n%s\t%.0fs\t%s",nom,temps,dif);
    fclose(f);
}

//Missatge per pantalla que es presenta si el jugador guanya
//a l'ordinador. En cas afirmatiu es crida a "escriuRecord"
//per guardar el record
void demanaRecord(double temps,int dificultat){
    int resposta;
    printf("\nVols guardar el teu record? (1 per a Sí, 0 per a  No) ");
    resposta = escanejaEnter(0,1);
    if(resposta == 1){
        char nom[16];
        escanejaNom(nom);
        strtok(nom,"\n");
        escriuRecord(nom,temps,dificultat);     
    }
}

//Missatge inicial de la partida per determinar
//qui comença la partida, si l'ordinador o el jugador
//Depenent de la tria, retorna 1 ò 0
int missatgeInici(void){
    int resposta;
    printf("\n\nQui comença? (1 per a ordinador, 0 per a tu) ");
    resposta = escanejaEnter(0,1);
    return resposta;
}

int main(void){
    //Variables que requerirem en la funció main
    int columna = 0, index = 0, jugadorInicial, profunditat, dificultat;
    //Variables temporals per trackejar el temps de record:
    time_t tempsinicial, tempsfinal;
    Node *arrel = calloc(1,sizeof(Node)),*pivot;
    MenuPrincipal(&dificultat);
    //Inicialització de la seed del generador de nombres pseudoaleatoris:
    srand(time(0));
    profunditat = assignacio(dificultat);
    Inicialitzar(arrel);
    jugadorInicial = missatgeInici();
    //Si tira primer l'ordinador, per estalviar càlculs del minimax innecessaris
    //tirem de forma més o menys aleatòria el primer cop:
    if(jugadorInicial == 1){
        if(dificultat == 0){
            columna = random(0,N);
            aplicaTirada(arrel,columna,1);
        }
        else{
            if(dificultat == 1){
                columna = random(2,N-2);
                aplicaTirada(arrel,columna,1);
            }
            else{
                columna = random(N/2-1,N/2);
                aplicaTirada(arrel,columna,1);
            }
        }
    }
    mostraTauler(arrel,columna);
    llegendaColors();
    columna = demanaTirada();
    index = columnaIndex(columna,arrel);
    //Definim el temps inicial quan ja s'ha introduït el valor per pantalla de la tirada:
    tempsinicial = time(NULL);
    aplicaTirada(arrel,index,0);
    mostraTauler(arrel,columna);
    printf("\nTirada de l'ordinador:\n");
    creaArbre(arrel,1,profunditat,profunditat);
    index = ferMinimaxAB(arrel,dificultat);
    columna = indexColumna(index,arrel);
    //El node pivot anirà recollint el fill del node arrel, per poder alliberar memòria cada vegada
    pivot = creaNode(arrel,index,1,profunditat);
    //Alliberem memòria
    esborraArbre(arrel);
    mostraTauler(pivot,columna);
    llegendaColors();
    //Mentre el pivot tingui fills, seguim demanant tirada
    //El pivot no té fills si s'emplena el tauler
    while(nombreFills(pivot)>0){
        columna = demanaTirada();
        //Comprovació que la columna on es vol tirar està buida
        while(pivot->tauler[0][columna]!='0'){
            printf("\nJugada invalida! Columna plena! Tira a una altra columna.");
            columna = demanaTirada();
        }
        //Realitzem el procés anàleg amb arrel com a suport del pivot
        creaArbre(pivot,1,1,1);
        index = columnaIndex(columna,pivot);
        arrel = creaNode(pivot,index,0,profunditat);
        esborraArbre(pivot);
        mostraTauler(arrel,columna);
        //Comprovació de victòria. Estem després d'una tirada de l'humà
        //Si entrem a l'if, vol dir que ha guanyat l'humà i demanem si
        //es vol introduir record. Retornem per pantalla la duració.
        //Sortim de la funció main
        if (quatreEnRatlla(arrel)!='0'){
            tempsfinal = time(NULL);
            missatgeVictoria(quatreEnRatlla(arrel));
            printf("\nLa partida ha durat %.1f segons.\n",(double)(tempsfinal-tempsinicial));
            demanaRecord((double)(tempsfinal-tempsinicial),dificultat);
            esborraNode(arrel);
            return 0;
        }
        printf("\nTirada de l'ordinador:\n");
        creaArbre(arrel,1,profunditat,profunditat);
        index = ferMinimaxAB(arrel,dificultat);
        columna = indexColumna(index,arrel);
        pivot = creaNode(arrel,index,1,profunditat);
        esborraArbre(arrel);
        mostraTauler(pivot,columna);
        //Si entrem a l'if vol dir que ha guanyat l'ordinador.
        //Fem un print de la duració de la partida i sortim
        //de la funció main.
        if (quatreEnRatlla(pivot)!='0'){
            tempsfinal = time(NULL);
            missatgeVictoria(quatreEnRatlla(pivot));
            printf("\nLa partida ha durat %.1f segons.\n",(double)(tempsfinal-tempsinicial));
            esborraNode(pivot);
            return 0;
        }
        llegendaColors();
    }
    //Si sortim del while, la memòria reservada que queda és del pivot
    esborraArbre(pivot);
    missatgeEmpat();
    return 0;
}