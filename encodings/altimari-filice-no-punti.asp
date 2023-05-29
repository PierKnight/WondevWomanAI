% Fatti ricevuti in INPUT:

% tile(X,Y,L). -> rappresenta ogni cella di coordinate X,Y e di livello L della griglia

% pawn(X, Y, P, SIDE). -> rappresenta una pedina, in cui:
%                         - X,Y sono le coordinate in cui la pedina è posizionata
%                         - P indica il numero della pedina (0 o 1)
%                         - SIDE indica se questa è una mia pedina (team) o del nemico (enemy)

% nearMoveToPawn(NT,P,X,Y,D) -> rappresenta la prossima mossa da fare lungo il percorso minimo tra la mia pedina NT e la pedina avversaria P, in cui:
% NT = Numero pedina Team
% P = Numero pedina nemico (da raggiungere)
% X,Y = coordinate della cella in cui mi devo muovere per raggiungere il nemico lungo il cammino minimo 
% D = lunghezza del cammino minimo 


% Il programma restituisce in OUTPUT un predicato così composto:
% move(P,MX,MY,UX,UY), in cui:
% - P -> pedina che il giocatore corrente ha deciso di muovere (0 o 1)
% - MX, MY -> coordinate X,Y in cui il giocatore corrente ha deciso di spostare la pedina P
% - UX, UY -> coordinate X,Y della cella (tile) che il giocatore corrente ha deciso di aggiornare



% ————————————————— GUESS sulle mosse —————————————————

offsetMove(-1..1).
moveToCell(CX,CY,P) | notMoveToCell(CX,CY,P) :-  pawn(X,Y,P,team), offsetMove(OFFX),offsetMove(OFFY),size(S), CX = X + OFFX,CY = Y + OFFY,
CX >= 0, CX < S, CY >= 0, CY < S.


% ———————————————— CHECK sulle mosse —————————————————

% è obbligatorio fare esattamente una mossa
:- #count{X,Y,P : moveToCell(X,Y,P)} != 1.

% non è possibile effettuare una mossa in una cella in cui si trova già un'altra pedina
:- moveToCell(X,Y,_), pawn(X,Y,_,_).

% non è possibile spostarsi su una cella che abbia un livello >= 2 rispetto al livello della cella di provenienza
:- moveToCell(X,Y,P), tile(X,Y,L), pawn(PX,PY, P,team), tile(PX,PY,PL), L >= PL + 2.

% non è possibile spostarsi su una cella di livello 4
:- moveToCell(X,Y,P), tile(X,Y,4).


% newPawn aggiorna la posizione della pedina una volta che questa è stata mossa.
% è necessario poichè, quando una pedina viene spostata da una cella A ad un'altra cella B,
% la cella A farà parte del suo nuovo vicinato, e per questo rientra tra le celle che possono
% essere potenzialmente aggiornate.
% Per la mia pedina che è stata spostata:
newPawn(NX,NY,P,team) :- pawn(X,Y,P,team), moveToCell(NX,NY,P).
% Per le pedine avversarie:
newPawn(X,Y,P,enemy) :- pawn(X,Y,P,enemy).
% Per la mia pedina che non è stata spostata:
movedThisTurn(P) :- moveToCell(_,_,P).
newPawn(X,Y,P,team) :- pawn(X,Y,P,team), not movedThisTurn(P).



% ————————————————— GUESS sull'aggiornamento  —————————————————

upgradeCell(CX,CY,P) | notUpgradeCell(CX,CY,P) :-  moveToCell(X,Y,P), offsetMove(OFFX),offsetMove(OFFY),size(S), CX = X + OFFX,CY = Y + OFFY,
CX >= 0, CX < S, CY >= 0, CY < S.

% ————————————————— CHECK sull'aggiornamento  —————————————————

% è obbligatorio aggiornare esattamente una cella
:- #count{X,Y,P : upgradeCell(X,Y,P)} != 1.

% non è possibile effettuare un aggiornamento su una cella in cui giace una pedina
:- upgradeCell(X,Y,P), newPawn(X,Y,_,_).

% non è possibile aggiornare una cella di livello superiore o uguale a 4
:- upgradeCell(X,Y,_), tile(X,Y,L), L >= 4.


