# Alchimisti Anonimi

> **Progetto universitario** — Corso di Ingegneria del Software  
> Universita' degli Studi dell'Aquila · A.A. 2025/2026

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Architecture](https://img.shields.io/badge/Architecture-MVP-blue)
![Status](https://img.shields.io/badge/Status-In%20sviluppo-yellow)

Implementazione del gioco da tavolo **"Alchemists"** di Matúš Kotry (Czech Games Edition), realizzata come progetto d'esame per il corso di Ingegneria del Software. Il gioco si svolge via **interfaccia a riga di comando** con grafica ASCII retro-style.

---

## Anteprima

```
  ╔══════════════════════════════════════════════════════════════╗
  ║                                                              ║
  ║  ███  █      ████ █   █ █████ █   █ █████  ████ █████ █████  ║
  ║ █   █ █     █     █   █   █   ██ ██   █   █       █     █    ║
  ║ █████ █     █     █████   █   █ █ █   █    ███    █     █    ║
  ║ █   █ █     █     █   █   █   █   █   █       █   █     █    ║
  ║ █   █ █████  ████ █   █ █████ █   █ █████ ████    █   █████  ║
  ║                      · A N O N I M I ·                       ║
  ║                                                              ║
  ║               un gioco di deduzione alchemica                ║
  ║                     *  .  o  O  o  .  *                      ║
  ║                                                              ║
  ╠══════════════════════════════════════════════════════════════╣
  ║                                                              ║
  ║     [1]   NUOVA PARTITA                                      ║
  ║     [2]   COME SI GIOCA                                      ║
  ║     [3]   CREDITI                                            ║
  ║     [0]   ESCI                                               ║
  ║                                                              ║
  ╚══════════════════════════════════════════════════════════════╝
```

---

## Requisiti

| Requisito | Versione minima |
|-----------|----------------|
| Java (JDK) | 17 |

Nessun build system esterno richiesto.

---

## Come eseguire

```bash
# 1. Clona il repository
git clone https://github.com/danlerr/Alchimisti_Anonimi.git
cd Alchimisti_Anonimi

# 2. Compila i sorgenti
javac --release 17 -cp src/main/java \
      -d out \
      $(find src/main/java -name "*.java")

# 3. Avvia il gioco
java -cp out:src/main/java alchgame.Main
```

> **Windows**: sostituire `out:src/main/java` con `out;src/main/java` e usare
> `dir /s /b src\main\java\*.java` per la lista dei file.

---

## Struttura del progetto

```
src/main/java/alchgame/
│
├── Main.java                    # Entry point
├── GameBootstrapper.java        # Wiring di controller, presenter e view
├── config/                      # Configurazione partita (rounds, yields, slot)
│
├── model/                       # Dominio — logica di gioco pura
│   ├── alchemy/                 # Ingredienti, formule alchemiche, pozioni, effetti
│   ├── board/                   # Tabellone, spazi azione, slot, carte favore
│   ├── factory/                 # AlchemyFactory, BoardFactory, PlayerFactory
│   ├── game/                    # AlchGame, Round, Target, Student, fasi (State)
│   └── player/                  # Player, PrivateLaboratory, DeductionGrid, ecc.
│
├── application/                 # Casi d'uso — orchestrazione senza UI
│   ├── GameController.java      # Coordinatore centrale, avanza stati di gioco
│   ├── ExperimentController.java
│   ├── ForageController.java
│   ├── TransmuteController.java
│   ├── DeclarationController.java
│   ├── OrderController.java
│   ├── dto/                     # Data Transfer Objects (snapshot immutabili)
│   │   └── assembler/           # Conversione domain → DTO
│   └── observer/                # Interfacce Subject/Observer
│
├── presentation/                # Layer di presentazione — CLI
│   ├── GameView.java            # Unico punto di I/O: rendering ANSI + input
│   ├── GamePresenter.java       # Dispatcher principale degli eventi
│   ├── OrderPresenter.java
│   ├── DeclarationPresenter.java
│   ├── ResolutionPresenter.java
│   ├── ExperimentPresenter.java
│   ├── ForagePresenter.java
│   └── TransmutePresenter.java
│
└── service/                     # Servizi trasversali
```

---

## Architettura

Il progetto segue il pattern **MVP (Model-View-Presenter)** con una separazione netta in tre layer:

```
┌─────────────────────────────────────────────┐
│  PRESENTATION                               │
│  GameView (I/O)  ←→  Presenter*             │
└───────────────────────┬─────────────────────┘
                        │ DTO (snapshot immutabili)
┌───────────────────────▼─────────────────────┐
│  APPLICATION                                │
│  *Controller  ──►  GameController           │
│                    (observer chain)         │
└───────────────────────┬─────────────────────┘
                        │ domain objects
┌───────────────────────▼─────────────────────┐
│  MODEL                                      │
│  AlchGame · Round · Board · Player · ...    │
└─────────────────────────────────────────────┘
```

**Regole di confine:**
- `GameView` riceve solo primitivi, stringhe e DTO — nessuna dipendenza dal dominio
- I controller non conoscono la view; comunicano tramite notifiche Observer
- I DTO sono record immutabili prodotti dagli assembler al momento della notifica

---

## Pattern di design

| Pattern | Dove |
|---------|------|
| **MVP** | Separazione `GameView` / `*Presenter` / `*Controller` |
| **Observer** | `Subject<T>` / `ActionObserver` / `GameObserver` — notifiche da controller a presenter |
| **State** | `Phase` + `OrderPhase`, `DeclarationPhase`, `ResolutionPhase` — avanzamento del round |
| **Strategy** | `PotionEffectStrategy`, `FavorEffectStrategy` — effetti intercambiabili a runtime |
| **Factory** | `AlchemyFactory`, `BoardFactory`, `PlayerFactory` — costruzione del modello |
| **DTO / Assembler** | Snapshot del dominio verso il layer di presentazione |

---

## Fasi di gioco

Ogni round si articola in tre fasi:

1. **Order** — i giocatori scelgono la propria posizione sulla traccia di sveglia, determinando l'ordine di turno
2. **Declaration** — ogni giocatore dichiara (o passa) un'azione tra:
   - `forage` — raccoglie un ingrediente
   - `transmute` — trasforma un ingrediente in oro
   - `experiment` — conduce un esperimento alchemico su un bersaglio
3. **Resolution** — le azioni vengono risolte nell'ordine di sveglia; il giocatore può consultare il proprio **laboratorio privato** (`L`) in qualsiasi momento

---

## Documentazione

| Documento | Descrizione |
|-----------|-------------|
| [docs/rules.md](./docs/rules.md) | Regole del gioco originale |
| [docs/REASONING.md](./docs/REASONING.md) | Decisioni architetturali e discussioni di design |
| [docs/TODO.md](./docs/TODO.md) | Backlog e note di sviluppo |
| [Rulebook ufficiale (PDF)](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf) | Regolamento originale CGE |

---

## Obiettivi didattici

- Applicare **OOA/OOD** e **pattern GRASP** a un dominio non banale
- Tradurre **SSD** e modelli concettuali in codice Java (OOA/OOD → OOP)
- Progettare un'architettura **a strati** con separazione netta delle responsabilita'
- Sperimentare un processo **agile iterativo** su un progetto di gruppo

---

## Team

| Nome | GitHub |
|------|--------|
| **Daniele Antonucci** | [@danlerr](https://github.com/danlerr) |
| **Leonardo Pastorelli** | [@SonoLeon](https://github.com/SonoLeon) |
| **Anastasia Di Francesco** | [@AnastasiaDif](https://github.com/AnastasiaDif) |
| **Federico Palmerini** | [@federicopalme02](https://github.com/federicopalme02) |
