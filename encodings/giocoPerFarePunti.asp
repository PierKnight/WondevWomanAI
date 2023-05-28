

%tile(X,Y,L).
%pawn(X,Y,0 o 1, team o enemy).

%guess



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


%check dell level up
upgradeCell(CX,CY,P) | notUpgradeCell(CX,CY,P) :-  moveToCell(X,Y,P), offsetMove(OFFX),offsetMove(OFFY),size(S), CX = X + OFFX,CY = Y + OFFY,
CX >= 0, CX < S, CY >= 0, CY < S.

% non è possibile fare più di un upgrade oppure nessuna
:- #count{X,Y,P : upgradeCell(X,Y,P)} != 1.

%non è possibile upgradare su una pedina che non è quella mossa
:- upgradeCell(X,Y,P), newPawn(X,Y,_,_).

%non è possibile upgradare una cella di livello superiore o uguale a 4
:- upgradeCell(X,Y,_), tile(X,Y,L), L >= 4.

move(P,MX,MY,UX,UY) :- moveToCell(MX,MY,P), upgradeCell(UX,UY,P).




%%% WEAK CONSTRAINTS


%%% PRIORITA' ALTA

% è preferibile spostarsi su una cella di liv 3 se questa è nel mio vicinato
:~ pawn(X1,Y1,P,team), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notMoveToCell(X2,Y2,P). [1@10]

% minimizza celle tali che liv_cella_vicina - liv_su_cui_sei_ora > 2:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,L1), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2>=L1, DIFF=L2-L1, DIFF>=2. [DIFF@9, X2,Y2]

% minimizza celle vicine di liv 4:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,4), vicine(X1,Y1,X2,Y2). [1@8, X2,Y2]

% se il nemico è su una cella di livello 2 o 3, e nel suo vicinato c'è una cella di livello 3,
% è preferibile spostarti su una cella che faccia sì che tu abbia la cella di liv 3 nel tuo vicinato
:~ pawn(X1,Y1,P,team), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, tile(X3,Y3,3), vicine(X2,Y2,X3,Y3), moveToCell(X4,Y4,P), not vicine(X4,Y4,X3,Y3). [1@7]

% è preferibile disabilitare le liv 3 se queste sono nel vicinato di un nemico e nel tuo, e lui è su una liv 2 o 3
:~ pawn(X1,Y1,_,enemy), tile(X1,Y1,L1), L1>=2, tile(X2,Y2,3), vicine(X1,Y1,X2,Y2), notUpgradeCell(X2,Y2,_). [1@6,X2,Y2]

% se stai aggiornando una cella di livello 2 (che quindi diventerà di livello 3),
% e questa è nel vicinato del nemico, e il nemico è attualmente su un liv 2 o 3: NON la aggiornare
:~ upgradeCell(X1,Y1,_), tile(X1,Y1,2), pawn(X2,Y2,_,enemy), tile(X2,Y2,L2), L2>=2, vicine(X1,Y1,X2,Y2). [1@5]





%%% PRIORITA' MEDIA



% preferisci mosse verso vicinati con più celle 3:
:~ moveToCell(X1,Y1,P), tile(X1,Y1,_), tile(X2,Y2,L2), vicine(X1,Y1,X2,Y2), L2<>3. [1@4]

% è preferibile che ti sposti sulla cella di livello max nel tuo vicinato
:~ moveToCell(X,Y,_), tile(X,Y,L), C=3-L. [C@3]

% è preferibile non aggiornare celle di livello 3 in 4
:~ upgradeCell(X,Y,_), tile(X,Y,3). [3@2]




%%% PRIORITA' BASSA



% è preferibile che aggiorni la cella di livello massimo nel tuo vicinato (valido per celle non di liv 3),
% cioè: aggiorna la cella che è esattamente del tuo livello, casomai quella che si discosta di meno dal tuo:
% 1. per celle nel vicinto che sono del tuo livello o inferiori:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L1>=L2, DIFF=L1-L2. [DIFF@1]
% 2. per celle nel vicinato ch sono superiori al tuo livello:
:~ moveToCell(X1,Y1,_), tile(X1,Y1,L1), upgradeCell(X2,Y2,_), tile(X2,Y2,L2), L2>L1, DIFF=L2-L1. [DIFF@1]
% il vincolo precedente è stato diviso in due per evitare che DIFF<0, il che sfocerebbe in costi negativi e falserebbe il risultato

