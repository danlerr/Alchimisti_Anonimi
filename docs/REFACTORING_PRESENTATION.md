# Refactoring del Presentation Layer

## Contesto

Il progetto è strutturato su tre layer:

```
presentation  →  application  →  domain
```

La regola fondamentale è che le dipendenze vanno **solo verso il basso**: il presentation conosce l'application, l'application conosce il dominio, ma mai il contrario.

Il layer presentation attuale funziona, ma viola questa regola in più punti: importa direttamente classi del dominio (`Player`, `Board`, `Ingredient`, `AlchemicFormula`, ecc.) e usa oggetti del dominio per costruire ciò che la view deve mostrare.

Questo documento descrive come portare il presentation layer a **completa indipendenza** dal dominio, e spiega il ragionamento dietro ogni scelta.

---

## Il problema: dipendenze dirette dal dominio

### Dove si trovano attualmente

| Classe (presentation) | Oggetti dominio importati |
|---|---|
| `OrderPhasePresenter` | `Board`, `Player` |
| `DeclarationPhasePresenter` | `Board`, `Player` |
| `ResolutionPhasePresenter` | `Player` |
| `ExperimentActionPresenter` | `Player`, `Ingredient`, `AlchemicFormula`, `Atom`, `Color`, `Potion`, `DeductionGrid`, `Size`, `Sign` |
| `TransmuteActionPresenter` | `Player`, `Ingredient` |
| `ForageActionPresenter` | `Player` (inutilizzato) |
| `SetupPresenter` | `AlchGame` |

### Perché è un problema

1. **Fragilità**: se una classe del dominio cambia (es. `Player` rinominata, metodo spostato), i presenter si rompono anche se la logica di gioco non li riguarda.
2. **Testabilità**: per testare un presenter in isolamento bisogna istanziare l'intero dominio (`AlchGame`, `Board`, `Round`, ecc.).
3. **Sostituibilità**: aggiungere una GUI grafica richiede riscrivere codice che naviga il dominio, invece di lavorare solo con dati già pronti.
4. **Duplicazione**: `OrderPhasePresenter` e `DeclarationPhasePresenter` contengono due metodi privati identici (`orderSlots()`, `declarantsByAction()`) perché entrambi devono interrogare `Board`.

---

## Soluzione: due strumenti

### Strumento 1 — DTO (Data Transfer Object)

Un DTO è un oggetto senza logica, usato esclusivamente per trasportare dati tra layer. Il layer application converte gli oggetti del dominio in DTO **prima** di passarli al presentation. Il presentation riceve solo DTO: dati già pronti, nessun import del dominio.

**Esempio — `Player` → `PlayerDTO`**

```java
// DOMINIO (non toccare)
public class Player {
    public String getName() { ... }
    public int getGold() { ... }
    public List<Ingredient> getIngredientsFromLab() { ... }
    // ... altri 20 metodi che il presenter non deve vedere
}

// DTO (nuovo, in application/dto/)
public record PlayerDTO(
    String name,
    int gold,
    int reputation,
    int actionCubes,
    List<IngredientDTO> ingredients,
    List<String> favors
) {}
```

Il presenter riceve `PlayerDTO` e chiama `player.name()`, `player.gold()` — non sa che `Player` esiste.

La conversione avviene **una volta sola**, nel layer application, in `GameController`:

```java
private PlayerDTO toPlayerDTO(Player player) {
    if (player == null) return null;
    return new PlayerDTO(
        player.getName(),
        player.getGold(),
        player.getReputation(),
        player.getActionCubes(),
        player.getIngredientsFromLab().stream()
            .map(i -> new IngredientDTO(i.getId(), i.getName()))
            .toList(),
        player.getFavorCards().stream()
            .map(f -> f.getName())
            .toList()
    );
}
```

### Strumento 2 — Interfacce sui controller

Attualmente il presentation importa i controller come classi concrete:

```java
// presenter
import alchgame.application.OrderController;

private final OrderController orderController;  // classe concreta
```

