%%% FATTI DA EMBAsp:

%tile(X,Y,L).
%pawn(X,Y,0 o 1, team o enemy).


%%% GUESS

offsetMove(-1..1).
moveToCell(CX,CY,P) | notMoveToCell(CX,CY,P) :-  pawn(X,Y,P,team), offsetMove(OFFX),offsetMove(OFFY),size(S), CX = X + OFFX,CY = Y + OFFY,
CX >= 0, CX < S, CY >= 0, CY < S.


% non è possibile fare più di una move oppure nessuna
:- #count{X,Y,P : moveToCell(X,Y,P)} != 1.

%non è possibile muoversi su una pedina
:- moveToCell(X,Y,_), pawn(X,Y,_,_).

%non è possibile muoversi su una cella con livello superiore di 2
:- moveToCell(X,Y,P), tile(X,Y,L), pawn(PX,PY, P,team), tile(PX,PY,PL), L >= PL + 2.

%non è possibile muoversi su una cella con livello 4
:- moveToCell(X,Y,P), tile(X,Y,4).

%newPawn aggiorna la posizione della pedina
newPawn(NX,NY,P,team) :- pawn(X,Y,P,team), moveToCell(NX,NY,P).
newPawn(X,Y,P,enemy) :- pawn(X,Y,P,enemy).
movedThisTurn(P) :- moveToCell(_,_,P).
newPawn(X,Y,P,team) :- pawn(X,Y,P,team), not movedThisTurn(P).

% check del level up
upgradeCell(CX,CY,P) | notUpgradeCell(CX,CY,P) :-  moveToCell(X,Y,P), offsetMove(OFFX),offsetMove(OFFY),size(S), CX = X + OFFX,CY = Y + OFFY,
CX >= 0, CX < S, CY >= 0, CY < S.

% non è possibile fare più di un upgrade oppure nessuna
:- #count{X,Y,P : upgradeCell(X,Y,P)} != 1.

% non è possibile upgradare su una pedina che non è quella mossa
:- upgradeCell(X,Y,P), newPawn(X,Y,_,_).

% non è possibile upgradare una cella di livello superiore o uguale a 4
:- upgradeCell(X,Y,_), tile(X,Y,L), L >= 4.

move(P,MX,MY,UX,UY) :- moveToCell(MX,MY,P), upgradeCell(UX,UY,P).



%%% STRONG CONSTRAINT PER VINCERE

% se hai una cella di livello 3 nel tuo vicinato e sei su una di liv 2 o 3, ti ci DEVI spostare
almenoUnLivello3Vicino(X1,Y1,P) :- pawn(X1,Y1,P,team), tile(X1,Y1,_), tile(X2,Y2,3), vicine(X1,Y1,X2,Y2).
:- pawn(X1,Y1,P,team), tile(X1,Y1,L1), L1>=2, almenoUnLivello3Vicino(X1,Y1,P), moveToCell(X2,Y2,P), tile(X2,Y2,L), L<>3.
:- pawn(X1,Y1,P1,team), tile(X1,Y1,L1), L1>=2, almenoUnLivello3Vicino(X1,Y1,P), moveToCell(X2,Y2,P2), tile(X2,Y2,L), P1<>P2.



%%% WEAK CONSTRAINT

%%% PRIORITA' MASSIMA

% se il nemico è su una cella di livello 2 o 3, e nel suo vicinato c'è una cella di livello 3,
% è preferibile spostarti su una cella che faccia sì che tu abbia la cella di liv 3 nel tuo vicinato
:~ pawn(X1,Y1,P,team), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, tile(X3,Y3,3), vicine(X2,Y2,X3,Y3), moveToCell(X4,Y4,P), not vicine(X4,Y4,X3,Y3). [1@11]


% è preferibile disabilitare le liv 3 se queste sono nel vicinato di un nemico e nel tuo, e lui è su una liv 2 o 3
:~ pawn(X1,Y1,_,enemy), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notUpgradeCell(X2,Y2,_). [1@10, X2,Y2]


