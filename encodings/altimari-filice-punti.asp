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




% ——————————————————————— WEAK CONSTRAINTS ———————————————————————


% PRIORITA' MASSIMA

% è preferibile spostarmi su una cella di liv 3 se questa è nel mio vicinato
:~ pawn(X1,Y1,P,team), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notMoveToCell(X2,Y2,P). [1@10]

% minimizza celle tali che liv_cella_vicina - liv_su_cui_sei_ora > 2:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1, DIFF>=2. [DIFF@9, X2,Y2]

% minimizza celle vicine di livello 4
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,4), vicine(X1,Y1,X2,Y2). [1@8, X2,Y2]

% se il nemico è su una cella di livello 2 o 3, e nel suo vicinato c'è una cella di livello 3,
% è preferibile spostarti su una cella che faccia sì che tu abbia la cella di liv 3 nel tuo vicinato
:~ pawn(X1,Y1,P,team), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, tile(X3,Y3,3), vicine(X2,Y2,X3,Y3), moveToCell(X4,Y4,P), not vicine(X4,Y4,X3,Y3). [1@7]

% è preferibile aggiornare le celle di livello 3 in 4 se queste sono nel vicinato di un nemico e nel tuo, e il nemico è 
% su una cella di livello 2 o superiore
:~ pawn(X1,Y1,_,enemy), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notUpgradeCell(X2,Y2,_). [1@6,X2,Y2]

% è preferibile non aggiornare una cella di livello 2 in livello 3
% se questa è nel vicinato del nemico, e il nemico è attualmente su una cella di livello 2 o 3
:~ upgradeCell(X1,Y1,_), tile(X1,Y1,2), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, vicine(X1,Y1,X2,Y2). [1@5]





%%% PRIORITA' MEDIA



% preferisci mosse verso vicinati con celle di livello 3:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2<>3. [1@4]

% è preferibile spostarsi sulla cella di livello massimo nel tuo vicinato
:~ moveToCell(X,Y,_), tile(X,Y,L), C=3-L. [C@3]

% è preferibile non aggiornare celle di livello 3 in 4
:~ upgradeCell(X,Y,_), tile(X,Y,3). [3@2]




%%% PRIORITA' BASSA


% è preferibile che aggiorni la cella di livello massimo nel tuo vicinato, casomai quella che si discosta di meno dal tuo:
% 1. per celle nel vicinto che sono del tuo livello o inferiori:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L1>=L2, DIFF=L1-L2. [DIFF@1]
% 2. per celle nel vicinato ch sono superiori al tuo livello:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L2>L1, DIFF=L2-L1. [DIFF@1]
% il vincolo precedente è stato diviso in due per evitare che DIFF<0, il che sfocerebbe in costi negativi e falserebbe il risultato

