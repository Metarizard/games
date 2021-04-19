#define MAX(x, y) (((x) > (y)) ? (x) : (y))
#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define ABS(x) (((x) > (-x)) ? (x) : (-x)) 
#define N 8
#define tol 1e-8
#define VERMELL "\x1b[1;31m"
#define VERD "\x1b[1;32m"
#define GROC "\x1b[1;33m"
#define BLAU "\x1b[1;34m"
#define MAGENTA "\x1b[1;35m"
#define CIAN "\x1b[1;36m"
#define RESET "\x1b[0m"

typedef struct node{
    char tauler[N][N];
    struct node **fills;
    int n_fills;
    double valor;
} Node;

void header(void);
void columnes(void);
void horitzontal(void);
void fila(char[N][N],int,int,int[4][2]);
void taulerInicial(Node *);
void mostraTauler(Node *,int);
void copiaTauler(Node *,Node *);
double fitxesFila(Node *,int,int);
double fitxesColumna(Node *,int,int);
double fitxesDiagC(Node *,int,int);
double fitxesDiagDC(Node *,int,int);
double fheurrandom(Node *);
char quatreEnRatlla(Node *);
char tresEnRatlla(Node *);
int nombreFills(Node *);
double pot(double,int);
double fheur(Node *,int);
void Inicialitzar(Node *);
void missatgeVictoria(char);
void missatgeEmpat(void);
int demanaTirada(void);
void MenuPrincipal(int *);
void Instruccions(void);
void Dificultat(int *);
void Records(void);
int assignacio(int);
void headerMenuPrincipal(void);
void headerInstruccions(void);
void headerRecords(void);
void headerDificultat(void);
void escriuRecord(char*,double,int);
void demanaRecord(double,int);
int *columnesBuides(Node *);
void aplicaTirada(Node *,int,int);
int columnaIndex(int,Node *);
int indexColumna(int,Node *);
int missatgeInici(void);