Con un'interfaccia:

```java
// presenter
import alchgame.application.IOrderController;

private final IOrderController orderController;  // astrazione
```

Il presenter non sa più che `OrderController` estende `Subject`, ha un costruttore con `Board` e `Round`, o qualsiasi altro dettaglio implementativo. Dipende solo dal contratto definito dall'interfaccia.

**Questo è il Dependency Inversion Principle (DIP)**: i moduli di alto livello (presentation) non dipendono dai moduli di basso livello (implementazioni concrete), ma entrambi dipendono da astrazioni.

**Beneficio immediato — testabilità**:

```java
// test di OrderPhasePresenter senza istanziare niente del dominio
IOrderController mockController = new IOrderController() {
    public List<String> getAvailableSlots() { return List.of("slot1", "slot2"); }
    public SlotResultDTO chooseSlot(String id) { return new SlotResultDTO(1, 0); }
};
OrderPhasePresenter presenter = new OrderPhasePresenter(mockController, mockView);
// testa il presenter in isolamento
```

---

## Dove vivono le nuove classi

Le interfacce e i DTO appartengono al **layer application**, non al presentation. La logica è: l'application definisce il contratto ("ecco cosa ti offro"), il presentation lo usa.

```
alchgame/
├── application/
│   ├── dto/                        ← NUOVO
│   │   ├── PlayerDTO.java
│   │   ├── IngredientDTO.java
│   │   ├── PotionDTO.java
│   │   ├── SlotResultDTO.java
│   │   ├── DeductionGridDTO.java
│   │   └── BoardStateDTO.java
│   ├── IStartGameController.java   ← NUOVO
│   ├── IOrderController.java       ← NUOVO
│   ├── IDeclarationController.java ← NUOVO
│   ├── IForageController.java      ← NUOVO
│   ├── ITransmuteController.java   ← NUOVO
│   ├── IExperimentController.java  ← NUOVO
│   ├── StartGameController.java    (implements IStartGameController)
│   ├── OrderController.java        (implements IOrderController)
│   └── ...
└── presentation/
    └── ...                         ← zero import dal dominio
```

---

## Elenco completo delle modifiche

### Nuovi file — `application/dto/`

#### `IngredientDTO.java`
```java
public record IngredientDTO(String id, String name) {}
```

#### `PlayerDTO.java`
```java
public record PlayerDTO(
    String name,
    int gold,
    int reputation,
    int actionCubes,
    List<IngredientDTO> ingredients,
    List<String> favors
) {}
```

#### `PotionDTO.java`
```java
public record PotionDTO(String label, String colorName) {}
```

#### `SlotResultDTO.java`
Sostituisce `Resources` (dominio) come valore di ritorno di `chooseSlot()`.
```java
public record SlotResultDTO(int ingredientCount, int favorCount) {}
```

#### `DeductionGridDTO.java`
Le `alchemicLabels` sono già formattate come stringhe leggibili — la logica di formattazione (`formatFormula`, `colorChar`, ecc.) si sposta da `ExperimentActionPresenter` a `ExperimentController.getDeductionGrid()`.
```java
public record DeductionGridDTO(
    List<String> ingredientNames,
    List<String> alchemicLabels,
    boolean[][] excluded
) {}
```

#### `BoardStateDTO.java`
Elimina `Board` dai presenter. Viene incluso direttamente nel `GameStateDTO` così ogni evento porta già lo stato del tabellone aggiornato.
```java
public record BoardStateDTO(
    Map<String, String> orderSlots,
    List<String> wakeUpOrder,
    List<String> actionIds,
    Map<String, List<String>> declarantsByAction
) {}
```

---

### Nuovi file — interfacce in `application/`

#### `IForageController.java`
```java
public interface IForageController {
    void forageIngredient();
}
```

#### `ITransmuteController.java`
Aggiunge `getPlayerIngredients()` — il presenter non accede più a `Player` per leggere il laboratorio.
```java
public interface ITransmuteController {
    List<IngredientDTO> getPlayerIngredients();
    int transmuteIngredient(String ingredientId);
}
```

