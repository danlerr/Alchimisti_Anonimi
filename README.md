# Alchimisti Anonimi

> **Academic project** — Software Engineering course  
> University of L'Aquila · A.Y. 2025/2026

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Architecture](https://img.shields.io/badge/Architecture-MVP-blue)
![Status](https://img.shields.io/badge/Status-In%20development-yellow)

Java implementation of the board game **"Alchemists"** by Matúš Kotry (Czech Games Edition), built as an exam project for the Software Engineering course. The game runs entirely via a **command-line interface** with retro-style ASCII graphics.

---

## Preview

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
  ║     [1]   NEW GAME                                           ║
  ║     [2]   HOW TO PLAY                                        ║
  ║     [3]   CREDITS                                            ║
  ║     [0]   EXIT                                               ║
  ║                                                              ║
  ╚══════════════════════════════════════════════════════════════╝
```

---

## Requirements

| Requirement | Minimum version |
|-------------|----------------|
| Java (JDK)  | 17             |

No external build system required.

---

## How to run

```bash
# 1. Clone the repository
git clone https://github.com/danlerr/Alchimisti_Anonimi.git
cd Alchimisti_Anonimi

# 2. Compile the sources
javac --release 17 -cp src/main/java \
      -d out \
      $(find src/main/java -name "*.java")

# 3. Start the game
java -cp out:src/main/java alchgame.Main
```

> **Windows**: replace `out:src/main/java` with `out;src/main/java` and use
> `dir /s /b src\main\java\*.java` to list source files.

---

## Project structure

```
src/main/java/alchgame/
│
├── Main.java                    # Entry point
├── GameBootstrapper.java        # Wiring of controllers, presenters and view
├── config/                      # Game configuration (rounds, yields, slots)
│
├── model/                       # Domain — pure game logic
│   ├── alchemy/                 # Ingredients, alchemic formulas, potions, effects
│   ├── board/                   # Board, action spaces, slots, favor cards
│   ├── factory/                 # AlchemyFactory, BoardFactory, PlayerFactory
│   ├── game/                    # AlchGame, Round, Target, Student, phases (State)
│   └── player/                  # Player, PrivateLaboratory, DeductionGrid, etc.
│
├── application/                 # Use cases — orchestration without UI
│   ├── GameController.java      # Central coordinator, advances game states
│   ├── ExperimentController.java
│   ├── ForageController.java
│   ├── TransmuteController.java
│   ├── DeclarationController.java
│   ├── OrderController.java
│   ├── dto/                     # Data Transfer Objects (immutable snapshots)
│   │   └── assembler/           # Domain → DTO conversion
│   └── observer/                # Subject/Observer interfaces
│
├── presentation/                # Presentation layer — CLI
│   ├── GameView.java            # Single I/O point: ANSI rendering + input
│   ├── GamePresenter.java       # Main event dispatcher
│   ├── OrderPresenter.java
│   ├── DeclarationPresenter.java
│   ├── ResolutionPresenter.java
│   ├── ExperimentPresenter.java
│   ├── ForagePresenter.java
│   └── TransmutePresenter.java
│
└── service/                     # Cross-cutting services
```

---

## Architecture

The project follows the **MVP (Model-View-Presenter)** pattern with a strict three-layer separation:

```
┌─────────────────────────────────────────────┐
│  PRESENTATION                               │
│  GameView (I/O)  ←→  Presenter*             │
└───────────────────────┬─────────────────────┘
                        │ DTOs (immutable snapshots)
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

**Boundary rules:**
- `GameView` receives only primitives, strings and DTOs — no dependency on the domain
- Controllers have no knowledge of the view; they communicate through Observer notifications
- DTOs are immutable records produced by assemblers at notification time

---

## Design patterns

| Pattern | Where |
|---------|-------|
| **MVP** | `GameView` / `*Presenter` / `*Controller` separation |
| **Observer** | `Subject<T>` / `ActionObserver` / `GameObserver` — controller-to-presenter notifications |
| **State** | `Phase` + `OrderPhase`, `DeclarationPhase`, `ResolutionPhase` — round progression |
| **Strategy** | `PotionEffectStrategy`, `FavorEffectStrategy`, `SlotRewardStrategy` — swappable effects at runtime |
| **Factory** | `AlchemyFactory`, `BoardFactory`, `PlayerFactory` — domain object construction |
| **Command** | `DelayedEffect` + `PendingEffects` — effects scheduled at creation time, deferred and drained at the start of the next round |
| **DTO / Assembler** | Immutable domain snapshots passed to the presentation layer |

---

## Game phases

Each round consists of three phases:

1. **Order** — players choose their position on the wake-up track, determining turn order
2. **Declaration** — each player declares (or passes) one action:
   - `forage` — collect an ingredient from the board
   - `transmute` — convert an ingredient into gold
   - `experiment` — conduct an alchemic experiment on a target
3. **Resolution** — actions are resolved in wake-up order; at any prompt players can press **`L`** to open their private laboratory

---

## Documentation

| Document | Description |
|----------|-------------|
| [docs/rules.md](./docs/rules.md) | Original game rules |
| [docs/REASONING.md](./docs/REASONING.md) | Architectural decisions and design discussions |
| [docs/TODO.md](./docs/TODO.md) | Backlog and development notes |
| [Official rulebook (PDF)](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf) | Original CGE rulebook |

---

## Learning objectives

- Apply **OOA/OOD** and **GRASP patterns** to a non-trivial domain
- Translate **SSDs** and conceptual models into working Java code (OOA/OOD → OOP)
- Design a clean **layered architecture** with strict separation of concerns
- Practice an **iterative agile process** in a team setting

---

## Team

| Name | GitHub |
|------|--------|
| **Daniele Antonucci** | [@danlerr](https://github.com/danlerr) |
| **Leonardo Pastorelli** | [@SonoLeon](https://github.com/SonoLeon) |
| **Anastasia Di Francesco** | [@AnastasiaDif](https://github.com/AnastasiaDif) |
| **Federico Palmerini** | [@federicopalme02](https://github.com/federicopalme02) |
