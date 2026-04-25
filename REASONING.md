discussioni e decisioni 

---
*Topic*: SD inizia esperimento, meglio mettere una classe façade (façade controller) che conosce il Player corrente nel turno e crea l'esperimento passando target e player oppure avere un handler di sessione del caso d'uso che conosce il player attivo e quindi può creare direttamente un'istanza di esperimento. 
*conclusione*: uc controller. 
*motivazione*: utilizzando la classe façade potrebbe portare alta responsabilità (god class), considerando che la classe dovrebbe gestire le altre azioni di gioco. 

---

*Topic*: organizzazione iterazione 2
Abbiamo deciso di fare prima il turno (UC01), successivamente volevamo parallelamente implementare UC02, UC03, UC04 (casi d'uso meno corposi rispetto a quelli fatti precedentemente). 
*Idea*: Dopo aver implementato UC01, allineare l'architettura ai principi e pattern di design per poi valutarla con la 
successiva implmentazione di UC02, UC03, UC04. 
*Obiettivo*: valutare il refactoring dello snapshot del progetto a metà iterazione 2. 