% ——————————————————————————— OUTPUT ————————————————————————————

move(P,MX,MY,UX,UY) :- moveToCell(MX,MY,P), upgradeCell(UX,UY,P).




% ———————————————— STRONG CONSTRAINT PER VINCERE —————————————————

% se hai una cella di livello 3 nel tuo vicinato e sei su una di liv 2 o 3, ti ci DEVI spostare
almenoUnLivello3Vicino(X1,Y1,P) :- pawn(X1,Y1,P,team), tile(X1,Y1,_), tile(X2,Y2,3), vicine(X1,Y1,X2,Y2).
:- pawn(X1,Y1,P,team), tile(X1,Y1,L1), L1>=2, almenoUnLivello3Vicino(X1,Y1,P), moveToCell(X2,Y2,P), tile(X2,Y2,L), L<>3.
:- pawn(X1,Y1,P1,team), tile(X1,Y1,L1), L1>=2, almenoUnLivello3Vicino(X1,Y1,P), moveToCell(X2,Y2,P2), tile(X2,Y2,L), P1<>P2.



% ——————————————————————— WEAK CONSTRAINTS ———————————————————————

% PRIORITA' MASSIMA

% se il nemico è su una cella di livello 2 o 3, e nel suo vicinato c'è una cella di livello 3,
% è preferibile spostarti su una cella che faccia sì che tu abbia la cella di liv 3 nel tuo vicinato
:~ pawn(X1,Y1,P,team), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, tile(X3,Y3,3), vicine(X2,Y2,X3,Y3), moveToCell(X4,Y4,P), not vicine(X4,Y4,X3,Y3). [1@11]


% è preferibile aggiornare le celle di livello 3 in 4 se queste sono nel vicinato di un nemico e nel tuo, e il nemico è 
% su una cella di livello 2 o superiore
:~ pawn(X1,Y1,_,enemy), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notUpgradeCell(X2,Y2,_). [1@10, X2,Y2]


% è preferibile non aggiornare una cella di livello 2 in livello 3
% se questa è nel vicinato del nemico, e il nemico è attualmente su una cella di livello 2 o 3
:~ upgradeCell(X1,Y1,_), tile(X1,Y1,2), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, vicine(X1,Y1,X2,Y2). [1@9]





% PRIORITA' MEDIA

%%% Strategia per andare a bloccare l'avversario:
% 1. Individua la pedina avversaria che sta sulla cella di livello maggiore
% 2. Per tale pedina, individua la cella di livello maggiore nel suo vicinato.
%   Questo implica che il nemico, nella migliore delle ipotesi, vincerà in:
%       se è su una cella di livello 0, e la sua migliore vicina è di livello 0 -> vittoria in 6 mosse
%        ''          ''              0,            ''                         1,     ''        5
%        ''          ''              1,            ''                         1,     ''        4
%        ''          ''              1,            ''                         2,     ''        3
%        ''          ''              2,            ''                         2,     ''        2
%        ''          ''              2,            ''                         3,     ''        1
% => io, per raggiungerlo, ho tempo massimo n mosse prima che lui vinca, dove n è il terzo numero delle triple di cui sopra
% 3. Effettua lo stesso ragionamento per le tue pedine
% 4. Effettua la mossa che ti avvicina al nemico se e solo se:
% 4.a. Il nemico puo' vincere in un numero di mosse minore rispetto a te (se fosse altrimenti, avrebbe più senso continuare a sviluppare il proprio gioco), e
% 4.b. il percorso minimo tra la tua pedina più vicina a quella avversaria in vantaggio è composto da n passi, con n <= numero di mosse necessarie al nemico per vincere