% se stai aggiornando una cella di livello 2 (che quindi diventerà di livello 3),
% e questa è nel vicinato del nemico, e il nemico è attualmente su un liv 2 o 3: NON la aggiornare
:~ upgradeCell(X1,Y1,_), tile(X1,Y1,2), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, vicine(X1,Y1,X2,Y2). [1@9]





%%% PRIORITA' MEDIA

%%%%%%%% PER ANDARE A BLOCCARE IL NEMICO %%%%%%%%

% nearMoveToPawn(NT,P,X,Y,D):
% NT= Numero pedina Team
% P = Numero pedina nemico (da raggiungere)
% X,Y = coordinate della cella in cui mi devo muovere per raggiungere il nemico tramite il cammino minimo calcolato da A*
% D = distanza (lunghezza) del cammino minimo calcolato da A*

%maxLevelEnemy(MAXL) :- #max{L,P : pawn(X,Y,P,enemy), tile(X,Y,L)} = MAXL.
%maxLevelPawn(P, MAXL) :- pawn(X,Y,P,enemy), tile(X,Y,MAXL), maxLevelEnemy(MAXL).
%nearMoveToPawnW(NT,X,Y,MAXL) :- nearMoveToPawn(NT,P,X,Y,D), maxLevelPawn(P,MAXL).
%:~ moveToCell(X,Y,NT), not nearMoveToPawnW(NT,X,Y,MAXL), COST = MAXL + 5,maxLevelEnemy(MAXL). [COST@2]
%:~ moveToCell(X,Y,NT), nearMoveToPawn(NT,P,X,Y,DISTANCE), maxLevelPawn(P, _). [DISTANCE@2]

% prendo il liv della cella corrente del nemico
% prendo il liv della cella migliore vicina al nemico, a patto che questa sia almeno di liv pari a quella del nemico
% il nemico, nella migliore delle ipotesi, vincerà in:
% parto da 0, best vicina 0 = vittoria in 6 mosse
% 0, 1, 5
% 1, 1, 4
% 1, 2, 3
% 2, 2, 2
% 2, 3, 1
% => io, per raggiungerlo, ho tempo max n mosse prima che lui vinca, dove n è il terzo numero delle triple di cui sopra

% 1. Per la pedina nemica messa meglio (cioè quella che, delle due, è sul livello maggiore), calcolo il max livello nel proprio vicinato:
% 1.1. il massimo MAXL tra i livelli correnti delle pedine nemiche:
maxLevel(MAXL,SIDE) :- pawn(_,_,_,SIDE), #max{L,P : pawn(X,Y,P,SIDE), tile(X,Y,L)} = MAXL.
% 1.2.1. la pedina nemica P che giace sul livello max MAXL in coordinate X,Y (se le due pedine giacciono su due liv differenti):
maxLevelPawn(X1,Y1,P1,MAXL,SIDE) :- pawn(X1,Y1,P1,SIDE), tile(X1,Y1,MAXL), maxLevel(MAXL,SIDE), pawn(X2,Y2,P2,SIDE), P1<>P2, tile(X2,Y2,L2), L2<>MAXL.
% 1.2.2. (se entrambe giacciono sullo stesso liv, prendo P1 A PRESCINDERE):
maxLevelPawn(X1,Y1,P1,MAXL,SIDE) :- pawn(X1,Y1,P1,SIDE), pawn(X2,Y2,P2,SIDE), P1<P2, tile(X1,Y1,MAXL), tile(X2,Y2,MAXL), maxLevel(MAXL,SIDE).
% 1.3. la pedina nemica P ha il liv MAX_LEVEL_NEIGH come livello max nel suo vicinato
maxLevelVicine(P,MAX_LEVEL_NEIGH,SIDE) :- maxLevelPawn(X2,Y2,P,_,SIDE), #max{L,X1,Y1 : tile(X1,Y1,L), vicine(X1,Y1,X2,Y2)} = MAX_LEVEL_NEIGH.



