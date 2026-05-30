[ ] gestione errori nel dominio
[ ] implementare le carte Favore come meccanica completa: tipi di favore, effetti, uso da parte del giocatore e rimozione dopo l'uso
[ ] 




---
- UCS1 - giocare una partita
[x] spostare la logica di avanzamento nel dominio (adesso è in GameFlowController e nel presentation)
[ ] 
[ ] 
[ ] 

- UC09 - condurre un esperimento
[ ] rivedere i controlli 
[ ] metodi che riguardano l'aggiornamento della deductionGrid 
[ ] decidere cosa fare con getIngredients() (duplicazione con UC04)
[x] resettare lo stato dello studente: dopo una pozione negativa, lo studente diventa UNHAPPY, ma il costo dei test successivi deve valere solo nello stesso turno/round, non per tutta la partita
[x] player.removeGold(1); hardcoded 
[ ] 
[ ] 
[ ] 

- UC01 - scegliere ordine nel tracciato
[ ] splittare/valutare RoundController
[ ] 
[ ] 
[ ] 
[ ] 

- UC02 - dichiarare azioni
[x] splittare/valutare RoundController
[ ] modellare gli action space con posizioni/slot dei cubi, non solo lista di giocatori
[ ] regole specifiche per action space: forage = griglia 4x2, max 2 cubi per giocatore sulla stessa riga; transmute = colonna 1 con cubi singoli [0][1][2][3], colonna 2 con coppie di cubi [4,5][6,7][8,9][10,11]
[ ] aggiornare la risoluzione per leggere gli slot/cubi in ordine di posizione, senza raggruppare per giocatore

- UC03 - ricerca un ingrediente
[ ] OPZIONALE: aggiungere gli ingredienti sulla board (oltre a pescare dal mazzo)
[ ] 
[ ] 
[ ] 
[ ] 

- UC04 - tramutare un ingrediente
[ ] decidere cosa fare con getIngredients() (duplicazione con UC09)
[x] player.addGold(1); hardcoded 
[ ] 
[ ] 