% per ogni SIDE (team o enemy), calcolo:
% 1. il massimo MAXL tra i livelli correnti delle proprie pedine
maxLevel(MAXL,SIDE) :- pawn(_,_,_,SIDE), #max{L,P : pawn(X,Y,P,SIDE), tile(X,Y,L)} = MAXL.
% 2.1 la pedina P che giace sul livello max MAXL in coordinate X,Y (se le due pedine giacciono su due liv differenti)
maxLevelPawn(X1,Y1,P1,MAXL,SIDE) :- pawn(X1,Y1,P1,SIDE), tile(X1,Y1,MAXL), maxLevel(MAXL,SIDE), pawn(X2,Y2,P2,SIDE), P1<>P2, tile(X2,Y2,L2), L2<>MAXL.
% 2.2 (se entrambe giacciono sullo stesso liv, prendo P1 A PRESCINDERE)
maxLevelPawn(X1,Y1,P1,MAXL,SIDE) :- pawn(X1,Y1,P1,SIDE), pawn(X2,Y2,P2,SIDE), P1<P2, tile(X1,Y1,MAXL), tile(X2,Y2,MAXL), maxLevel(MAXL,SIDE).
% 3. Il livello massimo MAX_LEVEL_NEIGH tra quelli delle celle nel vicinato della pedina P
maxLevelVicine(P,MAX_LEVEL_NEIGH,SIDE) :- maxLevelPawn(X2,Y2,P,_,SIDE), #max{L,X1,Y1 : tile(X1,Y1,L), vicine(X1,Y1,X2,Y2)} = MAX_LEVEL_NEIGH.
% 4. prendo le coordinate della cella vicina migliore di cui al punto 3
bestVicinaNonMinore(P,X2,Y2,MAXLEVEL,SIDE) :- maxLevelPawn(X1,Y1,P,_,SIDE), tile(X1,Y1,L1), maxLevelVicine(P,MAXLEVEL,SIDE), tile(X2,Y2,MAXLEVEL), vicine(X1,Y1,X2,Y2), L1<=MAXLEVEL.
% 5. quante mosse deve fare ogni SIDE al minimo per vincere (num mosse minime per vincere = 6 - liv corrente nemico messo meglio - liv best vicina nemico)
numMossePerVincere(P,NUMMOSSE,SIDE) :- maxLevelPawn(X1,Y1,P,_,SIDE), tile(X1,Y1,L1), bestVicinaNonMinore(P,X2,Y2,MAXLEVEL,SIDE), NUMMOSSE = 6 - L1 - MAXLEVEL.

% Per ogni mossa che mi avvicina al nemico NON FATTA e tale che numero mosse mancanti al nemico per vincere < numero passi cammino minimo: pago
:~ numMossePerVincere(ENEMY_PAWN, NUM_MOSSE_NEMICO, enemy), nearMoveToPawn(TEAM_PAWN, ENEMY_PAWN, X, Y, DISTANCE), NUM_MOSSE_NEMICO >= DISTANCE - 1,
maxLevelPawn(_,_,TEAM_BEST_PAWN,_,team), numMossePerVincere(TEAM_BEST_PAWN, NUM_MOSSE_MIO, team), NUM_MOSSE_NEMICO < NUM_MOSSE_MIO, notMoveToCell(X,Y,TEAM_PAWN). [1@8, X,Y]



%%% fine strategia per andare a bloccare l'avversario


% è preferibile spostarsi sulla cella di livello massimo nel tuo vicinato
:~ moveToCell(X,Y,_), tile(X,Y,L), C=3-L. [C@7]


% è preferibile che aggiorni la cella di livello massimo nel tuo vicinato, casomai quella che si discosta di meno dal tuo:
% 1. per celle nel vicinto che sono del tuo livello o inferiori:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L1>=L2, DIFF=L1-L2. [DIFF@6]
% 2. per celle nel vicinato che sono superiori al tuo livello:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L2>L1, DIFF=L2-L1. [DIFF@6]
% il vincolo precedente è stato diviso in due per evitare che DIFF<0, il che genererebbe costi negativi




% PRIORITA' BASSA

% preferisci mosse verso vicinati con celle di livello 3:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2<>3. [1@5]

% minimizza celle vicine di livello 4:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,4), vicine(X1,Y1,X2,Y2). [1@4, X2,Y2]

% minimizza celle tali che liv_cella_vicina - liv_su_cui_sei_ora > 2:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1. [DIFF@3, X2,Y2]
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1. [DIFF@3, X2,Y2]

% è preferibile non aggiornare celle di livello 3 in 4
:~ upgradeCell(X,Y,_), tile(X,Y,3). [3@1]  