#### `IOrderController.java`
```java
public interface IOrderController {
    List<String> getAvailableSlots();
    SlotResultDTO chooseSlot(String slotId);
}
```

#### `IDeclarationController.java`
```java
public interface IDeclarationController {
    List<String> getActionList();
    void declareAction(String actionId);
    void endDeclaration();
}
```

#### `IStartGameController.java`
```java
public interface IStartGameController {
    void setPlayerNumber(int n);
    void setPlayerName(String name);
    void startGame();
    boolean needsMorePlayerNames();
    int getInsertedPlayersCount();
}
```

#### `IExperimentController.java`
Il controller più ricco: espone ingredienti e griglia di deduzione senza mai passare oggetti del dominio.
```java
public interface IExperimentController {
    Set<String> getTargetIds();
    boolean paymentCheck(String targetId);
    int payGold(String targetId);
    List<IngredientDTO> getPlayerIngredients();
    PotionDTO conductExperiment(String targetId, String i1Id, String i2Id);
    DeductionGridDTO getDeductionGrid();
    void updateDeductionGrid(String ingredientId, int alchemicIndex);
}
```

---

### File modificati — `application/`

#### `GameStateDTO`
```java
// PRIMA
public record GameStateDTO(
    EventType type, PhaseType phaseType,
    Player currentPlayer,       // ← dominio
    String currentActionId,
    int roundNumber,
    List<Player> finalRanking   // ← dominio
) { ... }

// DOPO
public record GameStateDTO(
    EventType type, PhaseType phaseType,
    PlayerDTO currentPlayer,    // ← DTO
    String currentActionId,
    int roundNumber,
    List<PlayerDTO> finalRanking, // ← DTO
    BoardStateDTO boardState      // ← nuovo: incluso nell'evento
) { ... }
```

#### `GameController`
Aggiunge `Board` come dipendenza del costruttore (già disponibile nel contesto di assembly) e due metodi privati di conversione. La logica di gioco non cambia.

```java
// costruttore
public GameController(AlchGame alchgame, Board board) { ... }

// conversione Player → PlayerDTO
private PlayerDTO toPlayerDTO(Player player) {
    if (player == null) return null;
    return new PlayerDTO(
        player.getName(), player.getGold(), player.getReputation(),
        player.getActionCubes(),
        player.getIngredientsFromLab().stream()
            .map(i -> new IngredientDTO(i.getId(), i.getName())).toList(),
        player.getFavorCards().stream().map(f -> f.getName()).toList()
    );
}

// costruzione BoardStateDTO
private BoardStateDTO buildBoardState() {
    Map<String, String> slots = new LinkedHashMap<>();
    board.getOrderAssignments()
         .forEach((slot, p) -> slots.put(slot, p != null ? p.getName() : null));
    List<String> wakeUp = board.getWakeUpOrder().stream()
                               .map(Player::getName).toList();
    List<String> actionIds = board.getActionSpaceIds();
    Map<String, List<String>> declarants = new LinkedHashMap<>();
    for (String id : actionIds)
        declarants.put(id, board.getActionSpace(id).getDeclaredPlayers()
            .stream().map(Player::getName).toList());
    return new BoardStateDTO(slots, wakeUp, actionIds, declarants);
}
```

Tutti i punti in cui `GameController` crea un `GameStateDTO` usano `toPlayerDTO()` e `buildBoardState()`.

#### `ExperimentController`
Implementa `IExperimentController`. Le modifiche principali:

- `conductExperiment()` converte `Potion` → `PotionDTO` prima di restituire
- `updateDeductionGrid()` accetta `String ingredientId, int alchemicIndex` invece di oggetti dominio — recupera l'oggetto internamente
- Aggiunge `getPlayerIngredients()` e `getDeductionGrid()` — la logica di formattazione delle formule alchemiche si sposta qui da `ExperimentActionPresenter`