% 2. Per la pedina nemica migliore (quella con liv corrente maggiore), prendo le coordinate della vicina migliore (con liv maggiore)
% se questa è di livello almeno pari a quello corrente del nemico
bestVicinaNonMinore(P,X2,Y2,MAXLEVEL,SIDE) :- maxLevelPawn(X1,Y1,P,_,SIDE), tile(X1,Y1,L1), maxLevelVicine(P,MAXLEVEL,SIDE), tile(X2,Y2,MAXLEVEL), vicine(X1,Y1,X2,Y2), L1<=MAXLEVEL.

% 3. dopodichè, per la pedina nemica messa meglio (maxLevelEnemyPawn), calcolo quante mosse deve fare
% al minimo per vincere (num mosse minime per vincere = 6 - liv corrente nemico messo meglio - liv best vicina nemico)
numMossePerVincere(P,NUMMOSSE,SIDE) :- maxLevelPawn(X1,Y1,P,_,SIDE), tile(X1,Y1,L1), bestVicinaNonMinore(P,X2,Y2,MAXLEVEL,SIDE), NUMMOSSE = 6 - L1 - MAXLEVEL.

% 4. se il numero di mosse che manca alla pedina migliore del nemico è < del numero di passi del mio cammino minimo per raggiungerlo,
% e se il numero di mosse che manca a me per vincere è > del numero di mosse che manca al nemico per vincere (=> lui vince prima di me) -> mi avvicino; altrimenti no
% Per ogni mossa che mi avvicina al nemico NON FATTA e tale che numero mosse mancanti al nemico per vincere < numero passi cammino minimo: pago
% versione 1: (non considera mio stato)
%:~ numMosseNemicoPerVincere(ENEMY_PAWN, NUMMOSSE), nearMoveToPawn(TEAM_PAWN, ENEMY_PAWN, X, Y, DISTANCE), NUMMOSSE < DISTANCE, not moveToCell(X,Y,TEAM_PAWN). [1@8, X,Y]
% versione 2: (considera mio stato)
:~ numMossePerVincere(ENEMY_PAWN, NUM_MOSSE_NEMICO, enemy), nearMoveToPawn(TEAM_PAWN, ENEMY_PAWN, X, Y, DISTANCE), NUM_MOSSE_NEMICO >= DISTANCE - 1,
maxLevelPawn(_,_,TEAM_BEST_PAWN,_,team), numMossePerVincere(TEAM_BEST_PAWN, NUM_MOSSE_MIO, team), NUM_MOSSE_NEMICO < NUM_MOSSE_MIO, notMoveToCell(X,Y,TEAM_PAWN). [1@8, X,Y]







%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



% è preferibile che ti sposti sulla cella di livello max nel tuo vicinato
:~ moveToCell(X,Y,_), tile(X,Y,L), C=3-L. [C@7]

% è preferibile che aggiorni la cella di livello massimo nel tuo vicinato (valido per celle non di liv 3)
% Aggiorna la cella che è esattamente del tuo livello, casomai quella che si discosta di meno dal tuo:
% 1. per celle nel vicinto che sono del tuo livello o inferiori:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L1>=L2, DIFF=L1-L2. [DIFF@6]
% 2. per celle nel vicinato ch sono superiori al tuo livello:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L2>L1, DIFF=L2-L1. [DIFF@6]
% il vincolo precedente è stato diviso in due per evitare che DIFF<0, il che sfocerebbe in costi negativi e falserebbe il risultato




%%% PRIORITA' BASSA

% preferisci mosse verso vicinati con più celle 3:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2<>3. [1@5]


% minimizza celle vicine di liv 4:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,4), vicine(X1,Y1,X2,Y2). [1@4, X2,Y2]

% minimizza celle tali che liv_cella_vicina - liv_su_cui_sei_ora > 2:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1. [DIFF@3, X2,Y2]
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1. [DIFF@3, X2,Y2]

% è preferibile non aggiornare celle di livello 3 in 4
:~ upgradeCell(X,Y,_), tile(X,Y,3). [3@1]   % NB: costo deve essere 3 perchè altrimenti questo vincolo non viene preso abbastanza in cosiderazione


