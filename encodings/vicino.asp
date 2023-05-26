vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1-1, Y2=Y1-1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1-1, Y2=Y1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1-1, Y2=Y1+1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1, Y2=Y1-1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1, Y2=Y1+1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1+1, Y2=Y1-1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1+1, Y2=Y1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
vicine(X1,Y1,X2,Y2) :- tile(X1,Y1,_), tile(X2,Y2,_), X2=X1+1, Y2=Y1+1, X2>=0, X2<S, Y1>=0, Y2<S, size(S).