```java
@Override
public PotionDTO conductExperiment(String targetId, String i1Id, String i2Id) {
    // ...stessa logica invariata...
    String label     = potion.isNeutral() ? "NEUTRA" : potion.getColor().name() + " " + potion.getSign().name();
    String colorName = potion.isNeutral() ? "NEUTRAL" : potion.getColor().name();
    return new PotionDTO(label, colorName);
}

@Override
public void updateDeductionGrid(String ingredientId, int alchemicIndex) {
    Player player = game.getCurrentRound().getCurrentPlayer();
    Ingredient ingredient = player.findIngredientById(ingredientId);
    AlchemicFormula formula = player.getDeductionGrid().getAlchemics().get(alchemicIndex);
    player.excludeFromDeductionGrid(ingredient, formula);
}

@Override
public List<IngredientDTO> getPlayerIngredients() {
    return game.getCurrentRound().getCurrentPlayer()
        .getIngredientsFromLab().stream()
        .map(i -> new IngredientDTO(i.getId(), i.getName()))
        .toList();
}

@Override
public DeductionGridDTO getDeductionGrid() {
    DeductionGrid grid = game.getCurrentRound().getCurrentPlayer().getDeductionGrid();
    List<Ingredient> ings = grid.getIngredients();
    List<AlchemicFormula> alcs = grid.getAlchemics();
    boolean[][] excluded = new boolean[alcs.size()][ings.size()];
    for (int a = 0; a < alcs.size(); a++)
        for (int i = 0; i < ings.size(); i++)
            excluded[a][i] = grid.isExcluded(ings.get(i), alcs.get(a));
    List<String> labels = new ArrayList<>();
    for (int a = 0; a < alcs.size(); a++)
        labels.add("  [" + (a+1) + "]  " + formatFormula(alcs.get(a)));
    return new DeductionGridDTO(
        ings.stream().map(Ingredient::getName).toList(),
        labels, excluded
    );
}
```

#### `OrderController`
```java
// PRIMA
public Resources chooseSlot(String orderSlotId) { ... return res; }

// DOPO (implements IOrderController)
@Override
public SlotResultDTO chooseSlot(String orderSlotId) {
    // ...stessa logica...
    return new SlotResultDTO(res.ingredientCount(), res.favorCount());
}
```

#### `TransmuteController`
Aggiunge `getPlayerIngredients()` e implementa `ITransmuteController`. La logica di `transmuteIngredient()` non cambia.

#### `StartGameController`, `DeclarationController`, `ForageController`
Solo aggiungono `implements I<Nome>`. Zero modifiche alla logica.

---

### File modificati — `presentation/`

#### `ActionDispatcher`
La firma cambia da `Consumer<Player>` a `Runnable`. I presenter non ricevono più `Player` — ognuno chiede al proprio controller ciò di cui ha bisogno.

```java
// PRIMA
public class ActionDispatcher {
    private final Map<String, Consumer<Player>> handlers;
    public void dispatch(String actionId, Player player) {
        Consumer<Player> h = handlers.get(actionId);
        if (h != null) h.accept(player);
    }
}

// DOPO
public class ActionDispatcher {
    private final Map<String, Runnable> handlers;
    public void dispatch(String actionId) {
        Runnable h = handlers.get(actionId);
        if (h != null) h.run();
    }
}
```

Registrazione nella factory/assembly:
```java
new ActionDispatcher(Map.of(
    "forage",     () -> foragePresenter.run(),
    "transmute",  () -> transmutePresenter.run(),
    "experiment", () -> experimentPresenter.run()
))
```

#### `SetupPresenter`
Sostituisce `AlchGame` con due interi.
```java
// PRIMA
public SetupPresenter(StartGameController c, AlchGame alchGame, GameView view)

// DOPO
public SetupPresenter(IStartGameController c, int minPlayers, int maxPlayers, GameView view)
```

#### `OrderPhasePresenter`
Rimuove `Board`. Il tabellone arriva da `state.boardState()`. Usa `IOrderController`.
```java
// PRIMA
public void handleTurn(GameStateDTO state) {
    Player player = state.currentPlayer();
    view.showBoard(orderSlots(), board.getActionSpaceIds(), declarantsByAction());
    view.showCurrentPlayer(player.getName());
    // ...
    Resources res = orderController.chooseSlot(slotId);
    view.showSlotChoiceResult(slotId, res.ingredientCount(), res.favorCount());
}

// DOPO
public void handleTurn(GameStateDTO state) {
    PlayerDTO player = state.currentPlayer();
    BoardStateDTO board = state.boardState();
    view.showBoard(board.orderSlots(), board.actionIds(), board.declarantsByAction());
    view.showCurrentPlayer(player.name());
    // ...
    SlotResultDTO res = orderController.chooseSlot(slotId);
    view.showSlotChoiceResult(slotId, res.ingredientCount(), res.favorCount());
}
```

`showPhaseEnd()` usa `state.boardState().wakeUpOrder()` invece di `board.getWakeUpOrder()`.

#### `DeclarationPhasePresenter`
Stesso trattamento di `OrderPhasePresenter`: rimuove `Board`, usa `state.boardState()`, usa `IDeclarationController`.

#### `ResolutionPhasePresenter`
`Player` → `PlayerDTO`. `dispatcher.dispatch(actionId)` senza player.
```java
// PRIMA
public void handleTurn(GameStateDTO state) {
    Player player = state.currentPlayer();
    dispatcher.dispatch(actionId, player);
    view.showIngredients(player.getIngredientsFromLab().stream()
        .map(i -> i.getName()).toList());
}

// DOPO
public void handleTurn(GameStateDTO state) {
    PlayerDTO player = state.currentPlayer();
    dispatcher.dispatch(actionId);
    view.showIngredients(player.ingredients().stream()
        .map(IngredientDTO::name).toList());
}
```

#### `ForageActionPresenter`
Rimuove il parametro `Player` inutilizzato. Usa `IForageController`.
```java
// PRIMA
public void run(Player player) { ... }  // player non veniva mai usato

// DOPO
public void run() { ... }
```

#### `TransmuteActionPresenter`
Zero import del dominio. Gli ingredienti arrivano dal controller.
```java
// PRIMA
public void run(Player player) {
    List<Ingredient> ingredients = player.getIngredientsFromLab();  // dominio
    // ...
}

// DOPO
public void run() {
    List<IngredientDTO> ingredients = transmuteController.getPlayerIngredients();
    view.showIngredients(ingredients.stream().map(IngredientDTO::name).toList());
    int choice = view.promptIngredientChoice("Scegli ingrediente", ingredients.size());
    String ingredientId = ingredients.get(choice - 1).id();
    int updatedGold = transmuteController.transmuteIngredient(ingredientId);
    view.showTransmutationResult(updatedGold);
}
```

#### `ExperimentActionPresenter`
Il cambiamento più visibile: da 8 import del dominio a zero.
```java
// PRIMA: importa Player, Ingredient, AlchemicFormula, Atom, Color, Potion, DeductionGrid, Size, Sign
public void run(Player player) {
    List<Ingredient> ingredients = player.getIngredientsFromLab();
    // ...
    Potion potion = experimentController.conductExperiment(targetId, i1Id, i2Id);
    String label = potion.isNeutral() ? "NEUTRA" : potion.getColor().name() + ...
    // ...
    DeductionGrid grid = player.getDeductionGrid();
    // loop su AlchemicFormula, Atom, Color, Size, Sign...
}

// DOPO: zero import del dominio
public void run() {
    List<String> targetIds = new ArrayList<>(experimentController.getTargetIds());
    // ...
    List<IngredientDTO> ingredients = experimentController.getPlayerIngredients();
    // ...
    PotionDTO potion = experimentController.conductExperiment(targetId, i1Id, i2Id);
    view.showPotionResult(potion.label(), potion.colorName());

    if (view.promptUpdateDeductionGrid()) {
        DeductionGridDTO grid = experimentController.getDeductionGrid();
        view.showDeductionGrid(grid.ingredientNames(), grid.alchemicLabels(), grid.excluded());
        int ingChoice = view.promptDeductionIngredientChoice(grid.ingredientNames().size());
        int alcChoice = view.promptDeductionAlchemicChoice(grid.alchemicLabels().size());
        experimentController.updateDeductionGrid(
            grid.ingredientNames().get(ingChoice - 1),  // passa l'id, non l'oggetto
            alcChoice - 1
        );
    }
}
```

---

## Riepilogo tabellare

| File | Operazione | Motivo |
|---|---|---|
| `dto/IngredientDTO` | nuovo | trasporta id+nome senza Ingredient |
| `dto/PlayerDTO` | nuovo | trasporta stato giocatore senza Player |
| `dto/PotionDTO` | nuovo | trasporta risultato esperimento senza Potion |
| `dto/SlotResultDTO` | nuovo | sostituisce Resources (dominio) |
| `dto/DeductionGridDTO` | nuovo | griglia già formattata per la view |
| `dto/BoardStateDTO` | nuovo | stato tabellone senza Board |
| `IStartGameController` | nuovo | interfaccia per DIP |
| `IOrderController` | nuovo | interfaccia per DIP |
| `IDeclarationController` | nuovo | interfaccia per DIP |
| `IForageController` | nuovo | interfaccia per DIP |
| `ITransmuteController` | nuovo | interfaccia per DIP |
| `IExperimentController` | nuovo | interfaccia per DIP |
| `GameStateDTO` | modifica | Player → PlayerDTO, aggiunge BoardStateDTO |
| `GameController` | modifica | aggiunge conversioni toPlayerDTO + buildBoardState |
| `ExperimentController` | modifica | ritorna DTO, getDeductionGrid, getPlayerIngredients, updateDeductionGrid con primitivi |
| `OrderController` | modifica | chooseSlot ritorna SlotResultDTO |
| `TransmuteController` | modifica | aggiunge getPlayerIngredients |
| `StartGameController` | modifica | implements IStartGameController |
| `DeclarationController` | modifica | implements IDeclarationController |
| `ForageController` | modifica | implements IForageController |
| `ActionDispatcher` | modifica | Consumer<Player> → Runnable |
| `SetupPresenter` | modifica | AlchGame → int min, int max |
| `OrderPhasePresenter` | modifica | Board rimosso, usa state.boardState(), IOrderController |
| `DeclarationPhasePresenter` | modifica | Board rimosso, usa state.boardState(), IDeclarationController |
| `ResolutionPhasePresenter` | modifica | Player → PlayerDTO, dispatch senza player |
| `ForageActionPresenter` | modifica | Player inutilizzato rimosso, IForageController |
| `TransmuteActionPresenter` | modifica | zero import dominio, ingredienti dal controller |
| `ExperimentActionPresenter` | modifica | zero import dominio, tutto da IExperimentController |

---

## Risultato finale

```
┌─────────────────────────────────────────────────────┐
│                   PRESENTATION                      │
│  conosce: DTOs, interfacce I*Controller             │
│  non conosce: Player, Board, Ingredient, Potion...  │
└──────────────────────┬──────────────────────────────┘
                       │ dipende da
┌──────────────────────▼──────────────────────────────┐
│                   APPLICATION                       │
│  definisce: interfacce I*Controller, DTOs           │
│  implementa: *Controller (logica use case)          │
│  converte: dominio → DTO al confine del layer       │
└──────────────────────┬──────────────────────────────┘
                       │ dipende da
┌──────────────────────▼──────────────────────────────┐
│                     DOMAIN                          │
│  Player, Board, AlchGame, Ingredient...             │
│  nessuna dipendenza verso l'alto                    │
└─────────────────────────────────────────────────────┘
```

La freccia va solo verso il basso. Il presentation non sa che il dominio esiste.
